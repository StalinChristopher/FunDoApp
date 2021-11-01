package com.bl.todo.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

object Storage {
    private var storageRef = Firebase.storage.reference

    fun addProfileImage(bitmap: Bitmap, listener :(Boolean) -> Unit){
        var profileImageRef = storageRef.child("images").child("users").child(Authentication.getCurrentUser()?.uid.toString())
            .child("profile.webp")
        val baos = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        profileImageRef.putBytes(data).addOnCompleteListener{
            if(it.isSuccessful){
                Log.i("Storage","Upload successful")
                listener(true)
            }else{
                Log.i("Storage","Upload failed")
                listener(true)
            }
        }
    }

    fun getProfileImage(listener: (Bitmap?) -> Unit){

        var profileImageRef = storageRef.child("images").child("users").child(Authentication.getCurrentUser()?.uid.toString())
            .child("profile.webp")
        val oneMegabyte: Long = 1024 * 1024

        profileImageRef.getBytes(oneMegabyte).addOnSuccessListener {
            Log.i("Storage","Image download successful")
            val bitmap = BitmapFactory.decodeByteArray(it,0,it.size)
            listener(bitmap)
        }.addOnFailureListener{
            Log.i("Storage","Image download failed")
        }
    }
}