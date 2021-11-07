package com.bl.todo.viewmodels.notePage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bl.todo.models.NewNote
import com.bl.todo.services.Database
import com.bl.todo.wrapper.NoteInfo

class NoteViewModel : ViewModel() {
    private val _addNewNoteStatus = MutableLiveData<Boolean>()
    val addNewNoteStatus = _addNewNoteStatus as LiveData<Boolean>

    private val _updateNoteStatus = MutableLiveData<Boolean>()
    val updateNoteStatus = _updateNoteStatus as LiveData<Boolean>

    private val _deleteNoteStatus = MutableLiveData<Boolean>()
    val deleteNoteStatus = _deleteNoteStatus as LiveData<Boolean>

    fun addNoteToDb(note : NewNote, dateTime : String){
        Database.addNewNote(note, dateTime){
            if(it){
                _addNewNoteStatus.value = it
            }
        }
    }

    fun updateNoteToDb(noteInfo: NoteInfo,dateTime: String){
        Database.updateUserNotes(noteInfo,dateTime){
            if(it){
                _updateNoteStatus.value = it
            }
        }
    }

    fun deleteNoteToDb(noteInfo: NoteInfo){
        Database.deleteUserNotes(noteInfo){
            if(it){
                _deleteNoteStatus.value = it
            }
        }
    }
}