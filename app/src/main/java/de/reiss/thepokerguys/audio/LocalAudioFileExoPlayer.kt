package de.reiss.thepokerguys.audio

import android.content.Context
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ExoPlayer.STATE_BUFFERING
import com.google.android.exoplayer2.ExoPlayer.STATE_READY
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor
import com.google.android.exoplayer2.extractor.mp4.Mp4Extractor
import com.google.android.exoplayer2.extractor.ogg.OggExtractor
import com.google.android.exoplayer2.extractor.wav.WavExtractor
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.FileDataSourceFactory
import de.reiss.thepokerguys.util.logError
import java.io.File

class LocalAudioFileExoPlayer(var context: Context,
                              var eventListener: AudioFilePlayerEventListener) : AudioFilePlayer {

    private var player: SimpleExoPlayer =
            ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector(), DefaultLoadControl())

    private var currentFilePath: String? = null

    init {
        player.addListener(object : ExoPlayer.EventListener {

            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                logError(error?.cause) {
                    "Error playing file"
                }
                eventListener.onPlayError()
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState != STATE_BUFFERING) {
                    currentFilePath?.let {
                        eventListener.onPlaybackChanged(
                                filePath = it,
                                isNowPlaying = playWhenReady && playbackState == ExoPlayer.STATE_READY)
                    }
                }
            }

            override fun onLoadingChanged(isLoading: Boolean) {
            }

            override fun onPositionDiscontinuity() {
            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
            }

        })
    }

    override fun play(filePath: String, progress: Int?, duration: Int?, speed: Float?) {
        currentFilePath = filePath

        player.prepare(buildMediaSource(filePath))

        if (progress != null && duration != null) {
            if (progress == duration) {
                player.seekTo(0)
            } else {
                player.seekTo(progress.toLong())
            }
        }

        if (speed != null) {
            changeSpeed(speed)
        }

        @Suppress("UsePropertyAccessSyntax")
        player.setPlayWhenReady(true)
    }

    override fun pause(filePath: String) {
        if (filePath == currentFilePath) {
            @Suppress("UsePropertyAccessSyntax")
            player.setPlayWhenReady(false)
        }
    }

    override fun isPlaying(filePath: String): Boolean {
        return isSetToFile(filePath) && player.playWhenReady && player.playbackState == STATE_READY
    }

    override fun isSetToFile(filePath: String): Boolean {
        return filePath == currentFilePath
    }

    override fun shutdown() {
        player.stop()
        player.release()
        currentFilePath = null
    }

    override fun seekTo(position: Int) {
        player.currentPosition
        player.seekTo(position.toLong())
    }

    override fun seekRelative(toCurrentPosition: Int) {
        val newPosition = player.currentPosition + toCurrentPosition
        player.seekTo(sanitizedPosition(newPosition))
    }

    private fun sanitizedPosition(position: Long): Long {
        return when {
            position < 0 -> 0
            position > player.duration -> player.duration
            else -> position
        }
    }

    override fun currentProgress(): Int {
        return player.currentPosition.toInt()
    }

    override fun changeSpeed(speed: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val playbackParams = PlaybackParams()
            playbackParams.speed = speed
            player.playbackParams = playbackParams
        }
    }

    private fun buildMediaSource(filePath: String): MediaSource {
        val uri = Uri.fromFile(File(filePath))
        if (uri != null) {
            return ExtractorMediaSource(uri, FileDataSourceFactory(), extractorsFactory, null, null)
        }
        throw IllegalStateException("Unable to build media source")
    }

    private val extractorsFactory = ExtractorsFactory {
        arrayOf(OggExtractor(),
                WavExtractor(),
                Mp3Extractor(),
                Mp4Extractor())
    }

}