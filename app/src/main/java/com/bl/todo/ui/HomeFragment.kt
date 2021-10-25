package com.bl.todo.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bl.todo.R
import com.bl.todo.databinding.HomeFragmentBinding
import com.bl.todo.services.Authentication
import com.bl.todo.viewmodels.SharedViewModel
import com.bl.todo.viewmodels.SharedViewModelFactory

class HomeFragment : Fragment(R.layout.home_fragment) {
    private lateinit var binding: HomeFragmentBinding
    private lateinit var sharedViewModel: SharedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeFragmentBinding.bind(view)
        sharedViewModel = ViewModelProvider(requireActivity(), SharedViewModelFactory())[SharedViewModel::class.java]
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.homeToolbar)
        setHasOptionsMenu(true)

        var emailValue :String = arguments?.get("email").toString()
        var name = arguments?.get("name").toString()
        var phone = arguments?.get("phone").toString()


        binding.profileViewEmail.text = "Email : $emailValue"
        binding.profileViewName.text = "Name : $name"
        binding.profileViewPhone.text = "Phone : $phone"
        binding.profileLogout.setOnClickListener {
            Authentication.logOut()
            sharedViewModel.setLoginPageStatus(true)
        }

        binding.homeToolbar.setNavigationOnClickListener {
            Toast.makeText(requireContext(),"Navigation drawer button clicked", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var itemView = item.itemId
        when(itemView){
            R.id.profileIcon -> Toast.makeText(requireContext(),"Profile button clicked", Toast.LENGTH_SHORT).show()
        }
        return true

    }
}