package com.bl.todo.viewmodels.sharedView

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bl.todo.models.UserDetails
import com.bl.todo.services.Authentication
import com.bl.todo.services.Database
import com.facebook.AccessToken

class SharedViewModel : ViewModel(){

    private val _gotoHomePageStatus = MutableLiveData<Boolean>()
    val gotoHomePageStatus = _gotoHomePageStatus as LiveData<Boolean>

    private val _gotoLoginPageStatus = MutableLiveData<Boolean>()
    val gotoLoginPageStatus = _gotoLoginPageStatus as LiveData<Boolean>

    private val _gotoSplashScreenStatus = MutableLiveData<Boolean>()
    val gotoSplashScreenStatus = _gotoSplashScreenStatus as LiveData<Boolean>

    private val _gotoSignupPageStatus = MutableLiveData<Boolean>()
    val gotoSignupPageStatus = _gotoSignupPageStatus as LiveData<Boolean>

    private val _gotoForgotPasswordStatus = MutableLiveData<Boolean>()
    val gotoForgotPasswordStatus = _gotoForgotPasswordStatus as LiveData<Boolean>

    private val _gotoNoteFragmentStatus = MutableLiveData<Boolean>()
    val gotoNoteFragmentStatus = _gotoNoteFragmentStatus as LiveData<Boolean>

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

    fun setNoteFragmentPageStatus(status: Boolean){
        _gotoNoteFragmentStatus.value = status
    }

    fun logOutFromApp(){
        Authentication.logOut()
    }











}