package com.bl.todo.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bl.todo.R
import com.bl.todo.databinding.ResetpasswordFragmentBinding
import com.bl.todo.databinding.SplashScreenBinding
import com.bl.todo.services.Authentication
import com.bl.todo.viewmodels.SharedViewModel
import com.bl.todo.viewmodels.SharedViewModelFactory

class ResetPassword : Fragment(R.layout.resetpassword_fragment){
    private lateinit var binding: ResetpasswordFragmentBinding
    private lateinit var sharedViewModel : SharedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ResetpasswordFragmentBinding.bind(view)
        sharedViewModel = ViewModelProvider(requireActivity(),SharedViewModelFactory())[SharedViewModel::class.java]
        binding.resetSendButton.setOnClickListener {
            var email = binding.resetPasswordEditText.text.toString()
            Authentication.resetPassword(email){ status,message ->
                if(status){
                    Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }

            }
        }

        binding.resetBackButton.setOnClickListener {
            sharedViewModel.setLoginPageStatus(true)
        }
    }
}