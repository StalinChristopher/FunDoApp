package com.bl.todo.data.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.bl.todo.ui.wrapper.NoteInfo
import com.bl.todo.ui.wrapper.UserDetails
import com.bl.todo.common.DELETE_OP_CODE
import com.bl.todo.common.NetworkService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncDatabase(private val context: Context) {

    suspend fun syncUp(user: UserDetails) {
        if(NetworkService.isNetworkConnected(context)){
            val latestNotes = getLatestNotesFromCloud(user)
            DatabaseService.getInstance(context).clearNoteAndOpTable()
            latestNotes.forEach {
                DatabaseService.getInstance(context).addNewNoteToRoomDb(it, user)
            }
        } else {
            Toast.makeText(context,"No internet connection", Toast.LENGTH_SHORT).show()
        }

    }

    private suspend fun getLatestNotesFromCloud(user: UserDetails): List<NoteInfo> {
        return withContext(Dispatchers.IO) {
            val tempNotesList = DatabaseService.getInstance(context)
                .getUserNotes()?.toMutableList() ?: mutableListOf()
            val cloudNotesList = DatabaseService.getInstance(context).getNotesFromCloud(user)
            Log.i("Sync", "temp : $tempNotesList")
            Log.i("Sync", "cloud : $cloudNotesList")
            val latestNotes = mutableListOf<NoteInfo>()
            if (cloudNotesList != null) {
                for (cloudNote in cloudNotesList) {
                    var localNoteIndexCounter = 0
                    for (localNote in tempNotesList) {
                        if (cloudNote.fnid == localNote.fnid) {
                            val res = compareTimeStamp(localNote, cloudNote)
                            if (res) {
                                latestNotes.add(localNote)
                                Log.i("Sync", "$localNote")
                                FirebaseDatabaseService.updateUserNotes(localNote, user)
                            } else {
                                latestNotes.add(cloudNote)
                            }
                            break
                        }
                        localNoteIndexCounter++
                    }
                    if (localNoteIndexCounter == tempNotesList.size) {
                        tempNotesList.add(cloudNote)
                        latestNotes.add(cloudNote)
                    }
                }

                for (localNote in tempNotesList) {
                    var cloudNoteIndexCounter = 0
                    for (cloudNote in cloudNotesList) {
                        if (localNote.fnid == cloudNote.fnid) {
                            if (getOpCode(localNote) == DELETE_OP_CODE) {
                                FirebaseDatabaseService.deleteUserNotes(localNote)
                                latestNotes.remove(localNote)
                            }
                            break
                        }
                        cloudNoteIndexCounter++
                    }
                    if (cloudNoteIndexCounter == cloudNotesList.size) {
                        val opCode = getOpCode(localNote)
                        if (opCode != -1) {
                            latestNotes.add(localNote)
                            FirebaseDatabaseService.addNewNote(localNote, user)
                        }
                    }
                }
                return@withContext latestNotes
            } else {
                return@withContext listOf<NoteInfo>()
            }
        }

    }

    private fun compareTimeStamp(localNote: NoteInfo, cloudNote: NoteInfo): Boolean {
        val localDate = localNote.dateModified
        val cloudDate = cloudNote.dateModified
        return if (localDate != null && cloudDate != null) {
            localDate.after(cloudDate)
        } else {
            false
        }
    }

    private suspend fun getOpCode(noteInfo: NoteInfo): Int {
        return withContext(Dispatchers.IO) {
            var opCode = DatabaseService.getInstance(context).getOpCode(noteInfo)
            opCode
        }
    }


}