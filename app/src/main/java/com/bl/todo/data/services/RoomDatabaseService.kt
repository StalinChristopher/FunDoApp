package com.bl.todo.data.services

import android.content.Context
import android.util.Log
import com.bl.todo.authService.Authentication
import com.bl.todo.data.models.DatabaseUser
import com.bl.todo.data.room.LocalDatabase
import com.bl.todo.data.room.entities.NoteEntity
import com.bl.todo.data.room.entities.UserEntity
import com.bl.todo.data.wrapper.NoteInfo
import com.bl.todo.data.wrapper.UserDetails
import com.bl.todo.util.Utilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class RoomDatabaseService(context: Context) {
    private val localDatabase = LocalDatabase.getInstance(context)
    private val userDao = localDatabase.userDao()
    private val noteDao = localDatabase.noteDao()

    suspend fun addUserInfoDatabase(user : UserDetails) : UserDetails{
        return withContext(Dispatchers.IO){
            var userEntity = UserEntity(fid = user.fUid,userName = user.userName,
                email = user.email,phone=user.phone)
            user.uid = userDao.addUserData(userEntity)
            user
        }
    }

    suspend fun getUserData(uid : Long) : UserDetails{
        var userId = Authentication.getCurrentUser()?.uid.toString()
        return withContext(Dispatchers.IO){
            var userEntity = userDao.getUserData(uid)
            var user = UserDetails(userName = userEntity.userName,email = userEntity.email,phone = userEntity.phone
                ,uid = userEntity.uid,fUid = userEntity.fid)
            user
        }
    }

    suspend fun addNewNote(noteInfo: NoteInfo) : NoteInfo{
        return withContext(Dispatchers.IO){
            var noteEntity = NoteEntity(fNoteId = noteInfo.fnid,title = noteInfo.title,
                content = noteInfo.content,dateModified = noteInfo.dateModified)
            noteInfo.nid = noteDao.addNewNote(noteEntity)
            noteInfo
        }
    }

    suspend fun getUserNotes() : ArrayList<NoteInfo>{
        var notesList : ArrayList<NoteInfo> = ArrayList()
        return withContext(Dispatchers.IO){
            var resultList : ArrayList<NoteEntity> = noteDao.getUserNotes() as ArrayList<NoteEntity>
            for(i in resultList){
                var noteInfo = NoteInfo(title = i.title,content = i.content
                    ,fnid = i.fNoteId,nid = i.id, dateModified = i.dateModified)
                notesList.add(noteInfo)
            }
            notesList
        }
    }

    suspend fun updateUserNotes(noteInfo: NoteInfo) : NoteInfo? {
        return withContext(Dispatchers.IO){
            Log.i("updateRoom","$noteInfo")
            var noteEntity = NoteEntity(fNoteId = noteInfo.fnid,title = noteInfo.title,
                content = noteInfo.content,dateModified = noteInfo.dateModified,id = noteInfo.nid)
            noteDao.updateUserNotes(noteEntity)
            noteInfo
        }
    }

    suspend fun deleteUserNote(noteInfo: NoteInfo) : NoteInfo {
        return withContext(Dispatchers.IO){
            var noteEntity = NoteEntity(fNoteId = noteInfo.fnid, title = noteInfo.title,
            content = noteInfo.content, dateModified = noteInfo.dateModified, id = noteInfo.nid)
            noteDao.deleteUserNotes(noteEntity)
            noteInfo
        }
    }
}