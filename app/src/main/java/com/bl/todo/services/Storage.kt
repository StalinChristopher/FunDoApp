package com.bl.todo.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import kotlin.coroutines.suspendCoroutine

object Storage {
    private var storageRef = Firebase.storage.reference

    suspend fun addProfileImage(bitmap: Bitmap) : Boolean{
        var userId = Authentication.getCurrentUser()?.uid.toString()
        return suspendCoroutine { callback ->
            var profileImageRef = storageRef.child("images").child("users")
                .child(userId)
                .child("profile.webp")
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            profileImageRef.putBytes(data).addOnCompleteListener{
                if(it.isSuccessful){
                    Log.i("Storage","Upload successful")
//                    listener(true)
                    callback.resumeWith(Result.success(true))
                }else{
                    Log.i("Storage","Upload failed")
//                    listener(true)
                    callback.resumeWith(Result.failure(it.exception!!))
                }
            }
        }
    }

    suspend fun getProfileImage() : Bitmap{
        val oneMegabyte: Long = 1024 * 1024
        var userId = Authentication.getCurrentUser()?.uid.toString()
        return suspendCoroutine { callback ->
            var profileImageRef = storageRef.child("images").child("users")
                .child(userId).child("profile.webp")
            profileImageRef.getBytes(oneMegabyte).addOnSuccessListener {
                Log.i("Storage","Image download successful")
                val bitmap = BitmapFactory.decodeByteArray(it,0,it.size)
//                listener(bitmap)
                callback.resumeWith(Result.success(bitmap))
            }.addOnFailureListener{
                Log.i("Storage","Image download failed")
                callback.resumeWith(Result.failure(it))
            }
        }
    }
}