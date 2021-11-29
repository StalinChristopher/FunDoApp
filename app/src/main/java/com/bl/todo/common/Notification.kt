package com.bl.todo.common

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.bl.todo.R
import com.bl.todo.ui.MainActivity
import com.bl.todo.ui.wrapper.NoteInfo

class Notification : BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "FundooNotesChannel"
        const val TITLE_EXTRA = "titleExtra"
        const val MESSAGE_EXTRA = "messageExtra"
        const val NOTE = "note"
    }

    override fun onReceive(context: Context, intent: Intent) {
        var title = intent.getStringExtra(TITLE_EXTRA)
        var content = intent.getStringExtra(MESSAGE_EXTRA)
        var note = intent.getSerializableExtra(NOTE)  as NoteInfo

        var sendingIntent = Intent(context, MainActivity::class.java)
        sendingIntent.putExtra("destination", "home")
        sendingIntent.putExtra("noteInfo", note)

        var pendingIntent = PendingIntent.getActivity(context, 0,sendingIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.nav_notes_icon)
            .setContentIntent(pendingIntent)
            .setContentTitle(intent.getStringExtra(TITLE_EXTRA))
            .setContentText(intent.getStringExtra(MESSAGE_EXTRA))
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }
}