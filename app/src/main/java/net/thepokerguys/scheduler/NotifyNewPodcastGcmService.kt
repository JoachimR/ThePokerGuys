package net.thepokerguys.scheduler

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.NotificationCompat
import com.google.android.gms.gcm.*
import com.google.android.gms.gcm.GcmNetworkManager.RESULT_SUCCESS
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

/**
 * https://www.bignerdranch.com/blog/optimize-battery-life-with-androids-gcm-network-manager/
 */
class NotifyNewPodcastGcmService : GcmTaskService() {

    companion object {

        fun scheduleDaily(context: Context) {
            GcmNetworkManager.getInstance(context)
                    .schedule(PeriodicTask.Builder()
                            .setService(NotifyNewPodcastGcmService::class.java)
                            .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                            .setTag("RefreshRss")
                            .setPeriod(86400)
                            .setFlex(3600)
                            .build())
        }

    }

    private var downloadSubscription: Subscription? = null

    override fun onRunTask(taskParams: TaskParams): Int {
        if (AppSettings(this).shouldNotifyNewPodcast()) {
            logInfo { "About to download latest rss item" }
            refreshRss()
        }
        return RESULT_SUCCESS // low priority, so avoid rescheduling by returning success in any case
    }

    private fun refreshRss() {
        downloadSubscription?.let {
            if (!it.isUnsubscribed) {
                logInfo { "Already downloading latest RSS item, skipping this time" }
                return
            }
        }

        downloadSubscription = downloadLatestItem()
                .compose(RxUtil.ioMain<PodcastDatabaseItem?>())
                .subscribe({ latestItem ->
                    if (latestItem == null) {
                        logInfo { "No latest RSS item found" }
                    } else {
                        logInfo { "Downloaded and parsed latest RSS item successfully" }
                        handleLatestItem(latestItem)
                    }
                }, { error ->
                    logWarn(error) { "Could not download and parse latest RSS item successfully" }
                })
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

    private fun handleLatestItem(latestPodcast: PodcastDatabaseItem) {
        val appSettings = AppSettings(this)

        if (appSettings.getLastKnownLatestPodcastURL() == latestPodcast.url) {
            logInfo {
                "Latest RSS item has already been displayed once"
            }
        } else {
            appSettings.setLastKnownLatestPodcastURL(latestPodcast.url)

            if (latestPodcast.title.isEmpty()) {
                logInfo { "Latest RSS item has no title (?)" }
            } else {
                NotificationHelper.show(this, NotificationCategory.NewPodcast,
                        createNotification(this, latestPodcast))
            }
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