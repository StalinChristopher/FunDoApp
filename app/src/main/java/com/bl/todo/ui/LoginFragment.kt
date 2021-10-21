package com.bl.todo.ui

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bl.todo.R
import com.bl.todo.databinding.LoginFragmentBinding

class LoginFragment : Fragment(R.layout.login_fragment){
    private lateinit var binding: LoginFragmentBinding
    private lateinit var loginEmail: String
    private lateinit var loginPassword : String
    private lateinit var progressBar : ProgressBar
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LoginFragmentBinding.bind(view)
        binding.loginRegister.setOnClickListener {
            Toast.makeText(requireContext(),"button pressed",Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerId , SignUpFragment()).commit()
        }
        loginEmail = binding.loginEmail.text.toString()
        loginPassword = binding.loginPassword.text.toString()
        binding.loginSubmit.setOnClickListener {
//            Utilities.validateEmailPassword(loginEmail,loginPassword, view)
        }



    }
}