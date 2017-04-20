package net.thepokerguys.play

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import net.thepokerguys.audio.AudioPlayerService

import java.util.*

class GoForwardOrBackwardReceiver : BroadcastReceiver() {

    companion object {

        private val KEY_DIRECTION_FORWARD = "KEY_DIRECTION_FORWARD"

        fun createPendingIntent(context: Context, goForward: Boolean): PendingIntent {
            val uniqueRequestCode = Random().nextInt(100)

            return PendingIntent.getBroadcast(context, uniqueRequestCode,
                    Intent(context, GoForwardOrBackwardReceiver::class.java)
                            .putExtra(KEY_DIRECTION_FORWARD, goForward),
                    PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.hasExtra(KEY_DIRECTION_FORWARD)) {
            if (intent.getBooleanExtra(KEY_DIRECTION_FORWARD, true)) {
                AudioPlayerService.instance?.goForward10Seconds()
            } else {
                AudioPlayerService.instance?.goBack10Seconds()
            }
        }
    }

}
