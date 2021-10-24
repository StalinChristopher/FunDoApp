package com.bl.todo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bl.todo.models.UserDetails

class SharedViewModel : ViewModel(){
    private val _gotoHomePageStatus = MutableLiveData<UserDetails>()
    val gotoHomePageStatus = _gotoHomePageStatus as LiveData<UserDetails>

    fun setGotoHomePageStatus(userDetails : UserDetails){
        _gotoHomePageStatus.value = userDetails
    }
}