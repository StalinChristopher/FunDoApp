package com.bl.todo.ui

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.bl.todo.R
import com.bl.todo.databinding.ActivityMainBinding
import com.bl.todo.ui.home.HomeFragment
import com.bl.todo.ui.labels.LabelViewModel
import com.bl.todo.ui.labels.LabelsFragment
import com.bl.todo.ui.login.LoginFragment
import com.bl.todo.ui.note.NoteFragment
import com.bl.todo.ui.resetpassword.ResetPassword
import com.bl.todo.ui.signup.SignUpFragment
import com.bl.todo.ui.splash.SplashScreen
import com.bl.todo.ui.wrapper.LabelDetails
import com.bl.todo.ui.wrapper.NoteInfo
import com.bl.todo.ui.wrapper.UserDetails
import com.bl.todo.util.SharedPref
import com.bl.todo.util.Utilities
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity(), SplashScreen.InteractionListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var labelViewModel: LabelViewModel
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private var labelList = ArrayList<LabelDetails>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.homeToolbar)
        drawerLayout = binding.drawerLayout
        SharedPref.initializePref(this)
        sharedViewModel = ViewModelProvider(this@MainActivity)[SharedViewModel::class.java]
        labelViewModel = ViewModelProvider(this@MainActivity)[LabelViewModel::class.java]
        observeAppNavigation()
        observers()
//        sharedViewModel.setSplashScreenStatus(true)
        if (savedInstanceState == null) {
            gotoSplashScreen()
        }
        navDrawer()
        labelViewModel.getAllLabels(this)
    }

    private fun observers() {
        labelViewModel.getAllLabelStatus.observe(this) {
            Log.e("MainAct","$it")
            labelList = it
        }
    }

    fun lockDrawerLayout() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun navDrawer() {
        toggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
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

        drawerLayout.addDrawerListener(toggle)
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
                R.id.createNewLabelNavItem -> {
                    sharedViewModel.setLabelCreationFragmentStatus(true)
                }
            }
            if(R.id.createNewLabelNavItem != it.itemId){
                it.isCheckable = true
            }
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

        sharedViewModel.gotoLabelCreationFragmentStatus.observe(this, {
            if(it) {
                gotoLabelCreationPage()
            }
        })
    }

    private fun gotoNotePage() {
        Utilities.replaceFragment(supportFragmentManager, R.id.fragmentContainerId, NoteFragment())
    }

    private fun gotoLoginPage() {
        Utilities.replaceFragment(supportFragmentManager, R.id.fragmentContainerId, LoginFragment())
    }

    private fun gotoRegistrationPage() {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerId,SignUpFragment())
            .addToBackStack(null).commit()
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

    private fun gotoLabelCreationPage() {
        Utilities.replaceFragment(supportFragmentManager, R.id.fragmentContainerId, LabelsFragment())
    }

    override fun onSplashScreenExit(loggedIn: Boolean) {
        if (loggedIn) {
            gotoHomePage()
        } else {
            gotoLoginPage()
        }
    }
}