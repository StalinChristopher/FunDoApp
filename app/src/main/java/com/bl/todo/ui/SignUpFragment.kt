package com.bl.todo.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bl.todo.R
import com.bl.todo.databinding.SignupFragmentBinding
import com.bl.todo.models.UserDetails
import com.bl.todo.services.FirebaseAuthentication
import com.bl.todo.services.FirebaseDatabase
import com.bl.todo.util.Utilities
import com.google.android.material.textfield.TextInputEditText


class SignUpFragment : Fragment(R.layout.signup_fragment) {
    private lateinit var binding : SignupFragmentBinding
    private lateinit var dialog  : Dialog
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SignupFragmentBinding.bind(view)
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_loading)

        binding.signupLogin.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerId,LoginFragment()).commit()
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
            val newUser : UserDetails = UserDetails(userName.text.toString() , email.text.toString(), phone.text.toString())
            FirebaseAuthentication.signUpWithEmailAndPassword(email.text.toString(),password.text.toString()){ status,user->
                if(status){
                    FirebaseDatabase.addUserInfoDatabase(newUser){
                        if(it){
                            Toast.makeText(requireContext(),"Account created successfully",Toast.LENGTH_SHORT).show()
                            bundle = Utilities.addInfoToBundle(newUser)
                            var profileObj = ProfileFragment()
                            profileObj.arguments = bundle
                            dialog.dismiss()
                            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerId,profileObj).commit()
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