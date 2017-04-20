package net.thepokerguys.database

import java.util.*

data class PodcastDatabaseItem(
        val url: String,
        val publishedDate: Date,
        var title: String,
        var description: String,
        var progress: Int,
        var duration: Int,
        var speed: Float,
        var filePath: String)