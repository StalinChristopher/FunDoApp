package com.bl.todo.viewmodels.registerPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.todo.models.DatabaseUser
import com.bl.todo.models.UserDetails
import com.bl.todo.services.Authentication
import com.bl.todo.services.DatabaseService
import com.bl.todo.services.FirebaseDatabaseService
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {
    private val _signUpStatus = MutableLiveData<Boolean>()
    val signUpStatus = _signUpStatus as LiveData<Boolean>

    fun signUpWithEmailAndPassword(userDetails: UserDetails, password: String, phone : String){
        Authentication.signUpWithEmailAndPassword(userDetails.email,password, phone){
            var userDB = DatabaseUser(userDetails.userName,userDetails.email,userDetails.phone)
            if(it.loginStatus){
                viewModelScope.launch {
                    DatabaseService.addUserInfoDatabase(userDB)
                    _signUpStatus.postValue(it.loginStatus)
                }
            }else{
                _signUpStatus.value = it.loginStatus
            }


        }
    }

}