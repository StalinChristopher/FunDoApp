package com.bl.todo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bl.todo.models.UserDetails
import com.bl.todo.services.Authentication
import com.bl.todo.services.Database
import com.facebook.AccessToken

class SharedViewModel : ViewModel(){
    private val _gotoHomePageStatus = MutableLiveData<Boolean>()
    private val _gotoLoginPageStatus = MutableLiveData<Boolean>()
    private val _gotoSplashScreenStatus = MutableLiveData<Boolean>()
    private val _gotoSignupPageStatus = MutableLiveData<Boolean>()
    private val _gotoForgotPasswordStatus = MutableLiveData<Boolean>()
    private val _loginStatus = MutableLiveData<UserDetails>()
    private val _signUpStatus = MutableLiveData<UserDetails>()
    private val _facebookLoginStatus = MutableLiveData<UserDetails>()
    private val _resetPasswordStatus = MutableLiveData<Boolean>()

    val gotoHomePageStatus = _gotoHomePageStatus as LiveData<Boolean>
    val gotoLoginPageStatus = _gotoLoginPageStatus as LiveData<Boolean>
    val gotoSplashScreenStatus = _gotoSplashScreenStatus as LiveData<Boolean>
    val gotoSignupPageStatus = _gotoSignupPageStatus as LiveData<Boolean>
    val gotoForgotPasswordStatus = _gotoForgotPasswordStatus as LiveData<Boolean>
    val loginStatus = _loginStatus as LiveData<UserDetails>
    val signUpStatus = _signUpStatus as LiveData<UserDetails>
    val facebookLoginStatus = _facebookLoginStatus as LiveData<UserDetails>
    val resetPasswordStatus = _resetPasswordStatus as LiveData<Boolean>

    fun setGotoHomePageStatus(status: Boolean){
        _gotoHomePageStatus.value = status
    }

    fun setLoginPageStatus(status : Boolean){
        _gotoLoginPageStatus.value = status
    }

    fun setSplashScreenStatus(status: Boolean){
        _gotoSplashScreenStatus.value = status
    }

    fun setSignupPageStatus(status: Boolean){
        _gotoSignupPageStatus.value = status
    }

    fun setForgotPasswordPageStatus(status: Boolean){
        _gotoForgotPasswordStatus.value = status
    }

    fun loginWithEmailAndPassword(email : String, password : String){
        Authentication.loginWithEmailAndPassword(email, password){
            _loginStatus.value = it
        }
    }

    fun signUpWithEmailAndPassword(email:String, password: String){
        Authentication.signUpWithEmailAndPassword(email,password){
            _signUpStatus.value = it
        }
    }

    fun loginWithFacebook(token : AccessToken){
        Authentication.handleFacebookLogin(token){
            _facebookLoginStatus.value = it
        }
    }

    fun resetPasswordWithEmail(email : String){
        Authentication.resetPassword(email){
            _resetPasswordStatus.value = it
        }
    }

}