package com.bl.todo.services

import android.util.Log
import com.bl.todo.models.UserDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object FirebaseAuthentication {
    private val firebaseAuth: FirebaseAuth = Firebase.auth

    fun getCurrentUser() = firebaseAuth.currentUser

    fun logOut() = firebaseAuth.signOut()

    fun signUpWithEmailAndPassword(email : String, password : String, listener : (Boolean,FirebaseUser?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful){
                Log.i("Auth","Successful")
                listener(true, getCurrentUser())
            }
            else{
                Log.i("Auth","Failed")
                Log.i("Auth",it.exception.toString())
                listener(false, getCurrentUser())
            }
        }
    }
}