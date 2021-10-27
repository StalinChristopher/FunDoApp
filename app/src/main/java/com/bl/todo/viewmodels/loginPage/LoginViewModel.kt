package com.bl.todo.viewmodels.loginPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bl.todo.models.DatabaseUser
import com.bl.todo.models.UserDataDbStatus
import com.bl.todo.models.UserDetails
import com.bl.todo.services.Authentication
import com.bl.todo.services.Database
import com.facebook.AccessToken

class LoginViewModel : ViewModel() {
    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus = _loginStatus as LiveData<Boolean>

    private val _facebookLoginStatus = MutableLiveData<Boolean>()
    val facebookLoginStatus = _facebookLoginStatus as LiveData<Boolean>

    private val _userData = MutableLiveData<Boolean>()
    val userData = _userData as LiveData<Boolean>

    fun loginWithEmailAndPassword(email : String, password : String){
        Authentication.loginWithEmailAndPassword(email, password){
            Database.getUserData {
                _loginStatus.value = it
            }
        }
    }

    fun loginWithFacebook(token : AccessToken){
        Authentication.handleFacebookLogin(token){ user->
            var userDb = DatabaseUser(user.userName,user.email,user.phone)
            Database.addUserInfoDatabase(userDb){
                _facebookLoginStatus.value = it
            }


        }
    }

    fun getUserInfoFromDB(){
        Database.getUserData {
            _userData.value = it
        }
    }
}