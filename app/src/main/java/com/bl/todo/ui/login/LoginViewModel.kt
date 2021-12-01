package com.bl.todo.ui.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.todo.ui.wrapper.UserDetails
import com.bl.todo.auth.service.FirebaseAuthentication
import com.bl.todo.data.services.DatabaseService
import com.bl.todo.common.SharedPref
import com.facebook.AccessToken
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _loginStatus = MutableLiveData<UserDetails>()
    val loginStatus = _loginStatus as LiveData<UserDetails>

    private val _facebookLoginStatus = MutableLiveData<UserDetails>()
    val facebookLoginStatus = _facebookLoginStatus as LiveData<UserDetails>

    private val _userData = MutableLiveData<Boolean>()
    val userData = _userData as LiveData<Boolean>

    fun loginWithEmailAndPassword(context: Context, email: String, password: String) {
        FirebaseAuthentication.loginWithEmailAndPassword(email, password) {
            if (it.loginStatus) {
                viewModelScope.launch {
                    var user = DatabaseService.getInstance(context).addUserInfoDatabase(it)
                    if (user != null) {
                        SharedPref.addUserId(user.uid)
                        DatabaseService.getInstance(context).addCloudDataToLocalDB(user)
                        _loginStatus.postValue(user)
                    }
                }
            } else {
                _loginStatus.value = it
            }
        }
    }

    fun loginWithFacebook(context: Context, token: AccessToken) {
        FirebaseAuthentication.handleFacebookLogin(token) { user ->
            if (user.loginStatus) {
                viewModelScope.launch {
                    Log.i("Facebook", "Reached")
                    var user = DatabaseService.getInstance(context).addUserInfoDatabase(user)
                    if (user != null) {
                        SharedPref.addUserId(user.uid)
                        DatabaseService.getInstance(context).addCloudDataToLocalDB(user)
                        _facebookLoginStatus.postValue(user)
                    }
                }
            } else {
                _facebookLoginStatus.value = user
            }
        }
    }

}