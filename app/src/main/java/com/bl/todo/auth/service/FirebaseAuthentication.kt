package com.bl.todo.auth.service

import android.content.Context
import android.util.Log
import com.bl.todo.data.services.DatabaseService
import com.bl.todo.ui.wrapper.UserDetails
import com.bl.todo.common.SharedPref
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FirebaseAuthentication {
    private val firebaseAuth: FirebaseAuth = Firebase.auth

    fun getCurrentUser() = firebaseAuth.currentUser

    suspend fun logOut(context: Context) {
        withContext(Dispatchers.Default) {
            SharedPref.clearAll()
            LoginManager.getInstance().logOut()
            DatabaseService.getInstance(context).clearAllTables()
            firebaseAuth.signOut()
        }
    }

    fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        listener: (UserDetails) -> Unit
    ) {
        if (getCurrentUser() != null) {
            var fUid = getCurrentUser()?.uid.toString()
            var user = UserDetails(
                getCurrentUser()?.displayName.toString(), getCurrentUser()?.email.toString(),
                getCurrentUser()?.phoneNumber.toString(), true, fUid = fUid
            )
            listener(user)
        }
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            var user: UserDetails? = null
            if (it.isSuccessful) {
                Log.i("Auth", "Successful")
                Log.i("AuthUser", "${getCurrentUser()?.displayName.toString()}")
                var fUid = getCurrentUser()?.uid.toString()
                user = UserDetails(
                    getCurrentUser()?.displayName.toString(), getCurrentUser()?.email.toString(),
                    getCurrentUser()?.phoneNumber.toString(), true, fUid = fUid
                )
                listener(user)
            } else {
                Log.i("Auth", "Failed")
                Log.i("Auth", it.exception.toString())
                user = UserDetails("", "", "", false, fUid = null)
                listener(user)
            }
        }
    }

    fun loginWithEmailAndPassword(
        email: String,
        password: String,
        listener: (UserDetails) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            var user: UserDetails? = null
            if (it.isSuccessful) {
                Log.i("Auth", "Successful login")
                var fUid = getCurrentUser()?.uid.toString()
                user = UserDetails(
                    getCurrentUser()?.displayName.toString(), getCurrentUser()?.email.toString(),
                    getCurrentUser()?.phoneNumber.toString(), true, fUid = fUid
                )
                listener(user)
            } else {
                Log.i("Auth", "Login failed")
                Log.i("Auth", it.exception.toString())
                user = UserDetails("", "", "", false, fUid = null)
                listener(user)
            }
        }
    }

    fun handleFacebookLogin(token: AccessToken, listener: (UserDetails) -> Unit) {
        Log.d("FacebookAccessToken", "handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                var user: UserDetails? = null
                if (task.isSuccessful) {
                    Log.d("FacebookAuth", "signInWithCredential:success")
                    var fUid = getCurrentUser()?.uid.toString()
                    user = UserDetails(
                        getCurrentUser()?.displayName.toString(),
                        getCurrentUser()?.email.toString(),
                        getCurrentUser()?.phoneNumber.toString(),
                        true,
                        fUid = fUid
                    )
                    listener(user)
                } else {
                    Log.w("FacebookAuth", "signInWithCredential:failure", task.exception)
                    user = UserDetails("", "", "", false, fUid = null)
                    listener(user)
                }
            }

    }

    fun resetPassword(email: String, listener: (Boolean) -> Unit) {
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                listener(true)
            } else {
                listener(false)
            }
        }
    }
}
