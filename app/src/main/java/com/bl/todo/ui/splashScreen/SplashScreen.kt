package com.bl.todo.ui.splashScreen

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bl.todo.R
import com.bl.todo.databinding.SplashScreenBinding
import com.bl.todo.authService.Authentication
import com.bl.todo.data.services.DatabaseService
import com.bl.todo.ui.mainActivity.SharedViewModel
import com.bl.todo.util.SharedPref

class SplashScreen : Fragment(R.layout.splash_screen) {
    private lateinit var binding: SplashScreenBinding
    private lateinit var sharedViewModel : SharedViewModel
    private var interactionListener : InteractionListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        binding = SplashScreenBinding.bind(view)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        binding.splashIcon.alpha = 0f
        binding.splashIcon.animate().setDuration(1500).alpha(1f).withEndAction {
            var user = Authentication.getCurrentUser()
            var userId = SharedPref.getUserId()
            if( userId > 0L){
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