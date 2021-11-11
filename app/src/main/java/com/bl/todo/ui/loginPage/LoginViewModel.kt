package com.bl.todo.ui.loginPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.todo.data.models.DatabaseUser
import com.bl.todo.data.wrapper.UserDetails
import com.bl.todo.authService.Authentication
import com.bl.todo.data.services.DatabaseService
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
                    DatabaseService.getUserData()
                    _loginStatus.postValue(user)
                }
            }else{
                _loginStatus.value = user
            }
        }
    }

    fun loginWithFacebook(token : AccessToken){
        Authentication.handleFacebookLogin(token){ user->
            var userDb = DatabaseUser(user.userName,user.email,user.phone)
            if(user.loginStatus){
                viewModelScope.launch {
                    DatabaseService.addUserInfoDatabase(userDb)
                    _facebookLoginStatus.postValue(user)
                }
            }else{
                _facebookLoginStatus.value = user
            }



        }
    }

}