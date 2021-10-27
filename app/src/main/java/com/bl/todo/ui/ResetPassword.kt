package com.bl.todo.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bl.todo.R
import com.bl.todo.databinding.ResetpasswordFragmentBinding
import com.bl.todo.viewmodels.sharedView.SharedViewModel
import com.bl.todo.viewmodels.sharedView.SharedViewModelFactory
import com.bl.todo.viewmodels.resetPasswordPage.ResetPasswordViewModel
import com.bl.todo.viewmodels.resetPasswordPage.ResetPasswordViewModelFactory

class ResetPassword : Fragment(R.layout.resetpassword_fragment){
    private lateinit var binding: ResetpasswordFragmentBinding
    private lateinit var sharedViewModel : SharedViewModel
    private lateinit var resetPasswordViewModel : ResetPasswordViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        binding = ResetpasswordFragmentBinding.bind(view)
        sharedViewModel = ViewModelProvider(requireActivity(), SharedViewModelFactory())[SharedViewModel::class.java]
        resetPasswordViewModel = ViewModelProvider(this, ResetPasswordViewModelFactory())[ResetPasswordViewModel::class.java]
        binding.resetSendButton.setOnClickListener {
            var email = binding.resetPasswordEditText.text.toString()
            resetPasswordViewModel.resetPasswordWithEmail(email)
        }

        binding.resetBackButton.setOnClickListener {
            sharedViewModel.setLoginPageStatus(true)
        }

        resetPasswordObserver()
    }

    private fun resetPasswordObserver() {
        resetPasswordViewModel.resetPasswordStatus.observe(viewLifecycleOwner){
            if(it){
                Toast.makeText(requireContext(),"Email has been sent to reset the password",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(),"No account is associated with the given email",Toast.LENGTH_SHORT).show()
            }
        }
    }
}