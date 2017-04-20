package net.thepokerguys.play

import net.thepokerguys.database.PodcastDatabaseItem
import net.thepokerguys.util.MVP

interface Play {

    class Model : MVP.Model {

        var isLoading: Boolean = false
        var errorLoading: Boolean = false

        var isPodcastPlaying: Boolean = false
        var isPodcastCurrentlyBeingDownloaded: Boolean = false
        var isPodcastFullyDownloaded: Boolean = false

        var podcast: PodcastDatabaseItem? = null

    }

    interface View : MVP.View {

        fun setMVPModel(model: Play.Model)

        fun setAudioProgress(progressInMs: Int, maxInMs: Int)

        fun selectSpeedButton(speed: Float)

    }

    interface Presenter : MVP.Presenter {

        override val mvpView: Play.View?

        override val mvpModel: Play.Model

        fun cancelDownload()
        fun startDownload(downloadAlreadyConfirmed: Boolean = false)

        fun playOrPause()

        fun changeProgress(progress: Int)
        fun goBack10Seconds()
        fun goForward10Seconds()

        fun setSpeed(speed: Float)

        fun showShareDialog()
    }

}
