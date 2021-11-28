package com.bl.todo.ui.signup

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bl.todo.R
import com.bl.todo.databinding.SignupFragmentBinding
import com.bl.todo.ui.wrapper.UserDetails
import com.bl.todo.common.Utilities
import com.bl.todo.ui.SharedViewModel


class SignUpFragment : Fragment(R.layout.signup_fragment) {
    private lateinit var dialog: Dialog
    private lateinit var binding: SignupFragmentBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var signUpViewModel: SignUpViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        binding = SignupFragmentBinding.bind(view)
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_loading)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        signUpViewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
        binding.signupLogin.setOnClickListener {
//            sharedViewModel.setLoginPageStatus(true)
            activity?.supportFragmentManager?.popBackStack()
        }

        binding.signupSubmit.setOnClickListener {
            dialog.show()
            signup()
        }
        signUpObservers()
    }

    private fun signup() {
        val userName = binding.signupUsername
        val email = binding.signupEmail
        val phone = binding.signupMobile
        val password = binding.signupPassword
        val confirmPassword = binding.signupConfirmPassword
        val user = UserDetails(
            userName = userName.text.toString(), email = email.text.toString(),
            phone = phone.text.toString(), fUid = null
        )
        if (Utilities.signUpCredentialsValidator(
                userName,
                email,
                phone,
                password,
                confirmPassword,
                requireContext()
            )
        ) {
            signUpViewModel.signUpWithEmailAndPassword(
                requireContext(),
                user,
                password.text.toString()
            )
        } else {
            dialog.dismiss()
        }
    }

    private fun signUpObservers() {
        signUpViewModel.signUpStatus.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.toast_userSignedUp),
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
                sharedViewModel.setGotoHomePageStatus(true)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.toastError_account_not_created),
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
            }
        }
    }
}