package com.bl.todo.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bl.todo.R
import com.bl.todo.databinding.SplashScreenBinding
import com.bl.todo.services.Authentication
import com.bl.todo.services.FirebaseDatabaseService
import com.bl.todo.viewmodels.sharedView.SharedViewModel
import com.bl.todo.viewmodels.sharedView.SharedViewModelFactory

class SplashScreen : Fragment(R.layout.splash_screen) {
    private lateinit var binding: SplashScreenBinding
    private lateinit var sharedViewModel : SharedViewModel
    private var interactionListener :InteractionListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity(), SharedViewModelFactory())[SharedViewModel::class.java]
        binding = SplashScreenBinding.bind(view)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        binding.splashIcon.alpha = 0f
        binding.splashIcon.animate().setDuration(1500).alpha(1f).withEndAction {
            var user = Authentication.getCurrentUser()
            if( user != null){
                interactionListener?.onSplashScreenExit(true)
            }else{
                interactionListener?.onSplashScreenExit(false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.splashIcon.clearAnimation()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        interactionListener = context as? InteractionListener

    }

    interface InteractionListener {
        fun onSplashScreenExit(loggedIn : Boolean)
    }
}