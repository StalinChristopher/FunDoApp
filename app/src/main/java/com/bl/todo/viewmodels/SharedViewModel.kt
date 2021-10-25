package com.bl.todo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bl.todo.models.UserDetails

class SharedViewModel : ViewModel(){
    private val _gotoHomePageStatus = MutableLiveData<Boolean>()
    private val _gotoLoginPageStatus = MutableLiveData<Boolean>()
    private val _gotoSplashScreenStatus = MutableLiveData<Boolean>()
    private val _gotoSignupPageStatus = MutableLiveData<Boolean>()


    val gotoHomePageStatus = _gotoHomePageStatus as LiveData<Boolean>
    val gotoLoginPageStatus = _gotoLoginPageStatus as LiveData<Boolean>
    val gotoSplashScreenStatus = _gotoSplashScreenStatus as LiveData<Boolean>
    val gotoSignupPageStatus = _gotoSignupPageStatus as LiveData<Boolean>

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

}