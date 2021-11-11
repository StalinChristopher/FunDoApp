package com.bl.todo.ui.signUpPage

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bl.todo.R
import com.bl.todo.databinding.SignupFragmentBinding
import com.bl.todo.data.wrapper.UserDetails
import com.bl.todo.util.Utilities
import com.bl.todo.ui.mainActivity.SharedViewModel
import com.google.android.material.textfield.TextInputEditText


class SignUpFragment : Fragment(R.layout.signup_fragment) {
    private lateinit var dialog  : Dialog
    private lateinit var userName : TextInputEditText
    private lateinit var email : TextInputEditText
    private lateinit var phone : TextInputEditText
    private lateinit var binding : SignupFragmentBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var signUpViewModel: SignUpViewModel

    companion object{

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        binding = SignupFragmentBinding.bind(view)
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_loading)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        signUpViewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
        binding.signupLogin.setOnClickListener {
            sharedViewModel.setLoginPageStatus(true)
        }

        binding.signupSubmit.setOnClickListener {
            dialog.show()
            signup()
        }

        signUpObservers()
    }

    private fun signup() {
        var userName = binding.signupUsername
        var email = binding.signupEmail
        var phone  = binding.signupMobile
        var password = binding.signupPassword
        var confirmPassword = binding.signupConfirmPassword
        var user = UserDetails(userName.text.toString(),email.text.toString(),phone.text.toString())
        if(Utilities.signUpCredentialsValidator(userName, email, phone, password, confirmPassword,requireContext())){
            signUpViewModel.signUpWithEmailAndPassword(user,password.text.toString(),phone.text.toString())
        }else{
            dialog.dismiss()
        }
    }

    private fun signUpObservers() {
        signUpViewModel.signUpStatus.observe(viewLifecycleOwner){
            if(it){
                Toast.makeText(requireContext(),getString(R.string.toast_userSignedUp),Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                sharedViewModel.setGotoHomePageStatus(true)
            }
            else{
                Toast.makeText(requireContext(),getString(R.string.toastError_account_not_created),Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
    }


}