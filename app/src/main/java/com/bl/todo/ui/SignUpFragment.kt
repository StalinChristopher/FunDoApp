package com.bl.todo.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bl.todo.R
import com.bl.todo.databinding.SignupFragmentBinding
import com.bl.todo.models.UserDetails
import com.bl.todo.services.FirebaseAuthentication
import com.bl.todo.services.FirebaseDatabase
import com.bl.todo.util.Utilities
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUpFragment : Fragment(R.layout.signup_fragment) {
    private lateinit var binding : SignupFragmentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SignupFragmentBinding.bind(view)

        binding.signupLogin.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerId,LoginFragment()).commit()
        }

        binding.signupSubmit.setOnClickListener {
            signup()
        }
    }

    private fun signup() {
        var userName :TextInputEditText = binding.signupUsername
        var email : TextInputEditText = binding.signupEmail
        var phone : TextInputEditText = binding.signupMobile
        var password : TextInputEditText = binding.signupPassword
        var confirmPassword : TextInputEditText = binding.signupConfirmPassword
        var bundle : Bundle
        if(Utilities.signUpCredentialsValidator(userName, email, phone, password, confirmPassword)){
            val newUser : UserDetails = UserDetails(userName.text.toString() , email.text.toString(), phone.text.toString())
            FirebaseAuthentication.signUpWithEmailAndPassword(email.text.toString(),password.text.toString()){ status,user->
                if(status){
                    FirebaseDatabase.addUserInfoDatabase(newUser){
                        if(it){
                            Toast.makeText(requireContext(),"Account created successfully",Toast.LENGTH_SHORT).show()
                            var bundle = Bundle()
                            bundle.putString("email",user?.email)
                            var profileObj = ProfileFragment()
                            profileObj.arguments = bundle
                            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerId,profileObj).commit()
                        }
                        else{
                            Log.e("DatabaseError","write failed")
                        }
                    }
                }else{
                    Toast.makeText(requireContext(),"Account has not been created",Toast.LENGTH_SHORT).show()
                }

            }
        }

    }

}