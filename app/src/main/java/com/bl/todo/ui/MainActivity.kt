package com.bl.todo.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.bl.todo.R
import com.bl.todo.databinding.ActivityMainBinding
import com.bl.todo.ui.homePage.HomeFragment
import com.bl.todo.ui.loginPage.LoginFragment
import com.bl.todo.ui.notePage.NoteFragment
import com.bl.todo.ui.resetPasswordPage.ResetPassword
import com.bl.todo.ui.signUpPage.SignUpFragment
import com.bl.todo.ui.splashScreen.SplashScreen
import com.bl.todo.ui.wrapper.NoteInfo
import com.bl.todo.util.SharedPref
import com.bl.todo.util.Utilities
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity(), SplashScreen.InteractionListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.homeToolbar)
        SharedPref.initializePref(this)
        sharedViewModel = ViewModelProvider(this@MainActivity)[SharedViewModel::class.java]
        observeAppNavigation()
//        sharedViewModel.setSplashScreenStatus(true)
        if (savedInstanceState == null) {
            gotoSplashScreen()
        }
        navDrawer()
    }

    private fun navDrawer() {
        toggle = object : ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.homeToolbar,
            R.string.open,
            R.string.close
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                val navHeader = binding.navView.getHeaderView(0)
                val navHeaderName = navHeader.findViewById<MaterialTextView>(R.id.nav_profile_name)
                navHeaderName.text = getString(R.string.navHeader)
            }
        }

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        var firstMenuItem = binding.navView.menu.getItem(0)
        firstMenuItem.isChecked = true
        binding.navView.setNavigationItemSelectedListener {
            firstMenuItem.isChecked = false
            when (it.itemId) {
                R.id.nav_notes_item -> {Toast.makeText(
                    this,
                    "Notes button clicked",
                    Toast.LENGTH_SHORT
                ).show()
                sharedViewModel.setGotoHomePageStatus(true)}
                R.id.nav_remainders_item -> Toast.makeText(
                    this,
                    "reminder button clicked",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.nav_logout_item -> {
                    sharedViewModel.logOutFromApp(this)
                    sharedViewModel.setLoginPageStatus(true)
                }
            }
            it.isCheckable = true
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun observeAppNavigation() {
        sharedViewModel.gotoHomePageStatus.observe(this@MainActivity, {
            if (it) {
                gotoHomePage()
            }
        })

        sharedViewModel.gotoLoginPageStatus.observe(this@MainActivity, {
            if (it) {
                gotoLoginPage()
            }
        })

        sharedViewModel.gotoSignupPageStatus.observe(this@MainActivity, {
            if (it) {
                gotoRegistrationPage()
            }
        })

//        sharedViewModel.gotoSplashScreenStatus.observe(this@MainActivity,{
//            if(it){
//                gotoSplashScreen()
//            }
//        })

        sharedViewModel.gotoForgotPasswordStatus.observe(this@MainActivity, {
            if (it) {
                gotoForgotPasswordScreen()
            }
        })

        sharedViewModel.gotoNoteFragmentStatus.observe(this@MainActivity, {
            if (it) {
                gotoNotePage()
            }
        })

        sharedViewModel.gotoExistingNoteFragmentStatus.observe(this@MainActivity, {
            gotoExistingNotePage(it)
        })
    }

    private fun gotoNotePage() {
        Utilities.replaceFragment(supportFragmentManager, R.id.fragmentContainerId, NoteFragment())
    }

    private fun gotoLoginPage() {
        Utilities.replaceFragment(supportFragmentManager, R.id.fragmentContainerId, LoginFragment())
    }

    private fun gotoRegistrationPage() {
        Utilities.replaceFragment(
            supportFragmentManager,
            R.id.fragmentContainerId,
            SignUpFragment()
        )
    }

    private fun gotoHomePage() {
        Utilities.replaceFragment(supportFragmentManager, R.id.fragmentContainerId, HomeFragment())
    }

    private fun gotoSplashScreen() {
        Utilities.replaceFragment(supportFragmentManager, R.id.fragmentContainerId, SplashScreen())
    }

    private fun gotoForgotPasswordScreen() {
        Utilities.replaceFragment(supportFragmentManager, R.id.fragmentContainerId, ResetPassword())
    }

    private fun gotoExistingNotePage(noteInfo: NoteInfo) {
        val noteFragment = NoteFragment()
        val bundle = Utilities.addNoteInfoToBundle(noteInfo)
        noteFragment.arguments = bundle
        Utilities.replaceFragment(supportFragmentManager, R.id.fragmentContainerId, noteFragment)
    }

    override fun onSplashScreenExit(loggedIn: Boolean) {
        if (loggedIn) {
            gotoHomePage()
        } else {
            gotoLoginPage()
        }
    }


}