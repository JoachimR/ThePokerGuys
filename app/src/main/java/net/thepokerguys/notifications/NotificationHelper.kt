package net.thepokerguys.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context


object NotificationHelper {

    fun startForeground(service: Service, category: NotificationCategory, notification: Notification) {
        service.startForeground(category.notificationId, notification)
    }

    fun show(context: Context, category: NotificationCategory, notification: Notification) {
        notificationManager(context).notify(category.notificationId, notification)
    }

    fun cancel(context: Context, category: NotificationCategory) {
        notificationManager(context).cancel(category.notificationId)
    }

    private fun notificationManager(context: Context) =
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)

}