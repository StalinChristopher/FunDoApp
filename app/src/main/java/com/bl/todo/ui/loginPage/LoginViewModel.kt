package com.bl.todo.ui.loginPage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.todo.data.models.DatabaseUser
import com.bl.todo.data.wrapper.UserDetails
import com.bl.todo.authService.Authentication
import com.bl.todo.data.services.DatabaseService
import com.bl.todo.util.SharedPref
import com.facebook.AccessToken
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _loginStatus = MutableLiveData<UserDetails>()
    val loginStatus = _loginStatus as LiveData<UserDetails>

    private val _facebookLoginStatus = MutableLiveData<UserDetails>()
    val facebookLoginStatus = _facebookLoginStatus as LiveData<UserDetails>

    private val _userData = MutableLiveData<Boolean>()
    val userData = _userData as LiveData<Boolean>

    fun loginWithEmailAndPassword(email : String, password : String){
        Authentication.loginWithEmailAndPassword(email, password){ user->
            if(user.loginStatus){
                viewModelScope.launch {
                    var user = DatabaseService.addUserInfoDatabase(user)
                    if(user != null){
                        SharedPref.addUserId(user.uid)
                        DatabaseService.addCloudDataToLocalDB(user)
                        _loginStatus.postValue(user)
                    }
                }
            }else{
                _loginStatus.value = user
            }
        }
    }

    fun loginWithFacebook(token : AccessToken){
        Authentication.handleFacebookLogin(token){ user->
            if(user.loginStatus){
                viewModelScope.launch {
                    Log.i("Facebook","Reached")
                    var user = DatabaseService.addUserInfoDatabase(user)
                    if(user != null){
                        SharedPref.addUserId(user.uid)
                        DatabaseService.addCloudDataToLocalDB(user)
                        _facebookLoginStatus.postValue(user)
                    }
                }
            }else{
                _facebookLoginStatus.value = user
            }
        }
    }

}