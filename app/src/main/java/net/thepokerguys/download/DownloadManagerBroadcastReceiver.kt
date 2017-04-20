package net.thepokerguys.download

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import net.thepokerguys.App
import net.thepokerguys.eventbus.AppEventBus
import net.thepokerguys.eventbus.DownloadStopped

class DownloadManagerBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        AppEventBus.post(DownloadStopped(findUrl(context, intent)))
    }

    private fun findUrl(context: Context, intent: Intent): String? {
        val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        if (downloadId > -1) {
            (context.applicationContext as App).appSingletons.downloadManagerHelper()
                    .findUrlFromId(downloadId)
        }
        return null
    }

}
