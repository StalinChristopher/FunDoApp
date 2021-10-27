package com.bl.todo.viewmodels.homePage

import androidx.lifecycle.ViewModel
import com.bl.todo.services.Authentication

class HomeViewModel : ViewModel() {

    fun logOutFromHomePage(){
        Authentication.logOut()
    }
}