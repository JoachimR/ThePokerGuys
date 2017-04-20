package net.thepokerguys.list

import net.thepokerguys.util.MVP
import java.util.*

interface PodcastList {

    class Model : MVP.Model {

        var isReloadingList: Boolean = false
        var isDownloadingList: Boolean = false

        var isSetToSomeFile: Boolean = false
        var isCurrentlyPlaying: Boolean = false

        var errorDownloadingRss: Boolean = false
        var errorReloadingList: Boolean = false

        var listItems = ArrayList<ListItem>()

    }

    interface View : MVP.View {

        fun setMVPModel(model: PodcastList.Model)

        fun updatePlayingInfo(url: String, currentPlayTime: Int, isSetToFile: Boolean)

        fun showUndoDeleteSnackBar(originalTitle: String, originalFilePath: String)

    }

    interface Presenter : MVP.Presenter {

        fun downloadRssList()

        override val mvpView: PodcastList.View?

        override val mvpModel: PodcastList.Model
    }

}
