package com.bl.todo.services

import android.util.Log
import android.widget.Toast
import com.bl.todo.models.DatabaseNewNote
import com.bl.todo.models.DatabaseUser
import com.bl.todo.models.NewNote
import com.bl.todo.util.Utilities
import com.bl.todo.wrapper.NoteInfo
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object Database {
   private var db : DatabaseReference = Firebase.database.reference

   fun addUserInfoDatabase(user : DatabaseUser, listener : (Boolean)-> Unit){
      var userId =Authentication.getCurrentUser()?.uid.toString()
      db.child("users").child(userId).setValue(user).addOnCompleteListener {
         if(it.isSuccessful){
            Utilities.addUserInfoToSharedPref(user)
            listener(true)
         }else{
            listener(false)
         }
      }
   }

    fun getUserData(listener: (Boolean) -> Unit) {
       db.child("users").child(Authentication.getCurrentUser()?.uid.toString()).get()
          .addOnCompleteListener { status->
             if(status.isSuccessful) {
                status.result.also {
                   Log.i("DB","User : $it")
                   var userDb : DatabaseUser = Utilities.createUserFromHashMap(it?.value as HashMap<*, *>)
                   Utilities.addUserInfoToSharedPref(userDb)
                   listener(true)
                   }
             }
             else{
                Log.e("DB","Read Failed")
                Log.e("DB",status.exception.toString())
                listener(false)
             }
          }
    }

   fun addNewNote(newNote : NewNote,dateTime : String, listener: (Boolean) -> Unit){
      var userId =Authentication.getCurrentUser()?.uid.toString()
      var databaseNewNote = DatabaseNewNote(newNote.title,newNote.content,dateTime,userId)
      var ref = db.child("Notes").child(userId).push()
      ref.setValue(databaseNewNote).addOnCompleteListener {
         if(it.isSuccessful){
            Log.i("Database","Note added successfully")
            listener(true)
         }else{
            Log.e("Database error", "Adding new note failed")
            listener(false)
         }
      }
      var keygen = ref.key
      Log.i("key","$keygen")
   }

   fun getUserNotes(listener: (ArrayList<NoteInfo>?) -> Unit){
      var userId = Authentication.getCurrentUser()?.uid.toString()
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
               listener(noteList)
            }else{
               listener(null)
            }
         }else{
            Log.i("Database error","Notes could not be fetched")
            listener(null)
         }
      }
   }

   fun updateUserNotes(noteInfo: NoteInfo, dateTime : String, listener: (Boolean) -> Unit){
      var userId = Authentication.getCurrentUser()?.uid.toString()
      val noteMap = mapOf(
         "title" to noteInfo.title,
         "content" to noteInfo.content,
         "dateCreated" to dateTime
      )
      db.child("Notes").child(userId).child(noteInfo.noteKey).updateChildren(noteMap).addOnCompleteListener {
         if(it.isSuccessful){
            listener(true)
         }else{
            Log.e("Database","Update note failed")
            Log.e("Database",it.exception.toString())
            listener(false)
         }
      }
   }


}