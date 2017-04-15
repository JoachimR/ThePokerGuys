package de.reiss.thepokerguys.play

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.reiss.thepokerguys.audio.AudioPlayerService
import java.util.*

class StartPlayingReceiver : BroadcastReceiver() {

    companion object {

        fun createPendingIntent(context: Context): PendingIntent {
            val uniqueRequestCode = Random().nextInt(100)

            return PendingIntent.getBroadcast(context, uniqueRequestCode,
                    Intent(context, StartPlayingReceiver::class.java)
                    , PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        AudioPlayerService.instance?.playOrPausePodcast()
    }

}
