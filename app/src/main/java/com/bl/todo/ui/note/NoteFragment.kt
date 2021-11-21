package com.bl.todo.ui.note

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bl.todo.R
import com.bl.todo.data.room.DateTypeConverters
import com.bl.todo.databinding.NoteFragmentBinding
import com.bl.todo.ui.SharedViewModel
import com.bl.todo.ui.wrapper.NoteInfo
import com.bl.todo.ui.wrapper.UserDetails
import com.bl.todo.util.SharedPref
import java.util.*

class NoteFragment : Fragment(R.layout.note_fragment) {
    private lateinit var binding: NoteFragmentBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var noteViewModel: NoteViewModel
    private var bundleTitle: String? = null
    private var bundleContent: String? = null
    private var bundleFnid: String = ""
    private var bundleDateModified: Date? = null
    private var bundleNid: Long? = null
    private var bundleArchived : Boolean? = null
    private var userId = 0L

    companion object {
        var currentUser: UserDetails = UserDetails("name", "email", "phone", fUid = null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]
        binding = NoteFragmentBinding.bind(view)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        allListeners()
        observers()
        userId = SharedPref.getUserId()
        noteViewModel.getUserData(requireContext(), userId)
        setNoteContentFromBundle()
        if(bundleNid == null) {
            binding.archiveButton.visibility = View.GONE
        }
        archivedNote()
    }

    private fun archivedNote() {
        if(bundleArchived == false) {
            binding.archiveButton.setImageDrawable(
                AppCompatResources.getDrawable( requireContext(), R.drawable.archive_icon))
            binding.archiveButton.tag = "archiveNotSet"
        } else {
            binding.archiveButton.setImageDrawable(
                AppCompatResources.getDrawable( requireContext(), R.drawable.unarchive_icon))
            binding.archiveButton.tag = "archiveSet"
        }
        binding.archiveButton.setOnClickListener {
            if(bundleArchived == false) {
                val title = binding.noteTitle.text.toString()
                val content = binding.noteNotes.text.toString()
                var noteInfo = NoteInfo(
                    title = title, content = content,
                    fnid = bundleFnid, dateModified = bundleDateModified,
                    nid = bundleNid!!, archived = true
                )
                noteViewModel.updateNoteToDb(requireContext(), noteInfo, currentUser)
            } else if(bundleArchived == true){
                val title = binding.noteTitle.text.toString()
                val content = binding.noteNotes.text.toString()
                var noteInfo = NoteInfo(
                    title = title, content = content,
                    fnid = bundleFnid, dateModified = bundleDateModified,
                    nid = bundleNid!!, archived = false
                )
                noteViewModel.updateNoteToDb(requireContext(), noteInfo, currentUser)
            }
        }
    }

    private fun setNoteContentFromBundle() {
        bundleNid = arguments?.getLong("sqlNid")
        bundleTitle = arguments?.getString("title")
        bundleContent = arguments?.getString("content")
        bundleFnid = arguments?.getString("noteKey").toString()
        bundleArchived = arguments?.getBoolean("archived")
        var dateTime = DateTypeConverters().toOffsetDateTime(arguments?.getString("dateModified"))
        bundleDateModified = dateTime
        binding.noteTitle.setText(bundleTitle)
        binding.noteNotes.setText(bundleContent)
    }

    private fun observers() {
        noteViewModel.addNewNoteStatus.observe(viewLifecycleOwner) {
            if (it) {
                sharedViewModel.setGotoHomePageStatus(true)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.toastError_noteNotCreated),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        noteViewModel.updateNoteStatus.observe(viewLifecycleOwner) {
            if (it) {
                if(bundleArchived == true) {
                    sharedViewModel.setArchivedFragmentStatus(true)
                } else {
                    sharedViewModel.setGotoHomePageStatus(true)
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.toastError_updateNoteFailed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        noteViewModel.deleteNoteStatus.observe(viewLifecycleOwner) {
            if (it) {
                sharedViewModel.setGotoHomePageStatus(true)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.toastError_deleteNoteFailed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        noteViewModel.profileData.observe(viewLifecycleOwner) {
            currentUser = it
        }
    }

    private fun allListeners() {
        binding.notBackButton.setOnClickListener {
            sharedViewModel.setGotoHomePageStatus(true)
        }

        binding.noteSaveButton.setOnClickListener {
            val title = binding.noteTitle.text.toString()
            val content = binding.noteNotes.text.toString()
            if (bundleNid == null) {
                if (title.isEmpty() && content.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.toast_emptyNoteDiscarded),
                        Toast.LENGTH_SHORT
                    ).show()
                    sharedViewModel.setGotoHomePageStatus(true)
                } else {
                    var note = NoteInfo(
                        title = title,
                        content = content, dateModified = null
                    )
                    noteViewModel.addNoteToDb(requireContext(), note, currentUser)
                }
            } else {
                var noteInfo = NoteInfo(
                    title = title, content = content,
                    fnid = bundleFnid, dateModified = bundleDateModified,
                    nid = bundleNid!!, archived = bundleArchived!!
                )
                noteViewModel.updateNoteToDb(requireContext(), noteInfo, currentUser)
            }
        }

        binding.noteDeleteButton.setOnClickListener {
            if (bundleNid == null) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.toast_create_a_note_first_message),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Log.i("Note", "$bundleNid, $bundleFnid, $bundleTitle, $bundleContent")
                val noteInfo = NoteInfo(
                    bundleTitle!!, bundleContent!!,
                    bundleFnid, dateModified = bundleDateModified, nid = bundleNid!!
                )
                noteViewModel.deleteNoteToDb(requireContext(), noteInfo)
            }
        }
    }

}