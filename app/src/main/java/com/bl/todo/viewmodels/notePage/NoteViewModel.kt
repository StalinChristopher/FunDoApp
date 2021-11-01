package com.bl.todo.viewmodels.notePage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bl.todo.models.NewNote
import com.bl.todo.services.Database

class NoteViewModel : ViewModel() {
    private val _addNewNoteStatus = MutableLiveData<Boolean>()
    val addNewNoteStatus = _addNewNoteStatus as LiveData<Boolean>

    fun addNoteToDb(note : NewNote, dateTime : String){
        Database.addNewNote(note, dateTime){
            if(it){
                _addNewNoteStatus.value = it
            }
        }
    }
}