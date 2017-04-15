package de.reiss.thepokerguys.util

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri

fun findDurationFromFileMetaData(context: Context, fileAbsolutePath: String): Int {
    try {
        val uri = Uri.parse(fileAbsolutePath)
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(context, uri)
        return Integer.parseInt(mediaMetadataRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))
    } catch (e: Exception) {
        logError(e) { "Error when trying to find duration of file $fileAbsolutePath" }
    }
    return 0
}