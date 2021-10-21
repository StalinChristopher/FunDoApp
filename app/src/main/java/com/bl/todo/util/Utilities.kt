package com.bl.todo.util

import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.bl.todo.R
import com.bl.todo.databinding.LoginFragmentBinding
import java.util.regex.Pattern

object Utilities {
    fun replaceFragment(fragmentManager: FragmentManager,fromFragment : Int,toFragment: Fragment) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(fromFragment, toFragment)
        fragmentTransaction.commit()
    }

    fun validateEmailPassword(email: String, password: String, view: View){
        var binding: LoginFragmentBinding = LoginFragmentBinding.bind(view)
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.loginEmail.error = "Invalid email format"
        }else if( TextUtils.isEmpty(password)){
            binding.loginPassword.error = "Invalid password format"
        }
    }
}