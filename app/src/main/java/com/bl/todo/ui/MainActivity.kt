package com.bl.todo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bl.todo.R
import com.bl.todo.databinding.ActivityMainBinding
import com.bl.todo.util.Utilities
import com.bl.todo.viewmodels.SharedViewModel
import com.bl.todo.viewmodels.SharedViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedViewModel = ViewModelProvider(this@MainActivity, SharedViewModelFactory())[SharedViewModel::class.java]
        observeAppNavigation()
//        gotoLoginPage()
        Utilities.replaceFragment(supportFragmentManager,R.id.fragmentContainerId,SplashScreen())
    }

    private fun observeAppNavigation(){
        sharedViewModel.gotoHomePageStatus.observe(this@MainActivity,{
            if(it.loginStatus){
                var bundle = Utilities.addInfoToBundle(it)
                var profileFragment = ProfileFragment()
                profileFragment.arguments = bundle
                gotoHomePage(profileFragment)
            }
        })
    }

    private fun gotoLoginPage(){
        Utilities.replaceFragment(supportFragmentManager, R.id.fragmentContainerId, LoginFragment())
    }

    private fun gotoRegistrationPage(){
        Utilities.replaceFragment(supportFragmentManager, R.id.fragmentContainerId, SignUpFragment())
    }

    private fun gotoHomePage(profileFragment: ProfileFragment) {
        Utilities.replaceFragment(supportFragmentManager, R.id.fragmentContainerId, profileFragment)
    }


}