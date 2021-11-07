package com.bl.todo.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bl.todo.R
import com.bl.todo.databinding.NoteFragmentBinding
import com.bl.todo.models.NewNote
import com.bl.todo.viewmodels.notePage.NoteViewModel
import com.bl.todo.viewmodels.notePage.NoteViewModelFactory
import com.bl.todo.viewmodels.sharedView.SharedViewModel
import com.bl.todo.viewmodels.sharedView.SharedViewModelFactory
import com.bl.todo.wrapper.NoteInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NoteFragment : Fragment(R.layout.note_fragment) {
    private lateinit var binding : NoteFragmentBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var noteViewModel: NoteViewModel
    private  var bundleTitle : String? = null
    private  var bundleContent : String? = null
    private  var bundleKey : String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity(), SharedViewModelFactory())[SharedViewModel::class.java]
        noteViewModel =  ViewModelProvider(this,NoteViewModelFactory())[NoteViewModel::class.java]
        binding = NoteFragmentBinding.bind(view)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        allListeners()
        observers()
        setNoteContentFromBundle()
    }

    private fun setNoteContentFromBundle() {
        bundleTitle = arguments?.getString("title")
        bundleContent = arguments?.getString("content")
        bundleKey = arguments?.getString("noteKey")
        binding.noteTitle.setText(bundleTitle)
        binding.noteNotes.setText(bundleContent)
        Log.i("FromNote","$bundleKey")
    }

    private fun observers() {
        noteViewModel.addNewNoteStatus.observe(viewLifecycleOwner){
            if(it){
                sharedViewModel.setGotoHomePageStatus(true)
            }else{
                Toast.makeText(requireContext(),"Note not created",Toast.LENGTH_SHORT).show()
            }
        }

        noteViewModel.updateNoteStatus.observe(viewLifecycleOwner){
            if(it){
                sharedViewModel.setGotoHomePageStatus(true)
            }else{
                Toast.makeText(requireContext(),"Update note failed",Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun allListeners() {
        binding.notBackButton.setOnClickListener{
            sharedViewModel.setGotoHomePageStatus(true)
        }

        binding.noteSaveButton.setOnClickListener{
            var title = binding.noteTitle.text.toString()
            var content = binding.noteNotes.text.toString()
            var formattedDateTime = getCurrentDateTime()
            if(bundleKey == null){
                var note = NewNote(title,content)
                noteViewModel.addNoteToDb(note,formattedDateTime)
            }else{
                var noteInfo = NoteInfo(title,content,bundleKey!!)
                noteViewModel.updateNoteToDb(noteInfo,formattedDateTime)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDateTime(): String {
        var curDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm:ss")
        var formattedDateAndTime = curDateTime.format(formatter)
        Log.i("Date","$formattedDateAndTime")
        return formattedDateAndTime
    }
}