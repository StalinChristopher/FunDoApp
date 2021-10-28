package com.bl.todo.viewmodels.homePage

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bl.todo.services.Authentication
import com.bl.todo.services.Storage

class HomeViewModel : ViewModel() {

    private val _userProfilePic  = MutableLiveData<Bitmap>()
    val userProfilePic = _userProfilePic as LiveData<Bitmap>

    fun logOutFromHomePage(){
        Authentication.logOut()
    }

    fun setProfilePic(bitmap: Bitmap){
        Storage.addProfileImage(bitmap){
            if(it) {
                _userProfilePic.value = bitmap
            }
        }
    }

    fun getProfilePic(){
        Storage.getProfileImage {
            if(it != null){
                _userProfilePic.value = it
            }
        }
    }
}