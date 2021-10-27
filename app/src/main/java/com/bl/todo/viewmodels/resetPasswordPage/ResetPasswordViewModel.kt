package com.bl.todo.viewmodels.resetPasswordPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bl.todo.services.Authentication

class ResetPasswordViewModel : ViewModel() {
    private val _resetPasswordStatus = MutableLiveData<Boolean>()
    val resetPasswordStatus = _resetPasswordStatus as LiveData<Boolean>

    fun resetPasswordWithEmail(email : String){
        Authentication.resetPassword(email){
            _resetPasswordStatus.value = it
        }
    }

}