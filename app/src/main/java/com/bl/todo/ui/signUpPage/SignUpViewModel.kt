package com.bl.todo.ui.signUpPage

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.todo.data.models.DatabaseUser
import com.bl.todo.data.wrapper.UserDetails
import com.bl.todo.authService.Authentication
import com.bl.todo.data.services.DatabaseService
import com.bl.todo.util.SharedPref
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {
    private val _signUpStatus = MutableLiveData<Boolean>()
    val signUpStatus = _signUpStatus as LiveData<Boolean>

    fun signUpWithEmailAndPassword( context: Context, userDetails: UserDetails, password: String){
        Authentication.signUpWithEmailAndPassword(userDetails.email,password){
            if(it.loginStatus){
                viewModelScope.launch {
                    userDetails.fUid = it.fUid
                    var user = DatabaseService.addNewUserInfoDatabase( context, userDetails)
                    if(user != null){
                         SharedPref.addUserId(user.uid)
                        _signUpStatus.postValue(it.loginStatus)
                    }
                }
            }else{
                _signUpStatus.value = it.loginStatus
            }


        }
    }

}