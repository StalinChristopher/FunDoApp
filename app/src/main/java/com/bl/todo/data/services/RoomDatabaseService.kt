package com.bl.todo.data.services

import android.content.Context
import android.util.Log
import com.bl.todo.data.room.LocalDatabase
import com.bl.todo.data.room.entities.NoteEntity
import com.bl.todo.data.room.entities.OpEntity
import com.bl.todo.data.room.entities.UserEntity
import com.bl.todo.ui.wrapper.NoteInfo
import com.bl.todo.ui.wrapper.UserDetails
import com.bl.todo.util.CREATE_OP_CODE
import com.bl.todo.util.DELETE_OP_CODE
import com.bl.todo.util.UPDATE_OP_CODE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.collections.ArrayList

class RoomDatabaseService(context: Context) {
    private val localDatabase = LocalDatabase.getInstance(context)
    private val userDao = localDatabase.userDao()
    private val noteDao = localDatabase.noteDao()
    private val opDao = localDatabase.opDao()

    suspend fun addUserInfoDatabase(user: UserDetails): UserDetails {
        return withContext(Dispatchers.IO) {
            var userEntity = UserEntity(
                fid = user.fUid, userName = user.userName,
                email = user.email, phone = user.phone
            )
            user.uid = userDao.addUserData(userEntity)
            user
        }
    }

    suspend fun getUserData(uid: Long): UserDetails {
        return withContext(Dispatchers.IO) {
            var userEntity = userDao.getUserData(uid)
            var user = UserDetails(
                userName = userEntity.userName,
                email = userEntity.email,
                phone = userEntity.phone,
                uid = userEntity.uid,
                fUid = userEntity.fid
            )
            user
        }
    }

    suspend fun addNewNote(noteInfo: NoteInfo, onlineStatus: Boolean = true): NoteInfo {
        return withContext(Dispatchers.IO) {
            var noteEntity = NoteEntity(
                fNoteId = noteInfo.fnid, title = noteInfo.title,
                content = noteInfo.content, dateModified = noteInfo.dateModified
            )
            noteInfo.nid = noteDao.addNewNote(noteEntity)
            if (!onlineStatus) {
                var opEntity = OpEntity(fNid = noteInfo.fnid, opCode = CREATE_OP_CODE)
                opDao.addOp(opEntity)
            }
            noteInfo
        }
    }

    suspend fun getUserNotes(): ArrayList<NoteInfo> {
        var notesList: ArrayList<NoteInfo> = ArrayList()
        return withContext(Dispatchers.IO) {
            var resultList: ArrayList<NoteEntity> = noteDao.getUserNotes() as ArrayList<NoteEntity>
            for (i in resultList) {
                var noteInfo = NoteInfo(
                    title = i.title,
                    content = i.content,
                    fnid = i.fNoteId,
                    nid = i.id,
                    dateModified = i.dateModified
                )
                notesList.add(noteInfo)
            }
            notesList
        }
    }

    suspend fun updateUserNotes(noteInfo: NoteInfo, onlineStatus: Boolean = true): NoteInfo? {
        return withContext(Dispatchers.IO) {
            Log.i("updateRoom", "$noteInfo")
            var noteEntity = NoteEntity(
                fNoteId = noteInfo.fnid, title = noteInfo.title,
                content = noteInfo.content, dateModified = noteInfo.dateModified, id = noteInfo.nid
            )
            noteDao.updateUserNotes(noteEntity)
            if (!onlineStatus) {
                var opEntity = OpEntity(fNid = noteInfo.fnid, opCode = UPDATE_OP_CODE)
                opDao.addOp(opEntity)
            }
            noteInfo
        }
    }

    suspend fun deleteUserNote(noteInfo: NoteInfo, onlineStatus: Boolean = true): NoteInfo {
        return withContext(Dispatchers.IO) {
            var noteEntity = NoteEntity(
                fNoteId = noteInfo.fnid, title = noteInfo.title,
                content = noteInfo.content, dateModified = noteInfo.dateModified, id = noteInfo.nid
            )
            noteDao.deleteUserNotes(noteEntity)
            if (!onlineStatus) {
                if (noteInfo.fnid.isNotEmpty()) {
                    var opEntity = OpEntity(fNid = noteInfo.fnid, opCode = DELETE_OP_CODE)
                    opDao.addOp(opEntity)
                }
            }
            noteInfo
        }
    }

    suspend fun getOperationCode(noteInfo: NoteInfo): Int {
        return withContext(Dispatchers.IO) {
            val opEntity = opDao.getOpCode(noteInfo.fnid!!)
            var opCode: Int = if (opEntity != null) {
                opEntity.opCode
            } else {
                -1
            }
            opCode
        }
    }

    suspend fun clearNoteAndOp() {
        noteDao.clearNoteTable()
        opDao.deleteAllOps()
    }

    fun clearAllTables() {
        localDatabase.clearAllTables()
    }
}