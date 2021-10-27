package com.bl.todo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bl.todo.R
import com.bl.todo.databinding.ActivityMainBinding
import com.bl.todo.util.SharedPref
import com.bl.todo.util.Utilities
import com.bl.todo.viewmodels.sharedView.SharedViewModel
import com.bl.todo.viewmodels.sharedView.SharedViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SharedPref.initializePref(this)
        sharedViewModel = ViewModelProvider(this@MainActivity, SharedViewModelFactory())[SharedViewModel::class.java]
        observeAppNavigation()
        sharedViewModel.setSplashScreenStatus(true)
    }

    private fun observeAppNavigation(){
        sharedViewModel.gotoHomePageStatus.observe(this@MainActivity,{
            if(it){
                gotoHomePage()
            }
        })

        sharedViewModel.gotoLoginPageStatus.observe(this@MainActivity,{
            if(it){
                gotoLoginPage()
            }
        })

        sharedViewModel.gotoSignupPageStatus.observe(this@MainActivity, {
            if(it){
                gotoRegistrationPage()
            }
        })

        sharedViewModel.gotoSplashScreenStatus.observe(this@MainActivity,{
            if(it){
                gotoSplashScreen()
            }
        })

        sharedViewModel.gotoForgotPasswordStatus.observe(this@MainActivity, {
            if(it){
                gotoForgotPasswordScreen()
            }
        })
    }

    private fun gotoLoginPage(){
        Utilities.replaceFragment(supportFragmentManager, R.id.fragmentContainerId, LoginFragment())
    }

    private fun gotoRegistrationPage(){
        Utilities.replaceFragment(supportFragmentManager, R.id.fragmentContainerId, SignUpFragment())
    }

    private fun gotoHomePage() {
        Utilities.replaceFragment(supportFragmentManager, R.id.fragmentContainerId, HomeFragment())
    }

    private fun gotoSplashScreen(){
        Utilities.replaceFragment(supportFragmentManager,R.id.fragmentContainerId,SplashScreen())
    }

    private fun gotoForgotPasswordScreen(){
        Utilities.replaceFragment(supportFragmentManager,R.id.fragmentContainerId,ResetPassword())
    }




}