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
import com.google.android.material.textfield.TextInputEditText
import org.w3c.dom.Text


class SignUpFragment : Fragment(R.layout.signup_fragment) {
    private lateinit var sharedViewModel: SharedViewModel

    companion object{
        lateinit var dialog  : Dialog
        lateinit var userName : TextInputEditText
        lateinit var email : TextInputEditText
        lateinit var phone : TextInputEditText
        lateinit var binding : SignupFragmentBinding
    }
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
            sharedViewModel.signUpWithEmailAndPassword(email.text.toString(),password.text.toString())
        }else{
            dialog.dismiss()
        }
    }


}