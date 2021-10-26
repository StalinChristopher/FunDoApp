package com.bl.todo.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bl.todo.R
import com.bl.todo.databinding.LoginFragmentBinding
import com.bl.todo.services.Database
import com.bl.todo.util.Utilities
import com.bl.todo.viewmodels.SharedViewModel
import com.bl.todo.viewmodels.SharedViewModelFactory
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult

class LoginFragment : Fragment(R.layout.login_fragment){
    private lateinit var binding: LoginFragmentBinding
    private lateinit var callbackManager: CallbackManager
    private lateinit var sharedViewModel: SharedViewModel

    companion object{
       lateinit var dialog  : Dialog
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding = LoginFragmentBinding.bind(view)
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_loading)
        sharedViewModel = ViewModelProvider(requireActivity(), SharedViewModelFactory())[SharedViewModel::class.java]
        callbackManager = CallbackManager.Factory.create()
        binding.loginRegister.setOnClickListener {
            Toast.makeText(requireContext(),"button pressed",Toast.LENGTH_SHORT).show()
            sharedViewModel.setSignupPageStatus(true)
        }
        binding.loginSubmit.setOnClickListener {
            dialog.show()
            login()
        }

        binding.loginFacebookButton.setOnClickListener {
            facebookLogin()
        }

        binding.forgotPasswordClick.setOnClickListener {
            sharedViewModel.setForgotPasswordPageStatus(true)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode,resultCode,data)
    }

    private fun login() {
        var email = binding.loginEmail
        var password = binding.loginPassword
        if(Utilities.loginCredentialsValidator(email,password)) {
            sharedViewModel.loginWithEmailAndPassword(email.text.toString(), password.text.toString())
        }
    }

    private fun facebookLogin() {
        dialog.show()
        var facebookLoginButton = binding.loginFacebookButton
        facebookLoginButton.setReadPermissions("email","public_profile")
        facebookLoginButton.registerCallback(callbackManager,object : FacebookCallback<LoginResult> {
            override fun onCancel() {
                Log.d("Facebook-OAuth", "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d("Facebook-OAuth", "facebook:onError", error)
            }

            override fun onSuccess(result: LoginResult) {
                Log.d("Facebook-OAuth", "facebook:onSuccess:$result")
                sharedViewModel.loginWithFacebook(result.accessToken)
            }
        })
    }
}