package com.bl.todo.services

import android.util.Log
import android.widget.Toast
import com.bl.todo.models.UserDetails
import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object Authentication {
    private val firebaseAuth: FirebaseAuth = Firebase.auth

    fun getCurrentUser() = firebaseAuth.currentUser

    fun logOut() = firebaseAuth.signOut()

    fun signUpWithEmailAndPassword(email : String, password : String, listener : (Boolean,FirebaseUser?) -> Unit) {
        if(getCurrentUser()!=null){
            listener(true, getCurrentUser())
        }
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

    fun loginWithEmailAndPassword(email: String, password: String, listener: (Boolean, FirebaseUser?) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                Log.i("Auth","Successful login")
                listener(true, getCurrentUser())
            }else {
                Log.i("Auth","Login failed")
                Log.i("Auth",it.exception.toString())
                listener(false, getCurrentUser())
            }
        }
    }

    fun handleFacebookLogin(token : AccessToken, listener: (Boolean, FirebaseUser?) -> Unit){
        Log.d("FacebookAccessToken", "handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    Log.d("FacebookAuth", "signInWithCredential:success")
                    listener(true, getCurrentUser())
                } else {
                    Log.w("FacebookAuth", "signInWithCredential:failure", task.exception)
                    listener(false, getCurrentUser())
                }
            }

    }
}