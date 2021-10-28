package com.bl.todo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.bl.todo.R
import com.bl.todo.databinding.ActivityMainBinding
import com.bl.todo.services.Authentication
import com.bl.todo.util.SharedPref
import com.bl.todo.util.Utilities
import com.bl.todo.viewmodels.homePage.HomeViewModel
import com.bl.todo.viewmodels.homePage.HomeViewModelFactory
import com.bl.todo.viewmodels.sharedView.SharedViewModel
import com.bl.todo.viewmodels.sharedView.SharedViewModelFactory
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var toggle : ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.homeToolbar)
        SharedPref.initializePref(this)
        sharedViewModel = ViewModelProvider(this@MainActivity, SharedViewModelFactory())[SharedViewModel::class.java]
        observeAppNavigation()
        sharedViewModel.setSplashScreenStatus(true)
        navDrawer()
    }

    private fun navDrawer(){
        toggle = object : ActionBarDrawerToggle(this, binding.drawerLayout, binding.homeToolbar, R.string.open, R.string.close){
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                var navHeader = binding.navView.getHeaderView(0)
                var navHeaderName = navHeader.findViewById<MaterialTextView>(R.id.nav_profile_name)
                navHeaderName.text = SharedPref.getValue("userName")
            }
        }

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_notes_item -> Toast.makeText(this,"Notes button clicked",Toast.LENGTH_SHORT).show()
                R.id.nav_remainders_item -> Toast.makeText(this,"reminder button clicked",Toast.LENGTH_SHORT).show()
                R.id.nav_logout_item -> { sharedViewModel.logOutFromApp()
                    sharedViewModel.setLoginPageStatus(true)
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
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