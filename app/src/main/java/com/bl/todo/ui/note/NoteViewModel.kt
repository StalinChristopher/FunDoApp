package com.bl.todo.ui.note

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.todo.data.services.DatabaseService
import com.bl.todo.ui.wrapper.NoteInfo
import com.bl.todo.ui.wrapper.UserDetails
import kotlinx.coroutines.launch
import java.util.*

class NoteViewModel : ViewModel() {
    private val _addNewNoteStatus = MutableLiveData<Boolean>()
    val addNewNoteStatus = _addNewNoteStatus as LiveData<Boolean>

    private val _updateNoteStatus = MutableLiveData<Boolean>()
    val updateNoteStatus = _updateNoteStatus as LiveData<Boolean>

    private val _deleteNoteStatus = MutableLiveData<Boolean>()
    val deleteNoteStatus = _deleteNoteStatus as LiveData<Boolean>

    private val _profileData = MutableLiveData<UserDetails>()
    val profileData = _profileData as LiveData<UserDetails>

    fun addNoteToDb(context: Context, note: NoteInfo, user: UserDetails) {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            val dateTime = cal.time
            note.dateModified = dateTime
            val result = DatabaseService.getInstance(context).addNewNote(noteInfo = note, user)
            if (result) {
                _addNewNoteStatus.postValue(result)
            }
        }
    }

    fun updateNoteToDb(context: Context, noteInfo: NoteInfo, user: UserDetails) {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            var dateTime = cal.time
            noteInfo.dateModified = dateTime
            val resultStatus = DatabaseService.getInstance(context).updateUserNotes(noteInfo, user)
            if (resultStatus) {
                _updateNoteStatus.postValue(resultStatus)
            }
        }
    }

    fun deleteNoteToDb(context: Context, noteInfo: NoteInfo) {
        viewModelScope.launch {
            val status = DatabaseService.getInstance(context).deleteUserNotes(noteInfo)
            if (status)
                _deleteNoteStatus.postValue(status)
        }
    }

    fun getUserData(context: Context, uid: Long) {
        viewModelScope.launch {
            val userDetails = DatabaseService.getInstance(context).getUserData(uid)
            if (userDetails != null) {
                _profileData.postValue(userDetails)
            }
        }
    }
}