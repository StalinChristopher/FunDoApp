package com.bl.todo.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bl.todo.R
import com.bl.todo.databinding.LoginFragmentBinding
import com.bl.todo.models.UserDetails
import com.bl.todo.services.Authentication
import com.bl.todo.services.Database
import com.bl.todo.util.Utilities
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult

class LoginFragment : Fragment(R.layout.login_fragment){
    private lateinit var binding: LoginFragmentBinding
    private lateinit var dialog  : Dialog
    private lateinit var callbackManager: CallbackManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding = LoginFragmentBinding.bind(view)
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_loading)
//        checkUser()
        callbackManager = CallbackManager.Factory.create()
        binding.loginRegister.setOnClickListener {
            Toast.makeText(requireContext(),"button pressed",Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerId , SignUpFragment()).commit()
        }
        binding.loginSubmit.setOnClickListener {
            dialog.show()
            login()
        }

        binding.loginFacebookButton.setOnClickListener {
            facebookLogin()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode,resultCode,data)
    }

    private fun checkUser() {
        var user = Authentication.getCurrentUser()
        if( user != null){
            Database.getUserData(user.uid){ status, bundle->
                var profileObj = ProfileFragment()
                profileObj.arguments = bundle
                requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerId,profileObj).commit()
            }
        }
    }

    private fun login() {
        var email = binding.loginEmail
        var password = binding.loginPassword
        if(Utilities.loginCredentialsValidator(email,password)){
            Authentication.loginWithEmailAndPassword(email.text.toString(),password.text.toString()){ status, user ->
                if(!status){
                    dialog.dismiss()
                    Toast.makeText(requireContext(),"Incorrect Email or Password",Toast.LENGTH_SHORT).show()
                }else{
                    Database.getUserData(user!!.uid){ status, bundle ->
                        if(!status){
                            Log.e("DatabaseError","read failed")
                            dialog.dismiss()
                        }else{
                            var profileObj = ProfileFragment()
                            profileObj.arguments = bundle
                            dialog.dismiss()
                            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerId,profileObj).commit()
                        }
                    }
                }
            }
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
                Authentication.handleFacebookLogin(result.accessToken) { status,firebaseUser ->
                    if(!status){
                        Toast.makeText(requireContext(),"Authentication failed",Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }else{
                        var username = Authentication.getCurrentUser()?.displayName
                        var email = Authentication.getCurrentUser()?.email
                        var phone = Authentication.getCurrentUser()?.phoneNumber
                        var user = UserDetails(username, email,
                            phone)
                        Database.addUserInfoDatabase(user){
                            if(it){
                                Log.i("DB","info added to database")
                            }else{
                                Log.i("DB","info not added to database")
                            }
                        }
                        Database.getUserData(firebaseUser!!.uid){ status, bundle ->
                            if(!status){
                                Log.e("DatabaseError","read failed")
                                dialog.dismiss()
                            }else{
                                var profileObj = ProfileFragment()
                                profileObj.arguments = bundle
                                Log.i("bundle","${bundle?.get("email")}")
                                dialog.dismiss()
                                requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerId,profileObj).commit()
                            }
                        }

                    }
                }
            }
        })
    }
}