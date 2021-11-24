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

    private val _pagedNotes = MutableLiveData<ArrayList<NoteInfo>>()
    val pagedNotes = _pagedNotes as LiveData<ArrayList<NoteInfo>>

    private val _notesCount = MutableLiveData<Int>()
    val notesCount = _notesCount as LiveData<Int>

    private val _profileData = MutableLiveData<UserDetails>()
    val profileData = _profileData as LiveData<UserDetails>

    private val _syncStatus = MutableLiveData<Boolean>()
    val syncStatus = _syncStatus as LiveData<Boolean>

    private val _archivedNotes = MutableLiveData<ArrayList<NoteInfo>>()
    val archivedNotes = _archivedNotes as LiveData<ArrayList<NoteInfo>>

    private val _pagedArchivedNotes = MutableLiveData<ArrayList<NoteInfo>>()
    val pagedArchivedNotes = _pagedArchivedNotes as LiveData<ArrayList<NoteInfo>>

    private val _reminderNotes = MutableLiveData<ArrayList<NoteInfo>>()
    val reminderNotes = _reminderNotes as LiveData<ArrayList<NoteInfo>>

    private val _pagedReminderNotes = MutableLiveData<ArrayList<NoteInfo>>()
    val pagedReminderNotes = _pagedReminderNotes as LiveData<ArrayList<NoteInfo>>

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
            val resultNotes = DatabaseService.getInstance(context).getPagedNotes(10,0)
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

    fun getArchivedNotes(context: Context) {
        viewModelScope.launch {
            val resultNotes = DatabaseService.getInstance(context).getPagedArchivedNotes(10,0)
            if(resultNotes != null) {
                _archivedNotes.postValue(resultNotes)
            }
        }
    }

    fun getReminderNotes(context: Context) {
        viewModelScope.launch {
            val resultNotes = DatabaseService.getInstance(context).getPagedReminderNotes(10,0)
            if(resultNotes != null) {
                _reminderNotes.postValue(resultNotes)
            }
        }
    }

    fun getPagedNotes(context: Context, limit: Int, offset: Int) {
        viewModelScope.launch {
            val resultNotes = DatabaseService.getInstance(context).getPagedNotes(limit,offset)
            if (resultNotes != null) {
                _pagedNotes.postValue(resultNotes)
            }
        }
    }

    fun getNotesCount(context: Context) {
        viewModelScope.launch {
            val count = DatabaseService.getInstance(context).getNotesCount()
            _notesCount.postValue(count)
        }
    }

    fun getPagedArchivedNotes(context: Context, limit: Int, offset: Int) {
        viewModelScope.launch {
            val resultNotes = DatabaseService.getInstance(context).getPagedArchivedNotes(limit,offset)
            if(resultNotes != null) {
                _pagedArchivedNotes.postValue(resultNotes)
            }
        }
    }

    fun getPagedReminderNotes(context: Context, limit: Int, offset: Int) {
        viewModelScope.launch {
            val resultNotes = DatabaseService.getInstance(context).getPagedReminderNotes(limit,offset)
            if(resultNotes != null) {
                _pagedReminderNotes.postValue(resultNotes)
            }
        }
    }
}