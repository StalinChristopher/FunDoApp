package com.bl.todo.data.services

import android.util.Log
import com.bl.todo.authService.Authentication
import com.bl.todo.data.models.DatabaseNewNote
import com.bl.todo.data.models.DatabaseUser
import com.bl.todo.data.room.DateTypeConverters
import com.bl.todo.data.wrapper.NewNote
import com.bl.todo.util.Utilities
import com.bl.todo.data.wrapper.NoteInfo
import com.bl.todo.data.wrapper.UserDetails
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.suspendCoroutine

object FirebaseDatabaseService {
   private var db : DatabaseReference = Firebase.database.reference

   suspend fun addUserInfoDatabase(user : UserDetails): Boolean{
      var userId = Authentication.getCurrentUser()?.uid.toString()
      return suspendCoroutine{ callback ->
         var dbUser = DatabaseUser(user.userName,user.email,user.phone)
         db.child("users").child(userId).setValue(dbUser).addOnCompleteListener {
            if(it.isSuccessful){
               Utilities.addUserInfoToSharedPref(dbUser)
               callback.resumeWith(Result.success(true))
            }else{
               callback.resumeWith(Result.failure(it.exception!!))
               }
            }
         }
   }

    suspend fun getUserData(fUid : String) : UserDetails {
       Log.e("UserId","$fUid")
       return suspendCoroutine { callback ->
          db.child("users").child(fUid).get().addOnCompleteListener { status->
                if(status.isSuccessful) {
                   status.result.also {
                      Log.i("DB","User : $it")
                      var userDb : DatabaseUser = Utilities.createUserFromHashMap(
                         it?.value as HashMap<*, *>)
                      var user = UserDetails(userDb.userName,userDb.email,userDb.phone,fUid = fUid)
                      callback.resumeWith(Result.success(user))
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

   suspend fun addNewNote(noteInfo: NoteInfo, userDetails: UserDetails) : NoteInfo{
      var dateTime = DateTypeConverters().fromOffsetDateTime(noteInfo.dateModified)
      Log.e("TimeDate","$dateTime")
      var databaseNewNote = DatabaseNewNote(noteInfo.title,noteInfo.content, dateTime)
      return suspendCoroutine { callback ->
         var ref = db.child("Notes").child(userDetails.fUid.toString()).push()
         Log.i("refKey","${ref.key}")
         ref.setValue(databaseNewNote).addOnCompleteListener {
            if(it.isSuccessful){
               Log.i("Database","Note added successfully")
               noteInfo.fnid = ref.key.toString()
               callback.resumeWith(Result.success(noteInfo))
            }else{
               Log.e("Database error", "Adding new note failed")
               callback.resumeWith(Result.failure(it.exception!!))
            }
         }
      }
   }

   suspend fun getUserNotes(user: UserDetails) : ArrayList<NoteInfo>?{
      return suspendCoroutine { callback ->
         db.child("Notes").child(user.fUid.toString()).get().addOnCompleteListener {
            if(it.isSuccessful){
               var noteList = ArrayList<NoteInfo>()
               var dataSnapshot = it.result
               if (dataSnapshot != null) {
                  for(item in dataSnapshot.children){
                     var title = item.child("title").value.toString()
                     var content = item.child("content").value.toString()
                     var dateModified = item.child("dateModified").value.toString()
                     Log.e("DateFb","$dateModified")
                     var dateTime = DateTypeConverters().toOffsetDateTime(dateModified)
                     var key = item.key.toString()
                     var note = NoteInfo(title,content,key,dateModified = dateTime)
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

   suspend fun updateUserNotes(noteInfo: NoteInfo, user: UserDetails) : Boolean{
//      var userId = Authentication.getCurrentUser()?.uid.toString()
      var dateTime = DateTypeConverters().fromOffsetDateTime(noteInfo.dateModified)
      val noteMap = mapOf(
         "title" to noteInfo.title,
         "content" to noteInfo.content,
         "dateModified" to dateTime
      )
      return suspendCoroutine { callback ->
         db.child("Notes").child(user.fUid.toString()).child(noteInfo.fnid).updateChildren(noteMap)
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
         db.child("Notes").child(userId).child(noteInfo.fnid.toString()).removeValue()
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