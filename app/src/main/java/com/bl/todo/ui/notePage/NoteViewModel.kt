package com.bl.todo.ui.notePage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.todo.data.wrapper.NewNote
import com.bl.todo.data.services.DatabaseService
import com.bl.todo.data.wrapper.NoteInfo
import kotlinx.coroutines.launch

class NoteViewModel : ViewModel() {
    private val _addNewNoteStatus = MutableLiveData<Boolean>()
    val addNewNoteStatus = _addNewNoteStatus as LiveData<Boolean>

    private val _updateNoteStatus = MutableLiveData<Boolean>()
    val updateNoteStatus = _updateNoteStatus as LiveData<Boolean>

    private val _deleteNoteStatus = MutableLiveData<Boolean>()
    val deleteNoteStatus = _deleteNoteStatus as LiveData<Boolean>

    fun addNoteToDb(note : NewNote, dateTime : String){
        viewModelScope.launch {
            var result = DatabaseService.addNewNote(note, dateTime)
            if(result) {
                _addNewNoteStatus.postValue(result)
            }
        }
    }

    fun updateNoteToDb(noteInfo: NoteInfo,dateTime: String){
        viewModelScope.launch {
            var resultStatus = DatabaseService.updateUserNotes(noteInfo,dateTime)
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
}