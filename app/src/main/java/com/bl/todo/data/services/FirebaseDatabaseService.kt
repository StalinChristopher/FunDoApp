package com.bl.todo.data.services

import android.util.Log
import com.bl.todo.auth.service.FirebaseAuthentication
import com.bl.todo.data.models.FirebaseLabel
import com.bl.todo.data.models.FirebaseNewNote
import com.bl.todo.data.models.FirebaseUser
import com.bl.todo.data.room.DateTypeConverters
import com.bl.todo.ui.wrapper.LabelDetails
import com.bl.todo.common.Utilities
import com.bl.todo.ui.wrapper.NoteInfo
import com.bl.todo.ui.wrapper.UserDetails
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.suspendCoroutine

object FirebaseDatabaseService {
    private var db = Firebase.firestore

    suspend fun addUserInfoDatabase(user: UserDetails): Boolean {
        var userId = FirebaseAuthentication.getCurrentUser()?.uid.toString()
        return suspendCoroutine { callback ->
            var dbUser = FirebaseUser(user.userName, user.email, user.phone)
            db.collection("users").document(userId).set(dbUser).addOnCompleteListener {
                if (it.isSuccessful) {
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
        var databaseNewNote = FirebaseNewNote(noteInfo.title, noteInfo.content, dateTime, noteInfo.archived)
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
                                var archived = noteMap["archived"] as Boolean
                                var reminderInString = noteMap["reminder"].toString()
                                var reminder = Utilities.stringToDate(reminderInString as String?)
                                var dateTime = DateTypeConverters().toOffsetDateTime(dateModified)
                                var key = item.id
                                var note =
                                    NoteInfo(title, content, fnid = key, dateModified = dateTime,
                                        archived = archived, reminder = reminder)
                                noteList.add(note)
                            }
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
            "dateModified" to dateTime,
            "archived" to noteInfo.archived,
            "reminder" to Utilities.dateToString(noteInfo.reminder)
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
                .collection("notes").document(noteInfo.fnid).delete()
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

    suspend fun addNewLabel(label : LabelDetails, user: UserDetails) : LabelDetails {
        val userId = user.fUid.toString()
        val dateTime = Utilities.dateToString(label.dateModified)
        return suspendCoroutine { callback ->
            val firebaseLabel = FirebaseLabel(label.labelName, dateTime)
            val autoId = db.collection("users")
                .document(userId).collection("labels").document().id
            db.collection("users").document(userId).collection("labels")
                .document(autoId).set(firebaseLabel).addOnCompleteListener {
                if(it.isSuccessful) {
                    label.labelFid = autoId
                    callback.resumeWith(Result.success(label))
                } else {
                    callback.resumeWith(Result.failure(it.exception!!))
                }
            }
        }
    }

    suspend fun getLabels(user: UserDetails): ArrayList<LabelDetails> {
        val userID = user.fUid.toString()
        return suspendCoroutine { callback ->
            var labelList = ArrayList<LabelDetails>()
            db.collection("users").document(userID)
                .collection("labels").get().addOnCompleteListener {
                if(it.isSuccessful) {
                    val dataSnapshot = it.result
                    if(dataSnapshot != null) {
                        for( item in dataSnapshot.documents) {
                            val labelMap = item.data as HashMap<*, *>
                            val labelName = labelMap["labelName"].toString()
                            val labelFid = item.id
                            val dateModified = labelMap["labelModified"].toString()
                            val dateTime = DateTypeConverters().toOffsetDateTime(dateModified)
                            val labelItem = LabelDetails(labelName = labelName,
                                labelFid = labelFid,dateModified = dateTime)
                            labelList.add(labelItem)
                        }
                        callback.resumeWith(Result.success(labelList))
                    }
                } else {
                    callback.resumeWith(Result.failure(it.exception!!))
                }
            }
        }

    }

    suspend fun deleteLabel(label: LabelDetails, userDetails: UserDetails): LabelDetails {
        val userId = userDetails.fUid.toString()
        Log.e("Delete","$userId")
        return suspendCoroutine { callback ->
            db.collection("users").document(userId)
                .collection("labels").document(label.labelFid).delete()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback.resumeWith(Result.success(label))
                    } else {
                        Log.e("Database", "Delete note failed")
                        Log.e("Database", it.exception.toString())
                        callback.resumeWith(Result.failure(it.exception!!))
                    }
                }
        }
    }

    suspend fun updateLabel(label: LabelDetails, user: UserDetails) : LabelDetails {
        val userId = user.fUid.toString()
        var dateTime = DateTypeConverters().fromOffsetDateTime(label.dateModified)
        val labelMap = mapOf(
            "labelName" to label.labelName,
            "labelModified" to dateTime
        )
        return suspendCoroutine { callback ->
            db.collection("users").document(user.fUid.toString())
                .collection("labels").document(label.labelFid)
                .update(labelMap)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback.resumeWith(Result.success(label))
                    } else {
                        Log.e("Database", "Update label failed")
                        callback.resumeWith(Result.failure(it.exception!!))
                    }
                }
        }
    }

    suspend fun linkLabelAndNote(labelFid : String, noteFid : String, user: UserDetails) : Boolean {
        val userId = user.fUid.toString()
        val labelMap = mapOf(
            "labelId" to labelFid,
            "noteId" to noteFid
        )
        return suspendCoroutine { callback ->
//            val autoId = db.collection("users").document(userId)
//                .collection("noteLabels").document().id
            db.collection("users").document(userId)
                .collection("labelNote").
                document("${noteFid}_${labelFid}").set(labelMap)
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        callback.resumeWith(Result.success(true))
                    } else {
                        callback.resumeWith(Result.failure(it.exception!!))
                    }
                }
        }
    }

    suspend fun getLabelsForNote(noteInfo: NoteInfo, user: UserDetails) : ArrayList<LabelDetails> {
        val userID = user.fUid.toString()
        return suspendCoroutine {
            val labelList = ArrayList<LabelDetails>()
            db.collection("users").document(userID)
                .collection("labelNote").whereEqualTo("noteId",noteInfo.fnid).get().addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        val dataSnapshot = task.result
                        if(dataSnapshot != null) {
                            CoroutineScope(Dispatchers.Default).launch {
                                for( item in dataSnapshot.documents) {
                                    val labelMap = item.data as HashMap<*, *>
                                    val labelFid = labelMap["labelId"].toString()
                                    val noteFid = labelMap["noteId"].toString()
                                    val labelResult = withContext(Dispatchers.IO) {
                                        kotlin.runCatching {
                                            getLabelFromId(labelFid, user)
                                        }
                                    }
                                    labelResult.getOrNull()?.let {
                                        Log.i("FirebaseDatabaseService","label : $it")
                                        labelList.add(it)
                                    }
                                }
                                Log.i("FirebaseDatabaseService","listLabel : $labelList")
                                it.resumeWith(Result.success(labelList))
                            }

                        }
                    } else {
                        it.resumeWith(Result.failure(task.exception ?: Exception("Something went wrong")))
                    }
                }
        }
    }

    private suspend fun getLabelFromId(labelFid: String, user: UserDetails) = suspendCoroutine<LabelDetails> {
        db.collection("users").document(user.fUid.toString()).collection("labels").document(labelFid).get().addOnCompleteListener { task ->
            if(task.isSuccessful) {
                val document = task.result
                if(document != null) {
                    var labelMap = document.data as HashMap<*, * >
                    var labelName = labelMap["labelName"].toString()
                    val labelModified = labelMap["labelModified"].toString()
                    val labelModifiedDate = Utilities.stringToDate(labelModified)
                    val label = LabelDetails(labelName, labelFid, labelModifiedDate)
                    it.resumeWith(Result.success(label))
                } else {
                    it.resumeWith(Result.failure(Exception("No document associated with given ID")))
                }
            } else {
                it.resumeWith(Result.failure(task.exception ?: Exception("Something went wrong")))
            }
        }
    }

    suspend fun removeLabelAndNoteLink(linkId : String, user: UserDetails) : Boolean {
        val userId = user.fUid.toString()
        return suspendCoroutine { callback ->
            db.collection("users").document(userId)
                .collection("labelNote").document(linkId).delete()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback.resumeWith(Result.success(true))
                    } else {
                        Log.e("FirebaseDatabase", "Delete labelLink failed")
                        Log.e("FirebaseDatabase", it.exception.toString())
                        callback.resumeWith(Result.failure(it.exception!!))
                    }
                }
        }
    }
}