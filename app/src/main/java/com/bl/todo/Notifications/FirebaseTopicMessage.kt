package com.bl.todo.Notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

object FirebaseTopicMessage {
    fun subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("general")
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    Log.i("MainActivity","subscribed")
                } else {
                    Log.i("MainActivity", "failed")
                }
            }
    }
}