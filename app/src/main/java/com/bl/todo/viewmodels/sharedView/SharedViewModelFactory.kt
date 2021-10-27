package com.bl.todo.viewmodels.sharedView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SharedViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SharedViewModel() as T
    }

}
