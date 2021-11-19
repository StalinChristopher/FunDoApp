package com.bl.todo.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.todo.auth.service.FirebaseAuthentication
import com.bl.todo.data.services.DatabaseService
import com.bl.todo.data.services.FirebaseImageStorage
import com.bl.todo.data.services.SyncDatabase
import com.bl.todo.ui.wrapper.NoteInfo
import com.bl.todo.ui.wrapper.UserDetails
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeViewModel : ViewModel() {

    private val _userProfilePic = MutableLiveData<Bitmap>()
    val userProfilePic = _userProfilePic as LiveData<Bitmap>

    private val _userNotes = MutableLiveData<ArrayList<NoteInfo>>()
    val userNotes = _userNotes as LiveData<ArrayList<NoteInfo>>

    private val _profileData = MutableLiveData<UserDetails>()
    val profileData = _profileData as LiveData<UserDetails>

    private val _syncStatus = MutableLiveData<Boolean>()
    val syncStatus = _syncStatus as LiveData<Boolean>

    fun logOutFromHomePage(context: Context) {
        viewModelScope.launch {
            FirebaseAuthentication.logOut(context)
        }

    }

    fun setProfilePic(bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                FirebaseImageStorage.setProfileImage(bitmap)
                Log.i("Storage", "Add profile image successful")
                _userProfilePic.postValue(bitmap)
            } catch (e: Exception) {
                Log.e("Storage", "Add profile image failed")
                e.printStackTrace()
            }
        }
    }

    fun getProfilePic() {
        viewModelScope.launch {
            try {
                var resultBitmap = FirebaseImageStorage.getProfileImage()
                _userProfilePic.postValue(resultBitmap)
            } catch (e: Exception) {
                Log.e("Storage", "Get profile image failed")
            }
        }
    }

    fun getNotesFromUser(context: Context) {
        viewModelScope.launch {
            var resultNotes = DatabaseService.getInstance(context).getUserNotes()
            if (resultNotes != null) {
                _userNotes.postValue(resultNotes)
            }
        }
    }

    fun getUserData(context: Context, uid: Long) {
        viewModelScope.launch {
            var userDetails = DatabaseService.getInstance(context).getUserData(uid)
            if (userDetails != null) {
                _profileData.postValue(userDetails)
            }
        }
    }

    fun syncDatabase(context: Context, user: UserDetails) {
        viewModelScope.launch {
            SyncDatabase(context).syncUp(user)
            _syncStatus.postValue(true)

        }
    }
}