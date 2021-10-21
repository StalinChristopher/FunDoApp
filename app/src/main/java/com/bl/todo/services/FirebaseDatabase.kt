package com.bl.todo.services

import android.os.Bundle
import com.bl.todo.models.UserDetails
import com.google.firebase.database.DataSnapshot
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

    fun getUserData(uid: String,listener: (Boolean,Bundle?) -> Unit) {
       var bundle = Bundle()
       var result : DataSnapshot
      db.child("users").child(uid).get().addOnCompleteListener {
         if(it.isSuccessful){
            result = it.result!!
            bundle.putString("name", result.child("userName").value.toString())
            bundle.putString("email",result.child("email").value.toString())
            bundle.putString("phone",result.child("phone").value.toString())
            listener(true,bundle)
         }else{
            listener(false, null)
         }
      }
    }
}