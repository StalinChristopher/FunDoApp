package com.bl.todo.services

import android.util.Log
import android.widget.Toast
import androidx.annotation.BoolRes
import com.bl.todo.models.UserDetails
import com.bl.todo.util.SharedPref
import com.facebook.AccessToken
import com.facebook.login.LoginManager
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

    fun logOut(){
        SharedPref.clearAll()
        LoginManager.getInstance().logOut()
        firebaseAuth.signOut()
    }

    fun signUpWithEmailAndPassword(email : String, password : String, phone : String, listener : (UserDetails) -> Unit) {
        if(getCurrentUser()!=null){
            var user = UserDetails(getCurrentUser()?.displayName.toString(), getCurrentUser()?.email.toString(),
                getCurrentUser()?.phoneNumber.toString(), true)
            listener(user)
        }
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            var user : UserDetails? = null
            if(it.isSuccessful){
                Log.i("Auth","Successful")
                Log.i("AuthUser","${getCurrentUser()?.displayName.toString()}")
                user = UserDetails(getCurrentUser()?.displayName.toString(), getCurrentUser()?.email.toString(),
                    getCurrentUser()?.phoneNumber.toString(), true)
                listener(user)
            }
            else{
                Log.i("Auth","Failed")
                Log.i("Auth",it.exception.toString())
                user = UserDetails("","","", false)
                listener(user)
            }
        }
    }

    fun loginWithEmailAndPassword(email: String, password: String, listener: (UserDetails) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            var user : UserDetails? = null
            if(it.isSuccessful){
                Log.i("Auth","Successful login")
                user = UserDetails(
                    getCurrentUser()?.displayName.toString(), getCurrentUser()?.email.toString(),
                    getCurrentUser()?.phoneNumber.toString(),true)
                listener(user)
            }else {
                Log.i("Auth","Login failed")
                Log.i("Auth",it.exception.toString())
                user = UserDetails("","","",false)
                listener(user)
            }
        }
    }

    fun handleFacebookLogin(token : AccessToken, listener: (UserDetails) -> Unit){
        Log.d("FacebookAccessToken", "handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener{ task ->
                var user : UserDetails? = null
                if (task.isSuccessful) {
                    Log.d("FacebookAuth", "signInWithCredential:success")
                    user = UserDetails(getCurrentUser()?.displayName.toString(), getCurrentUser()?.email.toString(),
                        getCurrentUser()?.phoneNumber.toString(),true)
                    listener(user)
                } else {
                    Log.w("FacebookAuth", "signInWithCredential:failure", task.exception)
                    user = UserDetails("","","",false)
                    listener(user)
                }
            }

    }

    fun resetPassword(email: String, listener: (Boolean) -> Unit){
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener {
            if(it.isSuccessful){
                listener(true)
            }else{
                listener(false)
            }
        }
    }
}
