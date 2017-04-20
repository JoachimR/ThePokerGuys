package net.thepokerguys

import net.thepokerguys.database.PodcastDatabaseItem
import net.thepokerguys.util.emptyMutableList
import org.apache.commons.io.FilenameUtils
import java.util.*

fun sampleList(amountOfItems: Int = 1): MutableList<PodcastDatabaseItem> {
    if (amountOfItems < 1) {
        return emptyMutableList()
    }
    return (1..amountOfItems).mapTo(ArrayList<PodcastDatabaseItem>()) { sampleItem(it) }
}

fun sampleItem(numberOfItem: Int): PodcastDatabaseItem {
    val url = "http://feeds.soundcloud.com/stream/311721741-user-594538171-url_sample_${numberOfItem}_.m4a"
    return PodcastDatabaseItem(
            url = url,
            publishedDate = Date(),
            title = "TITLE_sample_$numberOfItem",
            description = "description_sample_$numberOfItem",
            progress = 1500,
            duration = 2000,
            speed = 1f,
            filePath = FilenameUtils.getName(url))
}
