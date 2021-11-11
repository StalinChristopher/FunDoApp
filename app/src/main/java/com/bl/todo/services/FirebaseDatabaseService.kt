package com.bl.todo.services

import android.util.Log
import android.widget.Toast
import com.bl.todo.models.DatabaseNewNote
import com.bl.todo.models.DatabaseUser
import com.bl.todo.models.NewNote
import com.bl.todo.util.Utilities
import com.bl.todo.wrapper.NoteInfo
import com.google.firebase.FirebaseException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.suspendCoroutine

object FirebaseDatabaseService {
   private var db : DatabaseReference = Firebase.database.reference

   suspend fun addUserInfoDatabase(user : DatabaseUser): Boolean{
      var userId =Authentication.getCurrentUser()?.uid.toString()
      return suspendCoroutine{ callback ->
         db.child("users").child(userId).setValue(user).addOnCompleteListener {
            if(it.isSuccessful){
               Utilities.addUserInfoToSharedPref(user)
               callback.resumeWith(Result.success(true))
            }else{
               callback.resumeWith(Result.failure(it.exception!!))
               }
            }
         }
   }

    suspend fun getUserData() : Boolean {
       var userId = Authentication.getCurrentUser()?.uid.toString()
       return suspendCoroutine { callback ->
          db.child("users").child(userId).get().addOnCompleteListener { status->
                if(status.isSuccessful) {
                   status.result.also {
                      Log.i("DB","User : $it")
                      var userDb : DatabaseUser = Utilities.createUserFromHashMap(
                         it?.value as HashMap<*, *>)
                      Utilities.addUserInfoToSharedPref(userDb)
                      callback.resumeWith(Result.success(true))
                   }
                }
                else{
                   Log.e("DB","Read Failed")
                   Log.e("DB",status.exception.toString())
                   callback.resumeWith(Result.failure(status.exception!!))
                }
             }
       }
    }

   suspend fun addNewNote(newNote : NewNote,dateTime : String) : NoteInfo{
      var userId =Authentication.getCurrentUser()?.uid.toString()
      var databaseNewNote = DatabaseNewNote(newNote.title,newNote.content,dateTime,userId)
      return suspendCoroutine { callback ->
         var ref = db.child("Notes").child(userId).push()
         ref.setValue(databaseNewNote).addOnCompleteListener {
            if(it.isSuccessful){
               Log.i("Database","Note added successfully")
               var noteInfo = NoteInfo(newNote.title,newNote.content,ref.key.toString())
               callback.resumeWith(Result.success(noteInfo))
            }else{
               Log.e("Database error", "Adding new note failed")
               callback.resumeWith(Result.failure(it.exception!!))
            }
         }
      }
   }

   suspend fun getUserNotes() : ArrayList<NoteInfo>?{
      var userId = Authentication.getCurrentUser()?.uid.toString()
      return suspendCoroutine { callback ->
         db.child("Notes").child(userId).get().addOnCompleteListener {
            if(it.isSuccessful){
               var noteList = ArrayList<NoteInfo>()
               var dataSnapshot = it.result
               if (dataSnapshot != null) {
                  for(item in dataSnapshot.children){
                     var title = item.child("title").value.toString()
                     var content = item.child("content").value.toString()
                     var key = item.key.toString()
                     var note = NoteInfo(title,content,key)
                     noteList.add(note)
                  }
                  callback.resumeWith(Result.success(noteList))
               }else{
                  Log.e("Database","No notes present for the current user")
                  callback.resumeWith((Result.failure(it.exception!!)))
               }
            }else{
               Log.i("Database error","Notes could not be fetched")
               callback.resumeWith(Result.failure(it.exception!!))
            }
         }
      }
   }

   suspend fun updateUserNotes(noteInfo: NoteInfo, dateTime : String) : Boolean{
      var userId = Authentication.getCurrentUser()?.uid.toString()
      val noteMap = mapOf(
         "title" to noteInfo.title,
         "content" to noteInfo.content,
         "dateCreated" to dateTime
      )
      return suspendCoroutine { callback ->
         db.child("Notes").child(userId).child(noteInfo.noteKey).updateChildren(noteMap)
            .addOnCompleteListener {
            if(it.isSuccessful){
               callback.resumeWith(Result.success(true))
            }else{
               Log.e("Database","Update note failed")
               callback.resumeWith(Result.failure(it.exception!!))
            }
         }
      }
   }

   suspend fun deleteUserNotes(noteInfo: NoteInfo) : Boolean{
      var userId = Authentication.getCurrentUser()?.uid.toString()
      return suspendCoroutine { callback ->
         db.child("Notes").child(userId).child(noteInfo.noteKey).removeValue()
            .addOnCompleteListener {
            if(it.isSuccessful){
               callback.resumeWith(Result.success(true))
            }else{
               Log.e("Database","Delete note failed")
               Log.e("Database",it.exception.toString())
               callback.resumeWith(Result.failure(it.exception!!))
            }
         }
      }
   }
}