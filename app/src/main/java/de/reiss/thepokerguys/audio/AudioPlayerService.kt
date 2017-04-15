package de.reiss.thepokerguys.audio

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.AudioManager.*
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import de.reiss.thepokerguys.App
import de.reiss.thepokerguys.R
import de.reiss.thepokerguys.database.PodcastDatabaseItem
import de.reiss.thepokerguys.eventbus.AppEventBus
import de.reiss.thepokerguys.eventbus.AudioFilePlaybackChanged
import de.reiss.thepokerguys.eventbus.FileDeleted
import de.reiss.thepokerguys.eventbus.Mp3FileProgress
import de.reiss.thepokerguys.util.findDurationFromFileMetaData
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class AudioPlayerService : Service() {

    companion object {

        var instance: AudioPlayerService? = null

    }

    private val executor = Executors.newScheduledThreadPool(1)

    private lateinit var audioManager: AudioManager

    private lateinit var playable: AudioFilePlayer

    private var currentPodcast: PodcastDatabaseItem? = null

    private var doRefreshProgressAutomatically = true

    private val playerEventListener = object : AudioFilePlayerEventListener {

        override fun onPlaybackChanged(filePath: String, isNowPlaying: Boolean) {
            AppEventBus.post(AudioFilePlaybackChanged(filePath))
            updateNotification()
        }

        override fun onPlayError() {
            Toast.makeText(this@AudioPlayerService, R.string.error_playing_file, Toast.LENGTH_SHORT).show()
            stopSelf()
        }

    }

    private val focusChangeListener = OnAudioFocusChangeListener { focusChange ->

        if (appSettings().isNoisyAware()
                && (focusChange == AUDIOFOCUS_LOSS || focusChange == AUDIOFOCUS_LOSS_TRANSIENT)) {

            pauseIfCurrentlyPlaying()
        }

    }

    private val becomingNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (appSettings().isNoisyAware()
                    && AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent?.action) {

                pauseIfCurrentlyPlaying()
            }
        }
    }

    private val localBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        val service: AudioPlayerService
            get() = this@AudioPlayerService
    }

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        registerReceiver(becomingNoisyReceiver, IntentFilter(ACTION_AUDIO_BECOMING_NOISY))

        playable = LocalAudioFileExoPlayer(this, playerEventListener)
    }

    override fun onBind(intent: Intent): IBinder? {
        currentPodcast?.let { playOrPausePodcast(it) }

        executor.scheduleWithFixedDelay({
            if (doRefreshProgressAutomatically) {
                saveProgressPersistently()
            }
        }, 0, 1000, TimeUnit.MILLISECONDS)

        executor.scheduleWithFixedDelay({
            if (doRefreshProgressAutomatically) {
                reportCurrentProgress()
            }
        }, 500, 1000, TimeUnit.MILLISECONDS)

        AppEventBus.register(this)

        return localBinder
    }

    override fun onUnbind(intent: Intent): Boolean {
        AppEventBus.unregister(this)

        audioManager.abandonAudioFocus(focusChangeListener)
        unregisterReceiver(becomingNoisyReceiver)

        executor.shutdown()

        currentPodcast = null
        playable.shutdown()

        return false
    }

    @Subscribe
    fun onEvent(event: FileDeleted) {
        currentPodcast?.let {
            if (event.filePath == it.filePath && isPlaying(it.filePath)) {
                currentPodcast = null
                playable.shutdown()
                updateNotification()
            }
        }
    }

    fun isSetToFile(absolutePath: String?): Boolean {
        return absolutePath == currentPodcast?.filePath
    }

    fun pauseIfCurrentlyPlaying() {
        currentPodcast?.let {
            if (isPlaying(it.filePath)) {
                playable.pause(it.filePath)
            }
        }
    }

    fun playOrPausePodcast(databaseItem: PodcastDatabaseItem? = currentPodcast) {
        if (databaseItem == null) {
            return
        }
        val filePath = databaseItem.filePath
        val file = File(filePath)
        if (!file.exists()) {
            return
        }

        if (databaseItem.filePath != currentPodcast?.filePath) {
            currentPodcast = databaseItem
        }

        storeDurationIfNecessary(file)

        currentPodcast?.let {
            if (playable.isPlaying(it.filePath)) {
                doPause(it)
            } else {
                doPlay(it)
            }
        }
    }

    /**
     * Since the duration can not be established without having the file downloaded first,
     * the duration needs to be retrieved and stored at the latest before playing the downloaded file
     */
    private fun storeDurationIfNecessary(file: File) {
        currentPodcast?.let {
            if (it.duration == 0) {
                val duration = findDurationFromFileMetaData(this, file.absolutePath)

                dbProxy().updatePodcastDuration(it.url, duration)

                it.duration = duration
            }
        }
    }

    private fun doPause(it: PodcastDatabaseItem) {
        playable.pause(it.filePath)
    }

    private fun doPlay(item: PodcastDatabaseItem) {
        audioManager.requestAudioFocus(
                focusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN).let {

            if (it == AUDIOFOCUS_REQUEST_GRANTED) {
                playable.play(item.filePath, item.progress, item.duration, item.speed)
            }
        }
    }

    private fun updateNotification() {
        currentPodcast.let { databaseItem ->
            if (databaseItem == null) {
                cancelPausedNotification(this)
                stopForeground(true)
            } else {
                if (isPlayingCurrent()) {
                    startAudioForegroundNotification(this, databaseItem)
                } else {
                    stopForeground(false)
                    showAudioPausedNotification(this, databaseItem)
                }
            }
        }
    }

    fun changeSpeed(speed: Float) {
        playable.changeSpeed(speed)
        saveSpeedPersistently(speed)
    }

    fun seekTo(progress: Int) {
        doRefreshProgressAutomatically = false
        playable.seekTo(progress)
        saveProgressPersistently()
        reportCurrentProgress()
        doRefreshProgressAutomatically = true
    }

    fun goBack10Seconds() {
        seekRelative(-10000)
    }

    fun goForward10Seconds() {
        seekRelative(10000)
    }

    private fun seekRelative(toCurrentPosition: Int) {
        doRefreshProgressAutomatically = false
        playable.seekRelative(toCurrentPosition)
        saveProgressPersistently()
        reportCurrentProgress()
        doRefreshProgressAutomatically = true
    }

    private fun reportCurrentProgress() {
        currentPodcast?.let {
            if (playable.isPlaying(it.filePath)) {
                AppEventBus.post(Mp3FileProgress(it.url, it.filePath, it.progress))
            }
        }
    }

    private fun saveProgressPersistently() {
        currentPodcast?.let { currentPodcast ->

            if (playable.isSetToFile(currentPodcast.filePath)) {
                currentPodcast.progress = playable.currentProgress()
                dbProxy().insertOrUpdatePodcast(podcastItem = currentPodcast, keepCurrentPlayProgress = false)
            }
        }
    }

    private fun saveSpeedPersistently(speed: Float) {
        currentPodcast?.let {
            if (playable.isSetToFile(it.filePath)) {
                it.speed = speed
                dbProxy().insertOrUpdatePodcast(podcastItem = it, keepCurrentPlayProgress = true)
            }
        }
    }

    fun isPlayingCurrent(): Boolean {
        return currentPodcast?.let { isPlaying(it.filePath) } ?: false
    }

    fun isSetToSomeExistingFile(): Boolean {
        return currentPodcast?.let { isSetToFile(it.filePath) && File(it.filePath).exists() } ?: false
    }

    fun isPlaying(filePath: String): Boolean {
        return playable.isPlaying(filePath)
    }

    private fun appSettings() = (this.applicationContext as App).appSingletons.appSettings()

    private fun dbProxy() = (this.applicationContext as App).appSingletons.dbProxy()

}
