package com.bl.todo.services

import com.bl.todo.models.DatabaseUser
import com.bl.todo.models.NewNote
import com.bl.todo.wrapper.NoteInfo

object DatabaseService {
    fun addUserInfoDatabase(user : DatabaseUser, listener : (Boolean)-> Unit){
        FirebaseDatabaseService.addUserInfoDatabase(user){
            listener(it)
        }
    }

    fun getUserData(listener: (Boolean) -> Unit) {
        FirebaseDatabaseService.getUserData(){
            listener(it)
        }
    }

    fun addNewNote(newNote : NewNote, dateTime : String, listener: (Boolean) -> Unit){
        FirebaseDatabaseService.addNewNote(newNote,dateTime){
            listener(it)
        }
    }

    fun getUserNotes(listener: (ArrayList<NoteInfo>?) -> Unit){
        FirebaseDatabaseService.getUserNotes(){
            listener(it)
        }
    }

    fun updateUserNotes(noteInfo: NoteInfo, dateTime : String, listener: (Boolean) -> Unit){
        FirebaseDatabaseService.updateUserNotes(noteInfo,dateTime){
            listener(it)
        }
    }

    fun deleteUserNotes(noteInfo: NoteInfo,listener: (Boolean) -> Unit){
        FirebaseDatabaseService.deleteUserNotes(noteInfo){
            listener(it)
        }
    }

}