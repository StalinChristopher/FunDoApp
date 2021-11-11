package com.bl.todo.ui.notePage

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
import com.bl.todo.data.wrapper.NewNote
import com.bl.todo.ui.mainActivity.SharedViewModel
import com.bl.todo.data.wrapper.NoteInfo
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
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        noteViewModel =  ViewModelProvider(this)[NoteViewModel::class.java]
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
                Toast.makeText(requireContext(),getString(R.string.toastError_noteNotCreated),Toast.LENGTH_SHORT).show()
            }
        }

        noteViewModel.updateNoteStatus.observe(viewLifecycleOwner){
            if(it){
                sharedViewModel.setGotoHomePageStatus(true)
            }else{
                Toast.makeText(requireContext(),getString(R.string.toastError_updateNoteFailed),Toast.LENGTH_SHORT).show()
            }
        }

        noteViewModel.deleteNoteStatus.observe(viewLifecycleOwner){
            if(it){
                sharedViewModel.setGotoHomePageStatus(true)
            }else{
                Toast.makeText(requireContext(),getString(R.string.toastError_deleteNoteFailed),Toast.LENGTH_SHORT).show()
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
                if(title.isEmpty() || content.isEmpty()){
                    Toast.makeText(requireContext(),getString(R.string.toast_emptyNoteDiscarded),Toast.LENGTH_SHORT).show()
                    sharedViewModel.setGotoHomePageStatus(true)
                }else{
                    var note = NewNote(title,content)
                    noteViewModel.addNoteToDb(note,formattedDateTime)
                }
            }else{
                var noteInfo = NoteInfo(title,content,bundleKey!!)
                noteViewModel.updateNoteToDb(noteInfo,formattedDateTime)
            }
        }

        binding.noteDeleteButton.setOnClickListener{
            if(bundleKey == null){
                Toast.makeText(requireContext(),getString(R.string.toast_create_a_note_first_message),Toast.LENGTH_SHORT).show()
            }else{
                val noteInfo = NoteInfo(bundleTitle!!,bundleContent!!,bundleKey!!)
                noteViewModel.deleteNoteToDb(noteInfo)
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