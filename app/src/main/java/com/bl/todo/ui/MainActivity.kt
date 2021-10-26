package com.bl.todo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bl.todo.R
import com.bl.todo.databinding.ActivityMainBinding
import com.bl.todo.models.DatabaseUser
import com.bl.todo.models.UserDetails
import com.bl.todo.services.Database
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
        sharedViewModel.setSplashScreenStatus(true)
        loginObservers()
        resetPasswordObserver()
        signUpObservers()
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

    private fun loginObservers(){
        sharedViewModel.loginStatus.observe(this@MainActivity){ user ->
            if(user.loginStatus){
                Toast.makeText(this@MainActivity,"User logged in ", Toast.LENGTH_SHORT).show()
                LoginFragment.dialog.dismiss()
                Database.getUserData {
                    var user = Utilities.createUserFromHashMap(it)
//                    TODO("adding user data to sharedPref")
                }
                sharedViewModel.setGotoHomePageStatus(true)
            }else {
                Toast.makeText(this@MainActivity,"Sign in failed", Toast.LENGTH_SHORT).show()
                LoginFragment.dialog.dismiss()
            }
        }

        sharedViewModel.facebookLoginStatus.observe(this@MainActivity){ user ->
            if(user.loginStatus){
                Toast.makeText(this@MainActivity,"User logged in", Toast.LENGTH_SHORT).show()
                var userDb = DatabaseUser(user.userName,user.email,user.phone)
                Database.addUserInfoDatabase(userDb){
                        status ->
                    if(!status){
                        Log.e("DatabaseError","write failed")
                        LoginFragment.dialog.dismiss()
                    }else{
//                        TODO("add user object in sharedPref")
                        LoginFragment.dialog.dismiss()
                    }
                }
                sharedViewModel.setGotoHomePageStatus(true)
            }else {
                Toast.makeText(this@MainActivity,"Facebook login unsuccessful", Toast.LENGTH_SHORT).show()
                LoginFragment.dialog.dismiss()
            }

        }
    }

    private fun resetPasswordObserver() {
        sharedViewModel.resetPasswordStatus.observe(this@MainActivity){
            if(it){
                Toast.makeText(this@MainActivity,"Email has been sent to reset the password",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@MainActivity,"No account is associated with the given email",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signUpObservers() {
        sharedViewModel.signUpStatus.observe(this@MainActivity){
            SignUpFragment.userName = SignUpFragment.binding.signupUsername
            SignUpFragment.email = SignUpFragment.binding.signupEmail
            SignUpFragment.phone = SignUpFragment.binding.signupMobile
            var user = DatabaseUser(
                SignUpFragment.userName.text.toString(),
                SignUpFragment.email.text.toString(),
                SignUpFragment.phone.text.toString())
            if(it.loginStatus){
                Toast.makeText(this@MainActivity,"User signed up",Toast.LENGTH_SHORT).show()
                Database.addUserInfoDatabase(user){status ->
                    if(!status){
                        Log.e("DatabaseError","write failed")
                        SignUpFragment.dialog.dismiss()
                    }else{
//                        TODO("add user object in sharedPref")
                        sharedViewModel.setGotoHomePageStatus(true)
                        SignUpFragment.dialog.dismiss()
                    }
                }
            }
            else{
                Toast.makeText(this@MainActivity,"Account not created",Toast.LENGTH_SHORT).show()
                SignUpFragment.dialog.dismiss()
            }
        }
    }


}