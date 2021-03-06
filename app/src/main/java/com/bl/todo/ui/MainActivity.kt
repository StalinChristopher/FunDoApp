package com.bl.todo.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.bl.todo.common.SharedPref
import com.bl.todo.common.Utilities
import com.bl.todo.ui.labels.LabelsFragment.Companion.ADD_LABEL
import com.bl.todo.ui.labels.LabelsFragment.Companion.SELECT_LABEL
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity(), SplashScreen.InteractionListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var labelViewModel: LabelViewModel
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private var labelList = ArrayList<LabelDetails>()
    private lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.homeToolbar)
        drawerLayout = binding.drawerLayout
        SharedPref.initializePref(this)
        sharedViewModel = ViewModelProvider(this@MainActivity)[SharedViewModel::class.java]
        labelViewModel = ViewModelProvider(this@MainActivity)[LabelViewModel::class.java]
        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_loading)
        observeAppNavigation()
        observers()
        if (savedInstanceState == null) {
            gotoSplashScreen()
        }
        navDrawer()
        labelViewModel.getAllLabels(this)
        sharedViewModel.subscribeToTopic()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        when(intent?.getStringExtra("destination")) {
            "home" -> {
                if(sharedViewModel.checkUser()){
                    var note = intent.getSerializableExtra("noteInfo") as NoteInfo
                    gotoExistingNotePage(note)
                }
            }
        }
    }

    private fun observers() {
        labelViewModel.getAllLabelStatus.observe(this) {
            labelList = it
        }
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
                R.id.nav_notes_item -> { sharedViewModel.setGotoHomePageStatus(true)}
                R.id.nav_remainders_item -> {sharedViewModel.setReminderFragmentStatus(true)}
                R.id.nav_logout_item -> {
                    sharedViewModel.logOutFromApp(this)
                    sharedViewModel.setLoginPageStatus(true)
                }
                R.id.nav_archived_item -> {
                    sharedViewModel.setArchivedFragmentStatus(true)
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

        sharedViewModel.gotoArchivedFragmentStatus.observe(this, {
            if(it) {
                gotoArchivedPage()
            }
        })

        sharedViewModel.gotoReminderFragmentStatus.observe(this, {
            if(it) {
                gotoReminderPage()
            }
        })
    }

    private fun gotoReminderPage() {
        var homeFragment = HomeFragment()
        var bundle = Bundle()
        bundle.putString("type", "reminder")
        homeFragment.arguments = bundle
        Utilities.replaceFragment(supportFragmentManager, R.id.fragmentContainerId,homeFragment)
    }

    private fun gotoArchivedPage() {
        var homeFragment = HomeFragment()
        var bundle = Bundle()
        bundle.putString("type","archive")
        homeFragment.arguments = bundle
        Utilities.replaceFragment(supportFragmentManager, R.id.fragmentContainerId, homeFragment)
    }

    private fun gotoNotePage() {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerId,NoteFragment())
            .addToBackStack(null).commit()
    }

    private fun gotoLoginPage() {
        Utilities.replaceFragment(supportFragmentManager, R.id.fragmentContainerId, LoginFragment())
    }

    private fun gotoRegistrationPage() {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerId,SignUpFragment())
            .addToBackStack(null).commit()
    }

    private fun gotoHomePage() {
        var homeFragment = HomeFragment()
        var bundle = Bundle()
        bundle.putString("type","home")
        homeFragment.arguments = bundle
        Utilities.replaceFragment(supportFragmentManager, R.id.fragmentContainerId, homeFragment)
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
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerId,noteFragment)
            .addToBackStack(null).commit()
    }

    private fun gotoLabelCreationPage() {
        val createLabelFragment = LabelsFragment()
        var bundle = Bundle()
        bundle.putInt("MODE",ADD_LABEL)
        createLabelFragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerId,createLabelFragment)
            .addToBackStack(null).commit()
    }

    override fun onSplashScreenExit(loggedIn: Boolean) {
        if (loggedIn) {
            gotoHomePage()
        } else {
            gotoLoginPage()
        }
    }
}