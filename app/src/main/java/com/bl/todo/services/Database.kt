package com.bl.todo.services

import android.os.Bundle
import android.util.Log
import com.bl.todo.models.DatabaseUser
import com.bl.todo.models.UserDataDbStatus
import com.bl.todo.models.UserDetails
import com.bl.todo.util.SharedPref
import com.bl.todo.util.Utilities
import com.facebook.share.Share
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object Database {
   private var db : DatabaseReference = Firebase.database.reference

   fun addUserInfoDatabase(user : DatabaseUser, listener : (Boolean)-> Unit){
      var userId =Authentication.getCurrentUser()?.uid.toString()
      db.child("users").child(userId).setValue(user).addOnCompleteListener {
         if(it.isSuccessful){
            Utilities.addUserInfoToSharedPref(user)
            listener(true)
         }else{
            listener(false)
         }
      }
   }

    fun getUserData(listener: (Boolean) -> Unit) {
       db.child("users").child(Authentication.getCurrentUser()?.uid.toString()).get()
          .addOnCompleteListener { status->
             if(!status.isSuccessful) {
                Log.e("DB","Read Failed")
                Log.e("DB",status.exception.toString())
                listener(false)
             }
             else{
                status.result.also {
                   Log.i("DB","User : $it")
                   var userDb : DatabaseUser = Utilities.createUserFromHashMap(it?.value as HashMap<*, *>)
                   Utilities.addUserInfoToSharedPref(userDb)
                   listener(true)
                }
             }
          }
    }
}