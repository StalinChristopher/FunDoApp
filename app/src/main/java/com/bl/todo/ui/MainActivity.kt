package com.bl.todo.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bl.todo.R
import com.bl.todo.databinding.ActivityMainBinding
import com.bl.todo.util.Utilities

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Utilities.replaceFragment(supportFragmentManager, R.id.fragmentContainerId, LoginFragment())



    }

}