package de.reiss.thepokerguys.list

import android.content.Context
import android.support.annotation.WorkerThread
import com.einmalfel.earl.EarlParser
import com.einmalfel.earl.Item
import de.reiss.thepokerguys.RSS_FEED_SOUNDCLOUD_URL
import de.reiss.thepokerguys.database.PodcastDatabaseItem
import de.reiss.thepokerguys.download.DownloadFolder
import de.reiss.thepokerguys.util.findDurationFromFileMetaData
import de.reiss.thepokerguys.util.logWarn
import java.io.File
import java.net.URL
import java.util.*

open class RssDownloader(val context: Context) {

    @WorkerThread
    open fun execute(): List<PodcastDatabaseItem> {
        val result = ArrayList<PodcastDatabaseItem>()
        try {
            val inputStream = URL(RSS_FEED_SOUNDCLOUD_URL).openConnection().getInputStream()
            val feed = EarlParser.parseOrThrow(inputStream, 0) // 0 means unlimited amount of items
            for (item in feed.items) {
                val podcastItem = createPodcastDatabaseItem(item)
                if (podcastItem != null) {
                    result.add(podcastItem)
                }
            }
        } catch (e: Exception) {
            logWarn(e) {
                "Error when trying to parse RSS feed."
            }
            throw RuntimeException("Error when trying to parse RSS feed.")
        }
        return result
    }

    private fun createPodcastDatabaseItem(feedItem: Item): PodcastDatabaseItem? {
        val title = feedItem.title
        val description = feedItem.description
        val date = feedItem.publicationDate

        if (date != null &&
                title != null &&
                title.isNotEmpty() &&
                description != null &&
                feedItem.enclosures.isNotEmpty() &&
                feedItem.enclosures[0] != null &&
                feedItem.enclosures[0].link.isNotEmpty()) {

            val mp3Url = feedItem.enclosures.get(0).link
            val file = DownloadFolder.fileForUrl(mp3Url)
            val item = PodcastDatabaseItem(publishedDate = date,
                    title = title,
                    description = description,
                    url = mp3Url,
                    progress = 0,
                    duration = duration(file),
                    speed = 1f,
                    filePath = file.absolutePath)
            return item
        }
        return null
    }

    private fun duration(file: File): Int {
        if (file.exists()) {
            return findDurationFromFileMetaData(context, file.absolutePath)
        }
        return 0
    }

}
