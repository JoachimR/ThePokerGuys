package de.reiss.thepokerguys.play

import android.os.Bundle
import android.support.v4.app.ShareCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import de.reiss.thepokerguys.App
import de.reiss.thepokerguys.AppRxFragment
import de.reiss.thepokerguys.R
import de.reiss.thepokerguys.audio.AudioPlayerService
import de.reiss.thepokerguys.audio.AudioPlayerService.Companion.instance
import de.reiss.thepokerguys.database.PodcastDatabaseItem
import de.reiss.thepokerguys.download.ConfirmDownloadDialog
import de.reiss.thepokerguys.eventbus.*
import de.reiss.thepokerguys.util.RxUtil
import de.reiss.thepokerguys.util.isConnectedToWiFi
import de.reiss.thepokerguys.util.logWarn
import de.reiss.thepokerguys.util.showDialogFragment
import org.greenrobot.eventbus.Subscribe
import rx.Subscription
import java.io.File

class PlayPresenter : AppRxFragment(), Play.Presenter {

    companion object {

        private val KEY_PODCAST_URL = "KEY_PODCAST_URL"

        fun createInstance(url: String): PlayPresenter {
            val playPresenter = PlayPresenter()
            val arguments = Bundle()
            arguments.putString(KEY_PODCAST_URL, url)
            playPresenter.arguments = arguments
            return playPresenter
        }

    }

    private var findInDatabaseSubscription: Subscription? = null

    private val model = Play.Model()
    private val podcast: PodcastDatabaseItem?
        get() {
            return model.podcast
        }

    override val mvpView: Play.View?
        get() {
            return activity as Play.View?
        }

    override val mvpModel: Play.Model
        get() = model

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setPodcastFromDatabase(arguments.getString(KEY_PODCAST_URL))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    fun setPodcastFromDatabase(url: String) {
        findInDatabaseSubscription?.let {
            if (!it.isUnsubscribed) return
        }

        model.isLoading = true
        model.errorLoading = false

        findInDatabaseSubscription = dbProxy().loadPodcastDatabaseItemObservable(url)
                .compose(RxUtil.ioMain<PodcastDatabaseItem?>())
                .doAfterTerminate {
                    model.isLoading = false
                    refreshModelInView()
                }
                .subscribe({ result ->
                    model.podcast = result
                }, { error ->
                    logWarn(error) { "Could not find podcast in database successfully" }
                    model.errorLoading = true
                })
    }

    override fun onStart() {
        super.onStart()
        AppEventBus.register(this)

        if (AudioPlayerService.instance == null) {
            (activity.application as App).bindToAudioPlayService()
        }

        refreshModelInView()
    }

    override fun onStop() {
        AppEventBus.unregister(this)
        super.onStop()
    }

    override fun goBack10Seconds() {
        podcast?.let { podcast ->

            AudioPlayerService.instance?.apply {
                if (isSetToFile(podcast.filePath)) {
                    goBack10Seconds()
                }
            }
        }
    }

    override fun goForward10Seconds() {
        podcast?.let { podcast ->

            AudioPlayerService.instance?.apply {
                if (isSetToFile(podcast.filePath)) {
                    goForward10Seconds()
                }
            }
        }
    }

    override fun playOrPause() {
        if (checkIfFileExists()) {
            AudioPlayerService.instance?.playOrPausePodcast(podcast)
        }
    }

    private fun checkIfFileExists(): Boolean {
        podcast?.let { podcast ->

            if (File(podcast.filePath).exists()) {
                return true
            }
        }

        Toast.makeText(context, R.string.file_not_found, Toast.LENGTH_SHORT).show()
        return false
    }

    override fun startDownload(downloadAlreadyConfirmed: Boolean) {
        podcast?.let { podcast ->

            if (downloadAlreadyConfirmed
                    || appSettings().shouldWarnWhenNoWifi().not()
                    || isConnectedToWiFi(context)) {
                downloadManagerHelper().startNewDownloadRequest(podcast.url)
            } else {
                activity?.showDialogFragment(ConfirmDownloadDialog.createInstance(
                        url = podcast.url, fileName = podcast.title))
            }
        }
    }

    override fun cancelDownload() {
        podcast?.let { podcast ->

            downloadManagerHelper().cancelDownloadRequest(podcast.url)
        }
    }

    override fun showShareDialog() {
        podcast?.let { podcast ->

            activity?.let { activity ->

                ShareCompat.IntentBuilder
                        .from(activity)
                        .setText(getString(R.string.share_podcast_message,
                                podcast.title, podcast.url))
                        .setType("text/plain")
                        .setChooserTitle(R.string.share_podcast_title)
                        .startChooser()
            }
        }
    }

    override fun setSpeed(speed: Float) {
        podcast?.let { podcast ->

            if (speed != podcast.speed) {
                podcast.speed = speed
                dbProxy().insertOrUpdatePodcast(podcastItem = podcast, keepCurrentPlayProgress = true)

                AudioPlayerService.instance?.apply {
                    if (isSetToFile(podcast.filePath)) {
                        instance?.changeSpeed(speed)
                    }
                }
            }
        }
    }

    override fun changeProgress(progress: Int) {
        podcast?.let { podcast ->

            if (progress != podcast.progress) {
                podcast.progress = progress
                dbProxy().insertOrUpdatePodcast(
                        podcastItem = podcast,
                        keepCurrentPlayProgress = false)

                AudioPlayerService.instance?.apply {
                    if (isSetToFile(podcast.filePath)) {
                        seekTo(progress)
                    }
                }

                refreshModelInView()
            }
        }
    }

    @Subscribe
    fun onEvent(event: AudioFilePlaybackChanged) {
        podcast?.let { podcast ->

            if (event.filePath == podcast.filePath) {
                refreshModelInView()
            }
        }
    }

    @Subscribe
    fun onEvent(event: DownloadConfirmed) {
        podcast?.let { podcast ->

            if (event.url == podcast.url) {
                startDownload(downloadAlreadyConfirmed = true)
            }
        }
    }

    @Subscribe
    fun onEvent(event: Mp3FileProgress) {
        podcast?.let { podcast ->

            if (podcast.filePath == event.filePath) {
                podcast.progress = event.progressInMs
                refreshModelInView()
            }
        }
    }

    @Subscribe
    fun onEvent(event: DownloadStarted) {
        refreshModelInView()
    }

    @Subscribe
    fun onEvent(event: DownloadStopped) {
        refreshModelInView()
    }

    private fun refreshModelInView() {
        refreshFileState()
        mvpView?.setMVPModel(model)
    }

    private fun refreshFileState() {
        podcast?.let { podcast ->

            model.isPodcastPlaying = AudioPlayerService.instance?.isPlaying(podcast.filePath) ?: false
            model.isPodcastCurrentlyBeingDownloaded = downloadManagerHelper().isCurrentlyDownloading(podcast.url)
            model.isPodcastFullyDownloaded = downloadManagerHelper().isAlreadyDownloaded(podcast.url)
        }
    }

}
