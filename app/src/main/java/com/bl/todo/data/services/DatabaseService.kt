package com.bl.todo.data.services

import android.content.Context
import android.util.Log
import com.bl.todo.ui.wrapper.LabelDetails
import com.bl.todo.ui.wrapper.NoteInfo
import com.bl.todo.ui.wrapper.UserDetails
import com.bl.todo.util.NetworkService
import com.google.firebase.FirebaseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseService(private val context: Context) {
    private var roomDb: RoomDatabaseService = RoomDatabaseService(context)

    companion object {
        private val instance: DatabaseService? by lazy { null }
        fun getInstance(context: Context): DatabaseService = instance ?: DatabaseService(context)
    }

    suspend fun addUserInfoDatabase(user: UserDetails): UserDetails? {
        return withContext(Dispatchers.IO) {
            try {
                var userFromFirebase = FirebaseDatabaseService.getUserData(user.fUid.toString())
                var userFromRoom: UserDetails = roomDb.addUserInfoDatabase(userFromFirebase)
                userFromRoom
            } catch (e: Exception) {
                Log.e("Database", "Database add error")
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun addNewUserInfoDatabase(context: Context, user: UserDetails): UserDetails? {
        return withContext(Dispatchers.IO) {
            try {
                FirebaseDatabaseService.addUserInfoDatabase(user)
                var userFromRoom = roomDb.addUserInfoDatabase(user)
                userFromRoom
            } catch (e: Exception) {
                Log.e("Database", "Database add error")
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun getUserData(uid: Long): UserDetails? {
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

    suspend fun addCloudDataToLocalDB(user: UserDetails): Boolean {
        return withContext(Dispatchers.IO) {
            if (NetworkService.isNetworkConnected(context)) {
                val noteListFromCloud = FirebaseDatabaseService.getUserNotes(user)
                if (noteListFromCloud != null) {
                    for (i in noteListFromCloud) {
                        roomDb.addNewNote(i)
                    }
                }
                true
            } else {
                false
            }
        }
    }

    suspend fun addNewNote(noteInfo: NoteInfo, user: UserDetails): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (NetworkService.isNetworkConnected(context)) {
                    var note = FirebaseDatabaseService.addNewNote(noteInfo, user)
                    roomDb.addNewNote(note)
                } else {
                    roomDb.addNewNote(noteInfo, false)
                }
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
                noteList
            } catch (e: Exception) {
                Log.e("Database", "Read notes for the user failed")
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun getNotesFromCloud(user: UserDetails): List<NoteInfo>? {
        return try {
            return withContext(Dispatchers.IO) {
                var notesList = FirebaseDatabaseService.getUserNotes(user)
                notesList as List<NoteInfo>
            }
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            null
        }
    }

    suspend fun updateUserNotes(noteInfo: NoteInfo, user: UserDetails): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (NetworkService.isNetworkConnected(context)) {
                    roomDb.updateUserNotes(noteInfo)
                    FirebaseDatabaseService.updateUserNotes(noteInfo, user)
                } else {
                    roomDb.updateUserNotes(noteInfo, false)
                }
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
                if (NetworkService.isNetworkConnected(context)) {
                    roomDb.deleteUserNote(noteInfo)
                    FirebaseDatabaseService.deleteUserNotes(noteInfo)
                } else {
                    roomDb.deleteUserNote(noteInfo, false)
                }
                true
            } catch (e: Exception) {
                Log.e("Database", "Delete note failed in data layer")
                false
            }
        }
    }

    suspend fun getOpCode(noteInfo: NoteInfo): Int {
        return withContext(Dispatchers.IO) {
            var opCode: Int = try {
                roomDb.getOperationCode(noteInfo)
            } catch (e: Exception) {
                Log.e("DatabaseService", "OpCode fetch errorr")
                -1
            }
            opCode
        }
    }

    suspend fun clearNoteAndOpTable() {
        return withContext(Dispatchers.IO) {
            roomDb.clearNoteAndOp()
        }
    }

    suspend fun clearAllTables() {
        return withContext(Dispatchers.IO) {
            roomDb.clearAllTables()
        }
    }

    suspend fun addNewNoteToRoomDb(noteInfo: NoteInfo, user: UserDetails) {
        return withContext(Dispatchers.IO) {
            try {
                roomDb.addNewNote(noteInfo)
            } catch (e: Exception) {
                Log.e("Database service", "Add new note failed")
                e.printStackTrace()
            }
        }
    }

    suspend fun addNewLabel(label: LabelDetails, userDetails: UserDetails): LabelDetails? {
        return withContext(Dispatchers.IO) {
            try {
                val labelFb =  FirebaseDatabaseService.addNewLabel(label,userDetails)
                labelFb
            } catch (e : java.lang.Exception) {
                Log.e("Database service", "Add new label failed")
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun getAllLabels(user: UserDetails) : ArrayList<LabelDetails>? {
        return withContext(Dispatchers.IO) {
            try {
                val labelList = FirebaseDatabaseService.getLabels(user)
                labelList
            } catch (e : Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun deleteLabel(label: LabelDetails, userDetails: UserDetails): LabelDetails? {
        return withContext(Dispatchers.IO) {
            try {
                val deletedLabel = FirebaseDatabaseService.deleteLabel(label, userDetails)
                deletedLabel
            } catch (e : Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun editLabel(label: LabelDetails, userDetails: UserDetails): LabelDetails? {
        return withContext(Dispatchers.IO) {
            try {
                val editLabel = userDetails?.let { FirebaseDatabaseService.updateLabel(label, it) }
                editLabel
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun getArchivedNotes(): ArrayList<NoteInfo>? {
        return withContext(Dispatchers.IO) {
            try {
                var noteList = roomDb.getArchivedNotes()
                noteList
            } catch (e: Exception) {
                Log.e("Database", "Read archived notes for the user failed")
                e.printStackTrace()
                null
            }
        }
    }
}