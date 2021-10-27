package com.bl.todo.viewmodels.resetPasswordPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ResetPasswordViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ResetPasswordViewModel() as T
    }
}