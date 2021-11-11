package com.bl.todo.services

import android.util.Log
import com.bl.todo.models.DatabaseUser
import com.bl.todo.models.NewNote
import com.bl.todo.wrapper.NoteInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseService {
    suspend fun addUserInfoDatabase(user : DatabaseUser){
        withContext(Dispatchers.IO){
            try{
                FirebaseDatabaseService.addUserInfoDatabase(user)
            }catch (e : Exception){
                Log.e("Database","Database add error")
                e.printStackTrace()
            }

        }
    }

    suspend fun getUserData() {
        withContext(Dispatchers.IO){
            try{
                FirebaseDatabaseService.getUserData()
            }catch(e : Exception){
                Log.e("Database","Database read error")
                e.printStackTrace()
            }
        }
    }

    suspend fun addNewNote(newNote : NewNote, dateTime : String) : Boolean{
        return withContext(Dispatchers.IO){
            try{
                FirebaseDatabaseService.addNewNote(newNote,dateTime)
                true
            }catch (e : Exception){
                Log.e("Database service","Add new note failed")
                e.printStackTrace()
                false
            }

        }
    }

    suspend fun getUserNotes() : ArrayList<NoteInfo>?{
        return withContext(Dispatchers.IO){
            try{
                var notesList = FirebaseDatabaseService.getUserNotes()
                notesList
            }catch (e : Exception){
                Log.e("Database","Read notes for the user failed")
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun updateUserNotes(noteInfo: NoteInfo, dateTime : String) : Boolean{
        return withContext(Dispatchers.IO){
            try{
                FirebaseDatabaseService.updateUserNotes(noteInfo,dateTime)
                true
            }catch (e : Exception){
                Log.e("Database","update notes failed in Data layer")
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun deleteUserNotes(noteInfo: NoteInfo) : Boolean{
        return withContext(Dispatchers.IO){
            try{
                FirebaseDatabaseService.deleteUserNotes(noteInfo)
                true
            }catch (e : Exception){
                Log.e("Database","Delete note failed in data layer")
                false
            }
        }
    }

}