package com.bl.todo.services

import com.bl.todo.models.UserDetails
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object FirebaseDatabase {
   private var db : DatabaseReference = Firebase.database.reference

   fun addUserInfoDatabase(user : UserDetails, listener : (Boolean)-> Unit){
      var userId =FirebaseAuthentication.getCurrentUser()?.uid.toString()
      db.child("users").child(userId).setValue(user).addOnCompleteListener {
         if(it.isSuccessful){
            listener(true)
         }else{
            listener(false)
         }
      }
   }
}