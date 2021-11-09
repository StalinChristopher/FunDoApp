package com.bl.todo.viewmodels.registerPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bl.todo.models.DatabaseUser
import com.bl.todo.models.UserDetails
import com.bl.todo.services.Authentication
import com.bl.todo.services.DatabaseService
import com.bl.todo.services.FirebaseDatabaseService

class SignUpViewModel : ViewModel() {
    private val _signUpStatus = MutableLiveData<Boolean>()
    val signUpStatus = _signUpStatus as LiveData<Boolean>

    fun signUpWithEmailAndPassword(userDetails: UserDetails, password: String, phone : String){
        Authentication.signUpWithEmailAndPassword(userDetails.email,password, phone){
            var userDB = DatabaseUser(userDetails.userName,userDetails.email,userDetails.phone)
            DatabaseService.addUserInfoDatabase(userDB){
                _signUpStatus.value = it
            }

        }
    }

}