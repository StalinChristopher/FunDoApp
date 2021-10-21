package com.bl.todo.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.bl.todo.R
import com.bl.todo.databinding.ProfileFragmentBinding
import com.bl.todo.services.FirebaseAuthentication

class ProfileFragment : Fragment(R.layout.profile_fragment) {
    private lateinit var binding: ProfileFragmentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ProfileFragmentBinding.bind(view)

        var emailValue :String = arguments?.get("email").toString()
        var name = arguments?.get("name").toString()
        var phone = arguments?.get("phone").toString()

        binding.profileViewEmail.text = "Email : $emailValue"
        binding.profileViewName.text = "Name : $name"
        binding.profileViewPhone.text = "Phone : $phone"
        binding.profileLogout.setOnClickListener {
            FirebaseAuthentication.logOut()
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerId,LoginFragment()).commit()
        }

    }
}