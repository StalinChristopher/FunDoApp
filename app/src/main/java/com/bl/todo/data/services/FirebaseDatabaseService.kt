package com.bl.todo.data.services

import android.util.Log
import com.bl.todo.auth.service.FirebaseAuthentication
import com.bl.todo.data.models.FirebaseNewNote
import com.bl.todo.data.models.FirebaseUser
import com.bl.todo.data.room.DateTypeConverters
import com.bl.todo.util.Utilities
import com.bl.todo.ui.wrapper.NoteInfo
import com.bl.todo.ui.wrapper.UserDetails
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.suspendCoroutine

object FirebaseDatabaseService {
    private var db = Firebase.firestore

    suspend fun addUserInfoDatabase(user: UserDetails): Boolean {
        var userId = FirebaseAuthentication.getCurrentUser()?.uid.toString()
        return suspendCoroutine { callback ->
            var dbUser = FirebaseUser(user.userName, user.email, user.phone)
            db.collection("users").document(userId).set(dbUser).addOnCompleteListener {
                if (it.isSuccessful) {
                    Utilities.addUserInfoToSharedPref(dbUser)
                    callback.resumeWith(Result.success(true))
                } else {
                    callback.resumeWith(Result.failure(it.exception!!))
                }
            }
        }
    }

    suspend fun getUserData(fUid: String): UserDetails {
        Log.e("UserId", "$fUid")
        return suspendCoroutine { callback ->
            db.collection("users").document(fUid).get().addOnCompleteListener { status ->
                if (status.isSuccessful) {
                    status.result?.also {
                        Log.e("DB", "User : $fUid")
                        Log.i("DB", "User : ${it?.data}")
                        var userDb: FirebaseUser = Utilities.createUserFromHashMap(
                            it.data as HashMap<*, *>
                        )
                        var user =
                            UserDetails(userDb.userName, userDb.email, userDb.phone, fUid = fUid)
                        callback.resumeWith(Result.success(user))
                    }
                } else {
                    Log.e("DB", "Read Failed")
                    Log.e("DB", status.exception.toString())
                    callback.resumeWith(Result.failure(status.exception!!))
                }
            }
        }
    }

    suspend fun addNewNote(noteInfo: NoteInfo, userDetails: UserDetails): NoteInfo {
        var dateTime = DateTypeConverters().fromOffsetDateTime(noteInfo.dateModified)
        Log.e("TimeDate", "$dateTime")
        var databaseNewNote = FirebaseNewNote(noteInfo.title, noteInfo.content, dateTime)
        return suspendCoroutine { callback ->
            val userId = userDetails.fUid.toString()
            val autoId = db.collection("users").document(userId)
                .collection("notes").document().id
            db.collection("users").document(userId).collection("notes")
                .document(autoId).set(databaseNewNote).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.i("Database", "Note added successfully")
                        noteInfo.fnid = autoId
                        callback.resumeWith(Result.success(noteInfo))
                    } else {
                        Log.e("Database error", "Adding new note failed")
                        callback.resumeWith(Result.failure(it.exception!!))
                    }
                }
        }
    }

    suspend fun getUserNotes(user: UserDetails): ArrayList<NoteInfo>? {
        var noteList = ArrayList<NoteInfo>()
        return suspendCoroutine { callback ->
            db.collection("users").document(user.fUid.toString())
                .collection("notes").get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        var dataSnapshot = it.result
                        if (dataSnapshot != null) {
                            for (item in dataSnapshot.documents) {
                                var noteMap = item.data as HashMap<*, *>
                                var title = noteMap["title"].toString()
                                var content = noteMap["content"].toString()
                                var dateModified = noteMap["dateModified"].toString()
                                Log.e("DateFb", dateModified)
                                var dateTime = DateTypeConverters().toOffsetDateTime(dateModified)
                                var key = item.id
                                Log.e("NoteGet", "$key")
                                var note =
                                    NoteInfo(title, content, fnid = key, dateModified = dateTime)
                                noteList.add(note)
                                Log.e("Sync", "noteLoop : $note")
                            }
                            Log.e("Sync", "noteList cloud : $noteList")
                            callback.resumeWith(Result.success(noteList))
                        } else {
                            Log.e("Database", "No notes present for the current user")
                            callback.resumeWith((Result.failure(it.exception!!)))
                        }
                    } else {
                        Log.i("Database error", "Notes could not be fetched")
                        callback.resumeWith(Result.failure(it.exception!!))
                    }
                }
        }
    }

    suspend fun updateUserNotes(noteInfo: NoteInfo, user: UserDetails): Boolean {
//      var userId = Authentication.getCurrentUser()?.uid.toString()
        var dateTime = DateTypeConverters().fromOffsetDateTime(noteInfo.dateModified)
        val noteMap = mapOf(
            "title" to noteInfo.title,
            "content" to noteInfo.content,
            "dateModified" to dateTime
        )
        return suspendCoroutine { callback ->
            db.collection("users").document(user.fUid.toString())
                .collection("notes").document(noteInfo.fnid)
                .update(noteMap)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback.resumeWith(Result.success(true))
                    } else {
                        Log.e("Database", "Update note failed")
                        callback.resumeWith(Result.failure(it.exception!!))
                    }
                }
        }
    }

    suspend fun deleteUserNotes(noteInfo: NoteInfo): Boolean {
        var userId = FirebaseAuthentication.getCurrentUser()?.uid.toString()
        return suspendCoroutine { callback ->
            db.collection("users").document(userId)
                .collection("notes").document(noteInfo.fnid.toString()).delete()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback.resumeWith(Result.success(true))
                    } else {
                        Log.e("Database", "Delete note failed")
                        Log.e("Database", it.exception.toString())
                        callback.resumeWith(Result.failure(it.exception!!))
                    }
                }
        }
    }
}