package net.thepokerguys.audio

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.NotificationCompat
import net.thepokerguys.R
import net.thepokerguys.board.BoardFinder
import net.thepokerguys.database.PodcastDatabaseItem
import net.thepokerguys.play.GoForwardOrBackwardReceiver
import net.thepokerguys.play.PlayActivity
import net.thepokerguys.play.StartPlayingReceiver
import java.util.*

private val NOTIFICATION_ID = 33

fun startAudioForegroundNotification(service: AudioPlayerService, podcastDatabaseItem: PodcastDatabaseItem) {
    service.startForeground(NOTIFICATION_ID,
            createBuilder(service, podcastDatabaseItem)

                    .addAction(R.drawable.ic_fast_rewind,
                            service.getText(R.string.notification_button_go_back_title),
                            GoForwardOrBackwardReceiver.createPendingIntent(service, false))

                    .addAction(R.drawable.ic_pause,
                            service.getText(R.string.notification_button_pause_title),
                            StartPlayingReceiver.createPendingIntent(service))

                    .addAction(R.drawable.ic_fast_forward,
                            service.getText(R.string.notification_button_go_forward_title),
                            GoForwardOrBackwardReceiver.createPendingIntent(service, true))

                    .build())
}

fun showAudioPausedNotification(context: Context, podcastDatabaseItem: PodcastDatabaseItem) {
    notificationManager(context).notify(NOTIFICATION_ID,
            createBuilder(context, podcastDatabaseItem)

                    .addAction(R.drawable.ic_fast_rewind,
                            context.getText(R.string.notification_button_go_back_title),
                            GoForwardOrBackwardReceiver.createPendingIntent(context, false))

                    .addAction(R.drawable.ic_play,
                            context.getText(R.string.notification_button_play_title),
                            StartPlayingReceiver.createPendingIntent(
                                    context))

                    .addAction(R.drawable.ic_fast_forward,
                            context.getText(R.string.notification_button_go_forward_title),
                            GoForwardOrBackwardReceiver.createPendingIntent(context, true))

                    .build())
}

fun cancelPausedNotification(context: Context) {
    notificationManager(context).cancel(NOTIFICATION_ID)
}

private fun createBuilder(context: Context, podcastDatabaseItem: PodcastDatabaseItem)
        : NotificationCompat.Builder {

    val title: String
    val text: String
    val board = BoardFinder.asTextFrom(context, podcastDatabaseItem.description)
    if (board.isEmpty()) {
        title = podcastDatabaseItem.title
        text = ""
    } else {
        title = board
        text = podcastDatabaseItem.title
    }

    return NotificationCompat.Builder(context)
            .setStyle(NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(1))
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent(context, podcastDatabaseItem.url))
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_play)
            .setWhen(0)
            .setAutoCancel(true)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))

            as NotificationCompat.Builder
}

private fun notificationManager(context: Context) =
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)

private fun pendingIntent(context: Context, url: String): PendingIntent? {
    val uniqueRequestCode = Random().nextInt(100)
    val contentPendingIntent = PendingIntent.getActivity(context, uniqueRequestCode,
            PlayActivity.createIntent(context, url)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT)
    return contentPendingIntent
}