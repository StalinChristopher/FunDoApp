package com.bl.todo.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bl.todo.R
import com.bl.todo.databinding.SignupFragmentBinding

class SignUpFragment : Fragment(R.layout.signup_fragment) {
    private lateinit var binding : SignupFragmentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SignupFragmentBinding.bind(view)

        binding.signupLogin.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerId,LoginFragment()).commit()
        }



    }

}