package com.bl.todo.ui.homePage

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.todo.authService.Authentication
import com.bl.todo.data.services.DatabaseService
import com.bl.todo.data.services.Storage
import com.bl.todo.data.wrapper.NoteInfo
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeViewModel : ViewModel() {

    private val _userProfilePic  = MutableLiveData<Bitmap>()
    val userProfilePic = _userProfilePic as LiveData<Bitmap>

    private val _userNotes = MutableLiveData<ArrayList<NoteInfo>>()
    val userNotes = _userNotes as LiveData<ArrayList<NoteInfo>>

    fun logOutFromHomePage(){
        Authentication.logOut()
    }

    fun setProfilePic(bitmap: Bitmap){
        viewModelScope.launch {
            try{
                Storage.addProfileImage(bitmap)
                Log.i("Storage","Add profile image successful")
                _userProfilePic.postValue(bitmap)
            }catch (e : Exception){
                Log.e("Storage","Add profile image failed")
                e.printStackTrace()
            }
        }
    }

    fun getProfilePic(){
        viewModelScope.launch {
            try{
                var resultBitmap = Storage.getProfileImage()
                _userProfilePic.postValue(resultBitmap)
            }catch (e : Exception){
                Log.e("Storage","Get profile image failed")
            }
        }
    }

    fun getNotesFromUser(){
        viewModelScope.launch {
            var resultNotes = DatabaseService.getUserNotes()
            if(resultNotes != null){
                _userNotes.postValue(resultNotes)
            }
        }
    }
}