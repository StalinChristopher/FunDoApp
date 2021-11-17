package com.bl.todo.ui.resetPasswordPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bl.todo.auth.service.FirebaseAuthentication

class ResetPasswordViewModel : ViewModel() {
    private val _resetPasswordStatus = MutableLiveData<Boolean>()
    val resetPasswordStatus = _resetPasswordStatus as LiveData<Boolean>

    fun resetPasswordWithEmail(email: String) {
        FirebaseAuthentication.resetPassword(email) {
            _resetPasswordStatus.value = it
        }
    }

}