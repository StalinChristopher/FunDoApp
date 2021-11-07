package com.bl.todo.viewmodels.homePage

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bl.todo.models.NewNote
import com.bl.todo.services.Authentication
import com.bl.todo.services.Database
import com.bl.todo.services.Storage
import com.bl.todo.wrapper.NoteInfo

class HomeViewModel : ViewModel() {

    private val _userProfilePic  = MutableLiveData<Bitmap>()
    val userProfilePic = _userProfilePic as LiveData<Bitmap>

    private val _userNotes = MutableLiveData<ArrayList<NoteInfo>>()
    val userNotes = _userNotes as LiveData<ArrayList<NoteInfo>>

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

    fun getNotesFromUser(){
        Database.getUserNotes {
            if(it != null){
                _userNotes.value = it
            }
        }
    }
}