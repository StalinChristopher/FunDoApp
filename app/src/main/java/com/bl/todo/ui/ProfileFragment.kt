package com.bl.todo.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bl.todo.R
import com.bl.todo.databinding.ProfileFragmentBinding
import com.bl.todo.services.Authentication
import com.bl.todo.viewmodels.SharedViewModel
import com.bl.todo.viewmodels.SharedViewModelFactory

class ProfileFragment : Fragment(R.layout.profile_fragment) {
    private lateinit var binding: ProfileFragmentBinding
    private lateinit var sharedViewModel: SharedViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ProfileFragmentBinding.bind(view)
        sharedViewModel = ViewModelProvider(requireActivity(), SharedViewModelFactory())[SharedViewModel::class.java]

        var emailValue :String = arguments?.get("email").toString()
        var name = arguments?.get("name").toString()
        var phone = arguments?.get("phone").toString()


        binding.profileViewEmail.text = "Email : $emailValue"
        binding.profileViewName.text = "Name : $name"
        binding.profileViewPhone.text = "Phone : $phone"
        binding.profileLogout.setOnClickListener {
            Authentication.logOut()
            sharedViewModel.setLoginPageStatus(true)
        }

    }
}