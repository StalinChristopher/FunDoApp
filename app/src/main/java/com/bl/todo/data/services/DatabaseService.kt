package com.bl.todo.data.services

import android.content.Context
import android.util.Log
import com.bl.todo.data.models.DatabaseUser
import com.bl.todo.data.wrapper.NewNote
import com.bl.todo.data.wrapper.NoteInfo
import com.bl.todo.data.wrapper.UserDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseService {
    private lateinit var roomDb : RoomDatabaseService
    fun initializeDbService(context : Context){
        roomDb = RoomDatabaseService(context)

    }
    suspend fun addUserInfoDatabase(user: UserDetails) : UserDetails?{
        return withContext(Dispatchers.IO) {
            try {
                var userFromFirebase = FirebaseDatabaseService.getUserData(user.fUid.toString())
                var userFromRoom : UserDetails? = null
                if(userFromFirebase != null){
                   userFromRoom =  roomDb.addUserInfoDatabase(userFromFirebase)
                }
                userFromRoom
            } catch (e: Exception) {
                Log.e("Database", "Database add error")
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun addNewUserInfoDatabase(user : UserDetails) : UserDetails?{
        return withContext(Dispatchers.IO){
            try{
                FirebaseDatabaseService.addUserInfoDatabase(user)
                var userFromRoom = roomDb.addUserInfoDatabase(user)
                userFromRoom
            }catch (e : Exception){
                Log.e("Database", "Database add error")
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun getUserData(uid : Long) : UserDetails? {
        return withContext(Dispatchers.IO) {
            try {
                var user = roomDb.getUserData(uid)
                user
            } catch (e: Exception) {
                Log.e("Database", "Database read error")
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun addCloudDataToLocalDB(user: UserDetails) : Boolean {
        return withContext(Dispatchers.IO){
            val noteListFromCloud = FirebaseDatabaseService.getUserNotes(user)
            if (noteListFromCloud != null) {
                for( i in noteListFromCloud){
                    roomDb.addNewNote(i)
                }
            }
            true
        }
    }

    suspend fun addNewNote(noteInfo: NoteInfo, user : UserDetails): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                var note =FirebaseDatabaseService.addNewNote(noteInfo, user)
                roomDb.addNewNote(note)
                true
            } catch (e: Exception) {
                Log.e("Database service", "Add new note failed")
                e.printStackTrace()
                false
            }

        }
    }

    suspend fun getUserNotes(): ArrayList<NoteInfo>? {
        return withContext(Dispatchers.IO) {
            try {
                var noteList = roomDb.getUserNotes()
                for(i in noteList){
                    Log.e("UserNotes","$i")
                }
//                var notesList = FirebaseDatabaseService.getUserNotes()
                noteList
            } catch (e: Exception) {
                Log.e("Database", "Read notes for the user failed")
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun updateUserNotes(noteInfo: NoteInfo, user: UserDetails): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                roomDb.updateUserNotes(noteInfo)
                FirebaseDatabaseService.updateUserNotes(noteInfo, user)
                true
            } catch (e: Exception) {
                Log.e("Database", "update notes failed in Data layer")
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun deleteUserNotes(noteInfo: NoteInfo): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                roomDb.deleteUserNote(noteInfo)
                FirebaseDatabaseService.deleteUserNotes(noteInfo)
                true
            } catch (e: Exception) {
                Log.e("Database", "Delete note failed in data layer")
                false
            }
        }
    }

}