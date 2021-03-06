package net.thepokerguys.list

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import net.thepokerguys.AppActivity
import net.thepokerguys.R
import net.thepokerguys.download.ConfirmDownloadDialog
import net.thepokerguys.eventbus.AppEventBus
import net.thepokerguys.eventbus.DownloadConfirmed
import net.thepokerguys.play.PlayActivity
import net.thepokerguys.util.*

class ListItemViewHolder(itemView: View, var appActivity: AppActivity) : RecyclerView.ViewHolder(itemView) {

    private val colorSetToFile = ContextCompat.getColor(appActivity, R.color.color_primary_opacity_127)
    private val colorFullyDownloaded = ContextCompat.getColor(appActivity, R.color.color_primary_opacity_10)
    private val colorNotDownloaded = ContextCompat.getColor(appActivity, R.color.md_white_1000)

    private var listItem: ListItem? = null

    var title = itemView.findViewById<TextView>(R.id.podcast_list_item_title)
    var publishDate = itemView.findViewById<TextView>(R.id.podcast_list_item_publish_date)
    var newPodcastIcon = itemView.findViewById<ImageView>(R.id.podcast_list_item_image_new_icon)
    var icon = itemView.findViewById<ImageView>(R.id.podcast_list_item_image_download_icon)

    var root = itemView.findViewById<View>(R.id.podcast_list_item_root)
    var contentRoot = itemView.findViewById<View>(R.id.podcast_list_item_content_root)

    var timeRoot = itemView.findViewById<View>(R.id.podcast_list_item_time_root)
    var leftTime = itemView.findViewById<TextView>(R.id.podcast_list_item_left_time)
    var rightTime = itemView.findViewById<TextView>(R.id.podcast_list_item_right_time)
    var progressBar = itemView.findViewById<ProgressBar>(R.id.podcast_list_item_progress_bar)

    var divider: View = itemView.findViewById(R.id.podcast_list_item_bottom_divider)

    init {
        contentRoot.setOnClickListener {
            listItem?.let {
                startPlayActivity(it)
            }
        }

        icon.setOnClickListener {
            onIconClick()
        }

        progressBar.progressDrawable.setColorFilter(
                ContextCompat.getColor(appActivity, R.color.color_primary_dark),
                android.graphics.PorterDuff.Mode.MULTIPLY)
    }

    private fun onIconClick() {
        listItem?.let { item ->
            when {
                item.isFullyDownloaded ->
                    deleteFile(item)
                item.isCurrentlyDownloading ->
                    cancelDownload(item)
                else ->
                    startDownload(item)
            }
        }
    }

    private fun startDownload(item: ListItem) {
        if (appActivity.appSettings().shouldWarnWhenNoWifi().not() || isConnectedToWiFi(appActivity)) {
            AppEventBus.post(DownloadConfirmed(item.url))
        } else {
            appActivity.showDialogFragment(ConfirmDownloadDialog.createInstance(item.url, item.title))
        }
    }

    private fun cancelDownload(item: ListItem) {
        appActivity.downloadManagerHelper().cancelDownloadRequest(item.url)
    }

    private fun deleteFile(item: ListItem) {
        appActivity.deleteFileHelper().deleteAudioFile(item.url, item.title)
    }

    private fun startPlayActivity(item: ListItem) {
        val dbItem = appActivity.dbProxy().findByMp3Url(item.url)
        if (dbItem == null) {
            Toast.makeText(appActivity, R.string.podcast_info_missing, Toast.LENGTH_SHORT).show()
        } else {
            appActivity.startActivity(PlayActivity.createIntent(appActivity, dbItem.url))
        }
    }

    fun bindItem(item: ListItem, isLastItem: Boolean) {
        this.listItem = item

        title.text = item.title
        publishDate.text = item.publishedDate.asFormattedDate(appActivity)

        bindItemIcons(item)
        bindItemTime(item)

        root.setBackgroundColor(when {
            item.isPlayerSetToFile -> colorSetToFile
            item.isFullyDownloaded -> colorFullyDownloaded
            else -> colorNotDownloaded
        })

        divider.visibility = isLastItem.trueAsGone()
    }

    private fun bindItemIcons(item: ListItem) {
        if (item.isFullyDownloaded) {
            icon.setImageResource(R.drawable.ic_delete_black_24dp)
            newPodcastIcon.visibility = GONE
        } else {
            icon.setImageResource(
                    when {
                        item.isCurrentlyDownloading && item.downloadProgress > -1 -> {
                            R.drawable.ic_cancel_black_24dp
                        }
                        else -> R.drawable.ic_file_download_black_24dp
                    })
            newPodcastIcon.visibility = item.wasRecentlyPublished.trueAsVisible()
        }
    }

    private fun bindItemTime(item: ListItem) {
        timeRoot.visibility = VISIBLE

        val progress = if (item.maxPlayTime <= 0) 0 else item.currentPlayTime

        leftTime.text = progress.asDisplayedDuration()
        rightTime.text = item.maxPlayTime.asDisplayedDuration()

        progressBar.max = item.maxPlayTime
        progressBar.progress = progress
    }

}
