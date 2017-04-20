package net.thepokerguys.delete

import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import net.thepokerguys.R
import net.thepokerguys.download.DownloadFolder
import net.thepokerguys.eventbus.AppEventBus
import net.thepokerguys.eventbus.FileDeleted
import net.thepokerguys.eventbus.FileRestored
import net.thepokerguys.util.logError
import net.thepokerguys.util.logInfo
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.util.*

open class DeleteFileHelper constructor(val context: Context) {

    private val deletedFilePaths = HashSet<String>()

    fun deleteAudioFile(url: String, title: String) {
        val file = DownloadFolder.findFile(url)
        if (file == null || file.exists().not()) {
            logInfo { "file for url $url does not exist" }
            return
        }

        file.absolutePath.let { filePath ->

            if (deletedFilePaths.contains(filePath)) {
                logInfo { "file $filePath already about to be deleted" }
                return
            }

            deletedFilePaths.add(filePath)

            object : DeleteAsyncTask(context = context, originalFilePath = filePath, makeBackup = true) {

                override fun onPostExecute(deleted: Boolean) {
                    if (deleted) {
                        AppEventBus.post(FileDeleted(title, filePath))
                    } else {
                        deletedFilePaths.remove(filePath)
                        Toast.makeText(context, R.string.toast_error_deleting_file, Toast.LENGTH_SHORT).show()
                    }
                }

            }.execute()
        }
    }

    fun undoDeleteAudioFile(filePath: String) {
        if (deletedFilePaths.contains(filePath).not()) {
            Toast.makeText(context, R.string.toast_error_restore_file, Toast.LENGTH_SHORT).show()
            return
        }

        object : UndoDeleteAsyncTask(context = context, origFilePath = filePath) {

            override fun onPostExecute(undoSuccess: Boolean?) {
                if (undoSuccess ?: false) {
                    AppEventBus.post(FileRestored())
                } else {
                    Toast.makeText(context, R.string.toast_error_undo_deleting_file, Toast.LENGTH_SHORT).show()
                }
            }

        }.execute()
    }

    fun cleanupUndoDeleteCache() {
        deletedFilePaths.forEach {
            val backupFile = createBackupFile(context, it)
            if (backupFile.exists()) {
                DeleteAsyncTask(
                        context = context,
                        originalFilePath = backupFile.absolutePath,
                        makeBackup = false).execute()
            }
        }

        deletedFilePaths.clear()
    }

    fun createBackupFile(context: Context, originalFileAbsolutePath: String): File {
        val backupFile = File(context.externalCacheDir,
                FilenameUtils.getName(originalFileAbsolutePath))
        return backupFile
    }

    inner open class DeleteAsyncTask(val context: Context,
                                     val originalFilePath: String,
                                     val makeBackup: Boolean) : AsyncTask<Void?, Void?, Boolean>() {

        override fun doInBackground(vararg p0: Void?): Boolean {
            val originalFile = File(originalFilePath)

            if (makeBackup) {
                val backupFile = File(context.externalCacheDir,
                        FilenameUtils.getName(originalFile.absolutePath))
                try {
                    return originalFile.renameTo(backupFile)
                } catch(e: SecurityException) {
                    logError(e) { "error when trying to back up file" }
                } catch(e: NullPointerException) {
                    logError(e) { "error when trying to back up file" }
                }
            }

            try {
                return originalFile.delete()
            } catch(e: SecurityException) {
                logError(e) { "error when trying to delete file" }
            }
            return false
        }

    }

    inner open class UndoDeleteAsyncTask(val context: Context,
                                         val origFilePath: String) : AsyncTask<Void?, Void?, Boolean>() {

        override fun doInBackground(vararg p0: Void?): Boolean {
            val backupFile = createBackupFile(context, origFilePath)
            if (backupFile.exists()) {
                try {
                    return backupFile.renameTo(File(origFilePath))
                } catch(e: Exception) {
                    logError(e) { "error when trying to restore file from backup file" }
                }
            }
            return false
        }

    }

}
