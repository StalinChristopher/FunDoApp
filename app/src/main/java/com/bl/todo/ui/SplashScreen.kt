package com.bl.todo.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bl.todo.R
import com.bl.todo.databinding.SplashScreenBinding
import com.bl.todo.services.Authentication
import com.bl.todo.services.Database
import com.bl.todo.util.Utilities

class SplashScreen : Fragment(R.layout.splash_screen) {
    private lateinit var binding: SplashScreenBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SplashScreenBinding.bind(view)
        binding.splashIcon.alpha = 0f
        binding.splashIcon.animate().setDuration(1500).alpha(1f).withEndAction {
            var user = Authentication.getCurrentUser()
            if( user != null){
                Database.getUserData(user.uid){ status, bundle->
                    var profileObj = ProfileFragment()
                    profileObj.arguments = bundle
                    requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerId,profileObj).commit()
                }
            }else{
                Utilities.replaceFragment(requireActivity().supportFragmentManager,R.id.fragmentContainerId,LoginFragment())
            }
        }

    }
}