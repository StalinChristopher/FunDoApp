package com.bl.todo.viewmodels.loginPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bl.todo.models.DatabaseUser
import com.bl.todo.models.UserDetails
import com.bl.todo.services.Authentication
import com.bl.todo.services.DatabaseService
import com.bl.todo.services.FirebaseDatabaseService
import com.facebook.AccessToken

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
                DatabaseService.getUserData {
                    if(it){
                        _loginStatus.value = user
                    }
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
                DatabaseService.addUserInfoDatabase(userDb){
                    _facebookLoginStatus.value = user
                }
            }else{
                _facebookLoginStatus.value = user
            }



        }
    }

    fun getUserInfoFromDB(){
        DatabaseService.getUserData {
            _userData.value = it
        }
    }
}