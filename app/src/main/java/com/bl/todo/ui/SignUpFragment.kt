package com.bl.todo.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bl.todo.R
import com.bl.todo.databinding.SignupFragmentBinding
import com.bl.todo.models.UserDetails
import com.bl.todo.services.Authentication
import com.bl.todo.services.Database
import com.bl.todo.util.Utilities
import com.bl.todo.viewmodels.SharedViewModel
import com.bl.todo.viewmodels.SharedViewModelFactory


class SignUpFragment : Fragment(R.layout.signup_fragment) {
    private lateinit var binding : SignupFragmentBinding
    private lateinit var dialog  : Dialog
    private lateinit var sharedViewModel: SharedViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SignupFragmentBinding.bind(view)
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_loading)
        sharedViewModel = ViewModelProvider(requireActivity(), SharedViewModelFactory())[SharedViewModel::class.java]
        binding.signupLogin.setOnClickListener {
            sharedViewModel.setLoginPageStatus(true)
        }

        binding.signupSubmit.setOnClickListener {
            dialog.show()
            signup()
        }
    }

    private fun signup() {
        var userName = binding.signupUsername
        var email = binding.signupEmail
        var phone  = binding.signupMobile
        var password = binding.signupPassword
        var confirmPassword = binding.signupConfirmPassword
        var bundle : Bundle
        if(Utilities.signUpCredentialsValidator(userName, email, phone, password, confirmPassword)){
            Authentication.signUpWithEmailAndPassword(email.text.toString(),password.text.toString()){ status, user->
                if(status){
                    val newUser : UserDetails = UserDetails(userName.text.toString() , email.text.toString(), phone.text.toString(),loginStatus = true)
                    Database.addUserInfoDatabase(newUser){
                        if(it){
                            Toast.makeText(requireContext(),"Account created successfully",Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            sharedViewModel.setGotoHomePageStatus(true)
//                            bundle = Utilities.addInfoToBundle(newUser)
//                            var profileObj = ProfileFragment()
//                            profileObj.arguments = bundle
//                            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerId,profileObj).commit()
                        }
                        else{
                            Log.e("DatabaseError","write failed")
                            dialog.dismiss()
                        }
                    }
                }else{
                    dialog.dismiss()
                    Toast.makeText(requireContext(),"Account has not been created",Toast.LENGTH_SHORT).show()
                }

            }
        }
    }
}