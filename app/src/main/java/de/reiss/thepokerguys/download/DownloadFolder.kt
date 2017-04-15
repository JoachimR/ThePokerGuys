package de.reiss.thepokerguys.download

import android.os.Environment
import org.apache.commons.io.FilenameUtils
import java.io.File

object DownloadFolder {

    val DOWNLOAD_FOLDER = "the-poker-guys"

    fun findFile(url: String): File? {
        val file = fileForUrl(url)
        return when {
            file.exists() -> file
            else -> null
        }
    }

    fun fileForUrl(url: String): File = File(getDownloadDir(), FilenameUtils.getName(url))

    fun getDownloadDir(): File {
        val directory = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), DOWNLOAD_FOLDER)
        if (!directory.exists() || directory.isDirectory) {
            //noinspection ResultOfMethodCallIgnored
            directory.mkdirs()
        }
        return directory
    }

    fun subPathForUrl(url: String): String {
        return DOWNLOAD_FOLDER + "/" + FilenameUtils.getName(url)
    }

}