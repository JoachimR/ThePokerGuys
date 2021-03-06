package net.thepokerguys.list

import android.content.Context
import android.support.annotation.WorkerThread
import com.einmalfel.earl.EarlParser
import com.einmalfel.earl.Item
import net.thepokerguys.RSS_FEED_SOUNDCLOUD_URL
import net.thepokerguys.database.PodcastDatabaseItem
import net.thepokerguys.download.DownloadFolder
import net.thepokerguys.util.findDurationFromFileMetaData
import net.thepokerguys.util.logWarn
import java.io.File
import java.net.URL
import java.util.*

open class RssDownloader(val context: Context) {

    /**
     * Download the RSS file that contains the podcast items
     *
     * @param amountOfItems the amount of items to download and parse. 0 means unlimited amount of items
     */
    @WorkerThread
    open fun execute(amountOfItems: Int = 0): List<PodcastDatabaseItem> {
        val result = ArrayList<PodcastDatabaseItem>()
        try {
            val inputStream = URL(RSS_FEED_SOUNDCLOUD_URL).openConnection().getInputStream()
            val feed = EarlParser.parseOrThrow(inputStream, amountOfItems)
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
