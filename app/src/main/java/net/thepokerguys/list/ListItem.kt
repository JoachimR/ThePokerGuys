package net.thepokerguys.list

import net.thepokerguys.util.view.recycler.HasId
import java.util.*

data class ListItem(val title: String,
                    val description: String,
                    val url: String,
                    val currentPlayTime: Int,
                    val maxPlayTime: Int,
                    val publishedDate: Date,
                    val isFullyDownloaded: Boolean,
                    val isCurrentlyDownloading: Boolean,
                    val downloadProgress: Int = -1,
                    val isPlayerSetToFile: Boolean,
                    val wasRecentlyPublished: Boolean) : HasId {

    override val itemId: Long
        get() = hashCode().toLong()

}