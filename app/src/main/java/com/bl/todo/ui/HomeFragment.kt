package com.bl.todo.ui

import android.Manifest
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

import android.content.Intent

import androidx.core.app.ActivityCompat

import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast


class HomeFragment : Fragment(R.layout.home_fragment) {
    private lateinit var binding: HomeFragmentBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var profileDialogView : View
    private lateinit var alertDialog: AlertDialog
    private lateinit var pleaseWaitDialog : Dialog
    private var menu : Menu? = null

    companion object{
        const val STORAGE_PERMISSION_CODE = 111
        const val IMAGE_FROM_GALLERY_CODE = 100
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeFragmentBinding.bind(view)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        pleaseWaitDialog = Dialog(requireContext())
        pleaseWaitDialog.setContentView(R.layout.dialog_loading)
        sharedViewModel = ViewModelProvider(requireActivity(), SharedViewModelFactory())[SharedViewModel::class.java]
        homeViewModel = ViewModelProvider(this, HomeViewModelFactory())[HomeViewModel::class.java]
        setHasOptionsMenu(true)
        profileDialog()
        observers()

    }

    private fun observers() {
        homeViewModel.userProfilePic.observe(viewLifecycleOwner){
            val profileImageButton = profileDialogView.findViewById<ImageButton>(R.id.profile_image)
            Log.i("ImageSet","Reached")
            profileImageButton.setImageBitmap(it)

            val item = menu?.findItem(R.id.profileIcon)
            item?.icon = BitmapDrawable(it)

            pleaseWaitDialog.dismiss()
        }
    }

    private fun profileDialog() {
        profileDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.profile_view,null)
        alertDialog = AlertDialog.Builder(requireContext()).setView(profileDialogView).create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        homeViewModel.getProfilePic()

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

        val profileImageButton = profileDialogView.findViewById<ImageButton>(R.id.profile_image)
        profileImageButton.setOnClickListener{
            if (ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                selectImageFromGallery()
            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
            }

        }

    }

    private fun selectImageFromGallery(){
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, IMAGE_FROM_GALLERY_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == STORAGE_PERMISSION_CODE && grantResults.isNotEmpty()){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(requireContext(),"Storage access required to upload",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IMAGE_FROM_GALLERY_CODE && data != null){
            pleaseWaitDialog.show()
            var imageUri = data.data
            var bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,imageUri)
            Log.i("Point","beforeSetting")
            homeViewModel.setProfilePic(bitmap)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
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