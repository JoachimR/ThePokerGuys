package de.reiss.thepokerguys.download

import android.app.DownloadManager
import android.app.DownloadManager.*
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import de.reiss.thepokerguys.R
import de.reiss.thepokerguys.download.DownloadFolder.findFile
import de.reiss.thepokerguys.download.DownloadFolder.getDownloadDir
import de.reiss.thepokerguys.download.DownloadFolder.subPathForUrl
import de.reiss.thepokerguys.eventbus.AppEventBus
import de.reiss.thepokerguys.eventbus.DownloadStarted
import de.reiss.thepokerguys.eventbus.DownloadStopped
import de.reiss.thepokerguys.util.getInt
import de.reiss.thepokerguys.util.getLong
import de.reiss.thepokerguys.util.getString
import org.apache.commons.io.FilenameUtils
import java.util.*

open class DownloadManagerHelper constructor(var context: Context) {

    companion object {

        /**
         * DownloadManager may hold a slightly different URL than the one that is extracted from
         * the RSS. This method returns a URL identifier that works for both URLs
         */
        fun urlIdentifier(url: String) = if (url.isEmpty()) "" else FilenameUtils.getName(url)!!

    }

    private val downloadManager: DownloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    open fun startNewDownloadRequest(url: String) {
        if (!isCurrentlyDownloading(url)) {
            val downloadDirectory = getDownloadDir()
            if (downloadDirectory.exists()) {
                val request = createDownloadManagerRequest(url)
                downloadManager.enqueue(request)
                AppEventBus.post(DownloadStarted(url))
            } else {
                Toast.makeText(context, context.getString(R.string.can_not_access_download_directory), Toast.LENGTH_SHORT).show()
            }
        }
    }

    open fun createDownloadManagerRequest(url: String): Request {
        val request = Request(Uri.parse(url))
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, subPathForUrl(url))
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(Request.VISIBILITY_VISIBLE)
        return request
    }

    open fun cancelDownloadRequest(url: String) {
        findCurrentDownloadItem(url)?.let {
            if (downloadManager.remove(it.id) > 0) {
                AppEventBus.post(DownloadStopped(url))
            }
        }
    }

    open fun allCurrentDownloadItems(): List<DownloadItem> {
        val result = ArrayList<DownloadItem>()
        val cursor = currentlyDownloadingItemsCursor()
        if (cursor.moveToFirst()) {
            do {
                result.add(DownloadItem(cursor.getLong(COLUMN_ID),
                        cursor.getString(COLUMN_URI),
                        cursor.getInt(COLUMN_STATUS),
                        cursor.getLong(COLUMN_BYTES_DOWNLOADED_SO_FAR),
                        cursor.getLong(COLUMN_TOTAL_SIZE_BYTES)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return result
    }

    open fun isCurrentlyDownloading(url: String): Boolean {
        return findCurrentDownloadItem(url) != null
    }

    open fun isAlreadyDownloaded(url: String): Boolean {
        return doesFileExist(url) && isCurrentlyDownloading(url).not()
    }

    private fun doesFileExist(url: String): Boolean {
        val file = findFile(url)
        return file != null && file.exists()
    }

    open fun findUrlFromId(id: Long): String? {
        return findDownloadItem(id)?.url
    }

    open fun findCurrentDownloadItem(url: String): DownloadItem? {
        val urlIdentifier = urlIdentifier(url)
        if (urlIdentifier.isEmpty()) {
            return null
        }

        val cursor = currentlyDownloadingItemsCursor()
        if (cursor.moveToFirst()) {
            do {
                val urlValue = cursor.getString(COLUMN_URI)
                if (urlIdentifier(urlValue) == urlIdentifier) {
                    return DownloadItem(cursor.getLong(COLUMN_ID),
                            urlValue,
                            cursor.getInt(COLUMN_STATUS),
                            cursor.getLong(COLUMN_BYTES_DOWNLOADED_SO_FAR),
                            cursor.getLong(COLUMN_TOTAL_SIZE_BYTES))
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return null
    }

    private fun findDownloadItem(id: Long): DownloadItem? {
        val cursor = allItemsCursor()
        if (cursor.moveToFirst()) {
            do {
                val idValue = cursor.getLong(COLUMN_ID)
                if (idValue == id) {
                    return DownloadItem(idValue,
                            cursor.getString(COLUMN_URI),
                            cursor.getInt(COLUMN_STATUS),
                            cursor.getLong(COLUMN_BYTES_DOWNLOADED_SO_FAR),
                            cursor.getLong(COLUMN_TOTAL_SIZE_BYTES))
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return null
    }

    private fun currentlyDownloadingItemsCursor(): Cursor {
        val query = DownloadManager.Query()
        query.setFilterByStatus(STATUS_PENDING or STATUS_RUNNING or STATUS_PAUSED)
        return downloadManager.query(query)
    }

    private fun allItemsCursor(): Cursor {
        val query = DownloadManager.Query()
        return downloadManager.query(query)
    }

}