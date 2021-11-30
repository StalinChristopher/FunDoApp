package com.bl.todo.Notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bl.todo.R
import com.bl.todo.common.Utilities
import com.bl.todo.ui.MainActivity
import com.bl.todo.ui.wrapper.NoteInfo

class NotificationWorker(val context: Context, workParams: WorkerParameters) :
    Worker(context, workParams) {
    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "FundooNotesChannel"
        const val TITLE_EXTRA = "titleExtra"
        const val MESSAGE_EXTRA = "messageExtra"
        const val NOTE = "note"
        const val FNID = "fnid"
        const val NID = "nid"
        const val DATE = "dateModified"
        const val ARCHIVED = "archived"
        const val REMINDER = "reminder"
    }

    override fun doWork(): Result {
        val noteTitle = inputData.getString(TITLE_EXTRA)
        val noteContent = inputData.getString(MESSAGE_EXTRA)
        val noteFnid = inputData.getString(FNID)
        val noteNid = inputData.getLong(NID, 0L)
        val noteDateModified = inputData.getString(DATE)
        val noteArchived = inputData.getBoolean(ARCHIVED, false)
        val noteReminder = inputData.getString(REMINDER)

        val dateModified = Utilities.stringToDate(noteDateModified)
        val reminder = Utilities.stringToDate(noteReminder)
        val note = NoteInfo(noteTitle.toString(),noteContent.toString(), noteFnid.toString(), noteNid, dateModified, noteArchived, reminder)

        val sendingIntent = Intent(context, MainActivity::class.java)
        sendingIntent.putExtra("destination", "home")
        sendingIntent.putExtra("noteInfo", note)

        val pendingIntent = PendingIntent.getActivity(context, 0,sendingIntent, PendingIntent.FLAG_ONE_SHOT)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.nav_notes_icon)
            .setContentIntent(pendingIntent)
            .setContentTitle(noteTitle)
            .setContentText(noteContent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
        return Result.success()
    }

}