package com.bl.todo.util

import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.bl.todo.R
import com.bl.todo.databinding.LoginFragmentBinding
import com.google.android.material.textfield.TextInputEditText
import java.util.regex.Pattern

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
       confirmPassword : TextInputEditText
    ) : Boolean{
        var status : Boolean = true
        if(userName.text.toString().isEmpty()){
            userName.error = "Username cannot be empty"
            status = false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
            email.error = "Invalid email format"
            status = false
        }
        if(phone.text.toString().length<10){
            phone.error = "Invalid mobile number"
            status = false
        }
        if(password.text.toString() != confirmPassword.text.toString()){
            confirmPassword.error = "Passwords doesn't match"
            status = false
        }
        if(password.text.toString().length < 6){
            password.error = "Password length cannot be less than 6 characters"
            status = false
        }
        return status
    }

}