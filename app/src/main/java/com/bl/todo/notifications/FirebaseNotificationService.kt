package com.bl.todo.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bl.todo.R
import com.bl.todo.ui.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseNotificationService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_ID = "fundooPushNotification"
        const val NOTIFICATION_ID = 2
        const val CHANNEL_NAME = "fundooApp push notification"
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.i("FirebaseNotifyService", "Refreshed token : $p0")
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        if(p0.notification != null) {
            generateNotification(p0.notification!!.title!!, p0.notification!!.body!!)
        }
        if(p0.data != null) {
            Log.i("FirebaseNotify","Data arrived ${p0.data["offer"]}")
        }
    }

    private fun generateNotification(title: String, content: String) {

        val intent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.nav_notes_icon)
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(notificationChannel)
        }

        manager.notify(NOTIFICATION_ID, notification)
    }
}