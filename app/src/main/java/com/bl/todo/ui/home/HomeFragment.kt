package com.bl.todo.ui.home

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
import com.bl.todo.ui.SharedViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import android.content.Intent
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.appcompat.widget.SearchView
import com.bl.todo.data.services.DatabaseService
import com.bl.todo.ui.home.adapter.NoteAdapter
import com.bl.todo.ui.wrapper.NoteInfo
import com.bl.todo.ui.wrapper.UserDetails

class HomeFragment() : Fragment(R.layout.home_fragment) {
    private lateinit var binding: HomeFragmentBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var profileDialogView: View
    private lateinit var alertDialog: AlertDialog
    private lateinit var pleaseWaitDialog: Dialog
    private var menu: Menu? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var noteAdapter: NoteAdapter
    private var userId = 0L
    private var type : String = ""
    private var totalNotes : Int = 0
    private var isLoading : Boolean = false

    companion object {
        const val STORAGE_PERMISSION_CODE = 111
        const val IMAGE_FROM_GALLERY_CODE = 100
        private var noteList: ArrayList<NoteInfo> = ArrayList()
        var currentUser: UserDetails = UserDetails("name", "email", "phone", fUid = null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeFragmentBinding.bind(view)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        pleaseWaitDialog = Dialog(requireContext())
        pleaseWaitDialog.setContentView(R.layout.dialog_loading)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        setHasOptionsMenu(true)
        profileDialog()
        userId = SharedPref.getUserId()
        initializeRecyclerView()
        observers()
        setUserDetails()
        homeViewModel.getUserData(requireContext(), userId)
        allListeners()
        if(type == "archive" || type == "reminder"){
            binding.homePageFloatingButton.visibility = View.GONE
        }
        homeViewModel.getNotesCount(requireContext())
    }

    private fun allListeners() {
        binding.homePageFloatingButton.setOnClickListener {
            sharedViewModel.setNoteFragmentPageStatus(true)
        }

        noteAdapter.setOnItemClickListener(object : NoteAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                var note = noteList[position]
                sharedViewModel.setExistingNoteFragmentStatus(note)
            }
        })

        binding.refreshLayoutId.setOnRefreshListener {
            homeViewModel.syncDatabase(requireContext(), currentUser)
        }
    }

    private fun initializeRecyclerView() {
        noteAdapter = NoteAdapter(noteList)
        recyclerView = binding.HomeRecyclerView
        recyclerView.layoutManager = StaggeredGridLayoutManager(2,1)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = noteAdapter
        type = arguments?.getString("type").toString()
        when (type) {
            "archive" -> {
                homeViewModel.getArchivedNotes(requireContext())
            }
            "reminder" -> {
                homeViewModel.getReminderNotes(requireContext())
            }
            "home" -> {
                homeViewModel.getNotesFromUser(requireContext())
            }
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                lateinit var layoutManager : RecyclerView.LayoutManager
                var firstVisibleItem : Int = 0
                when(recyclerView.layoutManager) {
                    is LinearLayoutManager -> {
                        layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                    }
                    is StaggeredGridLayoutManager -> {
                        layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                        val firstVisibleItemArray = layoutManager.findFirstVisibleItemPositions(null)
                        firstVisibleItem = firstVisibleItemArray[0]
                    }
                }
                val visibleCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                if (((visibleCount + firstVisibleItem) >= totalItemCount) &&
                    (totalItemCount < totalNotes)) {
                    isLoading = true
                    when (type) {
                        "archive" -> {
                            homeViewModel.getPagedArchivedNotes(requireContext(),
                                10, totalItemCount)
                        }
                        "reminder" -> {
                            homeViewModel.getPagedReminderNotes(requireContext(),
                                10, totalItemCount)
                        }
                        "home" -> {
                            homeViewModel.getPagedNotes(requireContext()
                                , 10, totalItemCount)
                        }
                    }
                }
            }
        })
    }

    private fun observers() {
        homeViewModel.userProfilePic.observe(viewLifecycleOwner) {
            val profileImageButton = profileDialogView.findViewById<ImageButton>(R.id.profile_image)
            profileImageButton.setImageBitmap(it)

            val item = menu?.findItem(R.id.profileIcon)
            item?.icon = BitmapDrawable(it)

            pleaseWaitDialog.dismiss()
        }

        homeViewModel.userNotes.observe(viewLifecycleOwner) {
            if (it != null) {
                noteList.clear()
                noteList.addAll(it)
                noteAdapter.notifyDataSetChanged()
            }
        }

        homeViewModel.notesCount.observe(viewLifecycleOwner) {
            totalNotes = it
        }

        homeViewModel.pagedNotes.observe(viewLifecycleOwner) {
            isLoading = false
            noteList.addAll(it)
            noteAdapter.notifyItemRangeInserted(noteAdapter.itemCount, it.size)
            loadProgressBar()
        }

        homeViewModel.archivedNotes.observe(viewLifecycleOwner) {
            if (it != null) {
                noteList.clear()
                noteList.addAll(it)
                noteAdapter.notifyDataSetChanged()
            }
        }

        homeViewModel.reminderNotes.observe(viewLifecycleOwner) {
            if (it != null) {
                noteList.clear()
                noteList.addAll(it)
                noteAdapter.notifyDataSetChanged()
            }
        }

        homeViewModel.pagedArchivedNotes.observe(viewLifecycleOwner) {
            isLoading = false
            noteList.addAll(it)
            noteAdapter.notifyItemRangeInserted(noteAdapter.itemCount, it.size)
            loadProgressBar()
        }

        homeViewModel.pagedReminderNotes.observe(viewLifecycleOwner) {
            isLoading = false
            noteList.addAll(it)
            noteAdapter.notifyItemRangeInserted(noteAdapter.itemCount, it.size)
            loadProgressBar()
        }

        homeViewModel.profileData.observe(viewLifecycleOwner) {
            currentUser = it
            setUserDetails()
        }

        homeViewModel.syncStatus.observe(viewLifecycleOwner) {
            homeViewModel.getNotesFromUser(requireContext())
            binding.refreshLayoutId.isRefreshing = false
        }
    }

    private fun profileDialog() {
        profileDialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.profile_view, null)
        alertDialog = AlertDialog.Builder(requireContext()).setView(profileDialogView).create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        homeViewModel.getProfilePic()

        val closeProfile = profileDialogView.findViewById<ImageView>(R.id.profileDialogClose)
        closeProfile.setOnClickListener {
            alertDialog.dismiss()
        }

        val profileImageButton = profileDialogView.findViewById<ImageButton>(R.id.profile_image)
        profileImageButton.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                selectImageFromGallery()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE
                )
            }
        }

        val profileDialogLogout =
            profileDialogView.findViewById<MaterialButton>(R.id.profileDialogButton)
        profileDialogLogout.setOnClickListener {
            homeViewModel.logOutFromHomePage(requireContext())
            sharedViewModel.setLoginPageStatus(true)
            alertDialog.dismiss()
        }
    }

    private fun setUserDetails() {
        val userName = profileDialogView.findViewById<MaterialTextView>(R.id.profileDialogName)
        val email = profileDialogView.findViewById<MaterialTextView>(R.id.profileDialogEmail)
        userName.text = currentUser?.userName
        email.text = currentUser?.email
    }

    private fun selectImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, IMAGE_FROM_GALLERY_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.isNotEmpty()) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_storage_access_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_FROM_GALLERY_CODE && data != null) {
            pleaseWaitDialog.show()
            var imageUri = data.data
            var bitmap =
                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
            homeViewModel.setProfilePic(bitmap)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        inflater.inflate(R.menu.toolbar_menu, menu)
        changeLayoutRecyclerView()
        searchRecyclerView()
    }

    private fun searchRecyclerView() {
        val searchItem = menu?.getItem(0)
        var toggleItem = menu?.getItem(1)
        var profileIcon = menu?.getItem(2)
        val searchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                noteAdapter.filter.filter(newText)
                noteAdapter.notifyDataSetChanged()
                return false
            }
        })
    }

    private fun changeLayoutRecyclerView() {
        var toggleItem = menu?.getItem(1)
        var view = toggleItem?.actionView
        var button = view?.findViewById<AppCompatToggleButton>(R.id.toggle_button)
        button?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
            } else {
                recyclerView.layoutManager = StaggeredGridLayoutManager(2, 1)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var itemView = item.itemId
        when (itemView) {
            R.id.profileIcon -> alertDialog.show()
        }
        return true
    }

    private fun loadProgressBar() {
        if ( isLoading) {
            binding.paginationProgressBar.visibility = View.VISIBLE
        } else {
            binding.paginationProgressBar.visibility = View.GONE
        }
    }
}