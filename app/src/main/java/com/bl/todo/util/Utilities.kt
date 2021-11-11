package com.bl.todo.util

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bl.todo.R
import com.bl.todo.data.models.DatabaseUser
import com.bl.todo.data.wrapper.UserDetails
import com.bl.todo.data.wrapper.NoteInfo
import com.google.android.material.textfield.TextInputEditText
import java.util.HashMap

object Utilities {
    fun replaceFragment(fragmentManager: FragmentManager,fromFragment : Int,toFragment: Fragment) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(fromFragment, toFragment)
        fragmentTransaction.commit()
    }

    fun signUpCredentialsValidator(
       userName : TextInputEditText,
       email : TextInputEditText,
       phone : TextInputEditText,
       password : TextInputEditText,
       confirmPassword : TextInputEditText,
       context: Context
    ) : Boolean{
        var status : Boolean = true
        if(userName.text.toString().isEmpty()){
            userName.error = context.getString(R.string.invalid_userName_message)
            status = false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
            email.error = context.getString(R.string.error_invalidEmail)
            status = false
        }
        if(phone.text.toString().length<10){
            phone.error = context.getString(R.string.error_invalidMobile)
            status = false
        }
        if(password.text.toString() != confirmPassword.text.toString()){
            confirmPassword.error = context.getString(R.string.error_differentPassword)
            status = false
        }
        if(password.text.toString().length < 6){
            password.error = context.getString(R.string.error_password_minimum_6_characters)
            status = false
        }
        return status
    }

    fun loginCredentialsValidator(email: TextInputEditText, password: TextInputEditText,context: Context): Boolean {
        var state = true
        if(!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
            email.error = context.getString(R.string.error_invalidEmail)
            state = false
        }
        if(password.text.toString().length < 6){
            password.error = context.getString(R.string.error_password_minimum_6_characters)
            state = false
        }
        return state
    }

    fun addInfoToBundle(newUser: UserDetails): Bundle {
        var bundle = Bundle()
        bundle.putString("name",newUser.userName)
        bundle.putString("email",newUser.email)
        bundle.putString("phone",newUser.phone)
        return bundle
    }

    fun createUserFromHashMap(userMap:HashMap<*,*>):DatabaseUser {
        return DatabaseUser(userMap["userName"].toString(),userMap["email"].toString(),userMap["phone"].toString())
    }

    fun addUserInfoToSharedPref(user : DatabaseUser){
        SharedPref.addKeyValue("userName",user.userName)
        SharedPref.addKeyValue("email",user.email)
        SharedPref.addKeyValue("Phone",user.phone)
    }

    fun addNoteInfoToBundle(noteInfo: NoteInfo) : Bundle{
        var bundle = Bundle()
        bundle.putString("title",noteInfo.title)
        bundle.putString("content",noteInfo.content)
        bundle.putString("noteKey",noteInfo.noteKey)
        return bundle
    }

}