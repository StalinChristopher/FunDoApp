package com.bl.todo.ui.notePage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.todo.data.wrapper.NewNote
import com.bl.todo.data.services.DatabaseService
import com.bl.todo.data.wrapper.NoteInfo
import com.bl.todo.data.wrapper.UserDetails
import com.bl.todo.util.SharedPref
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

    fun addNoteToDb(note : NoteInfo, user : UserDetails){
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            var dateTime = cal.time
            note.dateModified = dateTime
            var result = DatabaseService.addNewNote(noteInfo = note,user)
            if(result) {
                _addNewNoteStatus.postValue(result)
            }
        }
    }

    fun updateNoteToDb(noteInfo: NoteInfo,user: UserDetails){
        viewModelScope.launch {
            var uid = SharedPref.getUserId()
            val cal = Calendar.getInstance()
            var dateTime = cal.time
            noteInfo.dateModified = dateTime
            var resultStatus = DatabaseService.updateUserNotes(noteInfo,user)
            if(resultStatus){
                _updateNoteStatus.postValue(resultStatus)
            }
        }
    }

    fun deleteNoteToDb(noteInfo: NoteInfo){
        viewModelScope.launch {
            var status = DatabaseService.deleteUserNotes(noteInfo)
            if(status)
                _deleteNoteStatus.postValue(status)
        }
    }

    fun getUserData(uid : Long){
        viewModelScope.launch {
            var userDetails = DatabaseService.getUserData(uid)
            if(userDetails != null){
                _profileData.postValue(userDetails)
            }
        }
    }
}