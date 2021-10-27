package com.bl.todo.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bl.todo.R
import com.bl.todo.databinding.HomeFragmentBinding
import com.bl.todo.util.SharedPref
import com.bl.todo.viewmodels.sharedView.SharedViewModel
import com.bl.todo.viewmodels.sharedView.SharedViewModelFactory
import com.bl.todo.viewmodels.homePage.HomeViewModel
import com.bl.todo.viewmodels.homePage.HomeViewModelFactory
import com.facebook.share.Share
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class HomeFragment : Fragment(R.layout.home_fragment) {
    private lateinit var binding: HomeFragmentBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var alertDialog: AlertDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeFragmentBinding.bind(view)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        sharedViewModel = ViewModelProvider(requireActivity(), SharedViewModelFactory())[SharedViewModel::class.java]
        homeViewModel = ViewModelProvider(this, HomeViewModelFactory())[HomeViewModel::class.java]
        setHasOptionsMenu(true)
        profileDialog()

    }

    private fun profileDialog() {
        var profileDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.profile_view,null)
        alertDialog = AlertDialog.Builder(requireContext()).setView(profileDialogView).create()

        val userName  = profileDialogView.findViewById<MaterialTextView>(R.id.profileDialogName)
        val email = profileDialogView.findViewById<MaterialTextView>(R.id.profileDialogEmail)
        val phone = profileDialogView.findViewById<MaterialTextView>(R.id.profileDialogPhone)

        userName.text = SharedPref.getValue("userName")
        email.text = SharedPref.getValue("email")
        phone.text = SharedPref.getValue("Phone")

        val profileDialogLogout = profileDialogView.findViewById<MaterialButton>(R.id.profileDialogButton)
        profileDialogLogout.setOnClickListener{
            homeViewModel.logOutFromHomePage()
            sharedViewModel.setLoginPageStatus(true)
            alertDialog.dismiss()
        }

        val closeProfile = profileDialogView.findViewById<ImageView>(R.id.profileDialogClose)
        closeProfile.setOnClickListener{
            alertDialog.dismiss()
        }

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var itemView = item.itemId
        when(itemView){
            R.id.profileIcon -> alertDialog.show()
        }
        return true
    }
}