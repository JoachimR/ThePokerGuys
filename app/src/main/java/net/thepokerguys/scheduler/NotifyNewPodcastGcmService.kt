package net.thepokerguys.scheduler

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.NotificationCompat
import com.google.android.gms.gcm.GcmNetworkManager.RESULT_FAILURE
import com.google.android.gms.gcm.GcmNetworkManager.RESULT_SUCCESS
import com.google.android.gms.gcm.GcmTaskService
import com.google.android.gms.gcm.TaskParams
import net.thepokerguys.MainActivity
import net.thepokerguys.R
import net.thepokerguys.database.PodcastDatabaseItem
import net.thepokerguys.list.RssDownloader
import net.thepokerguys.notifications.NotificationCategory
import net.thepokerguys.notifications.NotificationHelper
import net.thepokerguys.settings.AppSettings
import net.thepokerguys.util.RxUtil
import net.thepokerguys.util.logInfo
import net.thepokerguys.util.logWarn
import rx.Observable
import rx.Subscription
import java.util.*

class NotifyNewPodcastGcmService : GcmTaskService() {

    private var downloadSubscription: Subscription? = null

    override fun onRunTask(taskParams: TaskParams): Int {
        if (AppSettings(this).shouldNotifyNewPodcast().not()) {
            return RESULT_SUCCESS
        }

        logInfo { "About to download latest rss item" }
        return if (refreshRss()) RESULT_SUCCESS else RESULT_FAILURE
    }

    private fun refreshRss(): Boolean {
        downloadSubscription?.let {
            if (!it.isUnsubscribed) {
                logInfo { "Already downloading latest RSS item, skipping this time" }
                return true
            }
        }

        downloadSubscription = downloadLatestItem()
                .compose(RxUtil.ioMain<PodcastDatabaseItem?>())
                .subscribe({ latestItem ->
                    if (latestItem == null) {
                        logInfo { "No latest RSS item found" }
                    } else {
                        logInfo { "Download and parsed latest RSS item successfully" }
                        showNotification(latestItem)
                    }
                }, { error ->
                    logWarn(error) { "Could not download and parse latest RSS item successfully" }
                })

        return false
    }

    private fun downloadLatestItem(): Observable<PodcastDatabaseItem?> {
        return Observable.create {
            try {
                val rssDownloader = RssDownloader(this)
                val list = rssDownloader.execute(amountOfItems = 1)
                it.onNext(if (list.isEmpty()) null else list[0])
                it.onCompleted()
            } catch (e: Exception) {
                it.onError(e)
            }
        }
    }

    private fun showNotification(latestItem: PodcastDatabaseItem) {
        if (latestItem.title.isEmpty()) {
            logInfo { "Latest RSS item has no title, skipping the notification display" }
        } else {
            NotificationHelper.show(this, NotificationCategory.NewPodcast,
                    createNotification(this, latestItem))
        }
    }

    private fun createNotification(context: Context, latestPodcast: PodcastDatabaseItem): Notification {
        return NotificationCompat.Builder(context)
                .setCategory(NotificationCompat.CATEGORY_PROMO)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent(context))
                .setContentTitle(getString(R.string.notification_new_podcast_title))
                .setContentText(latestPodcast.title)
                .setSmallIcon(R.drawable.ic_new_releases)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .build()
    }

    private fun pendingIntent(context: Context): PendingIntent? {
        val uniqueRequestCode = Random().nextInt(100)
        val contentPendingIntent = PendingIntent.getActivity(context, uniqueRequestCode,
                MainActivity.createIntent(context)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT)
        return contentPendingIntent
    }

}