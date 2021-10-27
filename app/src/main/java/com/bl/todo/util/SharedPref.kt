package com.bl.todo.util

import android.content.Context
import android.content.SharedPreferences

object SharedPref {

    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var sharedPrefEdit : SharedPreferences.Editor

    fun initializePref(context: Context){
        sharedPreferences = context.getSharedPreferences("sharedPrefFile",Context.MODE_PRIVATE)
        sharedPrefEdit = sharedPreferences.edit()
    }

    fun addKeyValue(key : String,value : String){
        sharedPrefEdit.putString(key,value)
        sharedPrefEdit.apply()
    }

    fun getValue(key : String) : String? {
        return sharedPreferences.getString(key, key)
    }

    fun removeKey(key: String){
        sharedPrefEdit.remove(key)
        sharedPrefEdit.apply()
    }

    fun clearAll(){
        sharedPrefEdit.clear()
        sharedPrefEdit.apply()
    }
}