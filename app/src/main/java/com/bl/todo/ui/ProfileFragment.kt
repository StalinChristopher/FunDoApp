package com.bl.todo.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bl.todo.R
import com.bl.todo.databinding.ProfileFragmentBinding

class ProfileFragment : Fragment(R.layout.profile_fragment) {
    private lateinit var binding: ProfileFragmentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ProfileFragmentBinding.bind(view)
        var emailValue :String = arguments?.get("email") as String
        binding.profileViewEmail.text = emailValue



    }
}