package com.bl.todo.ui.note

import android.app.*
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.bl.todo.R
import com.bl.todo.notifications.NotificationWorker
import com.bl.todo.notifications.NotificationWorker.Companion.ARCHIVED
import com.bl.todo.notifications.NotificationWorker.Companion.CHANNEL_ID
import com.bl.todo.notifications.NotificationWorker.Companion.DATE
import com.bl.todo.notifications.NotificationWorker.Companion.FNID
import com.bl.todo.notifications.NotificationWorker.Companion.MESSAGE_EXTRA
import com.bl.todo.notifications.NotificationWorker.Companion.NID
import com.bl.todo.notifications.NotificationWorker.Companion.REMINDER
import com.bl.todo.notifications.NotificationWorker.Companion.TITLE_EXTRA
import com.bl.todo.databinding.NoteFragmentBinding
import com.bl.todo.ui.SharedViewModel
import com.bl.todo.ui.wrapper.NoteInfo
import com.bl.todo.ui.wrapper.UserDetails
import com.bl.todo.common.SharedPref
import com.bl.todo.common.Utilities
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class NoteFragment : Fragment(R.layout.note_fragment) {
    private lateinit var binding: NoteFragmentBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var noteViewModel: NoteViewModel
    private var bundleTitle: String? = null
    private var bundleContent: String? = null
    private var bundleFnId: String = ""
    private var bundleDateModified: Date? = null
    private var bundleNid: Long? = null
    private var bundleArchived : Boolean? = null
    private var bundleReminder : Date? = null
    private var userId = 0L
    private lateinit var alertDialog: AlertDialog
    private var reminder: Date? = null

    companion object {
        var currentUser: UserDetails = UserDetails("name", "email", "phone", fUid = null)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]
        binding = NoteFragmentBinding.bind(view)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        allListeners()
        observers()
        createNotificationChannel()
        userId = SharedPref.getUserId()
        noteViewModel.getUserData(requireContext(), userId)
        setNoteContentFromBundle()
        if(bundleNid == null) {
            binding.archiveButton.visibility = View.GONE
            binding.addReminderButton.visibility = View.GONE
        }
        archivedNote()
        reminder()
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
                val noteInfo = NoteInfo(
                    title = title, content = content,
                    fnid = bundleFnId, dateModified = bundleDateModified,
                    nid = bundleNid!!, archived = true, reminder = bundleReminder
                )
                noteViewModel.updateNoteToDb(requireContext(), noteInfo, currentUser)
            } else if(bundleArchived == true){
                val title = binding.noteTitle.text.toString()
                val content = binding.noteNotes.text.toString()
                val noteInfo = NoteInfo(
                    title = title, content = content,
                    fnid = bundleFnId, dateModified = bundleDateModified,
                    nid = bundleNid!!, archived = false, reminder = bundleReminder
                )
                noteViewModel.updateNoteToDb(requireContext(), noteInfo, currentUser)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun reminder() {
        if(bundleReminder != null) {
            binding.reminderNoteLayout.visibility = View.VISIBLE
            val formatter = SimpleDateFormat(getString(R.string.reminder_label_formatter_pattern),
                Locale.getDefault())
            val date = formatter.format(bundleReminder)
            binding.reminderTextView.text = date
        }

        binding.addReminderButton.setOnClickListener {
            val currentDateTime = Calendar.getInstance()
            val startYear = currentDateTime.get(Calendar.YEAR)
            val startMonth = currentDateTime.get(Calendar.MONTH)
            val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
            val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
            val startMinute = currentDateTime.get(Calendar.MINUTE)

            val dateTimeDialog = DatePickerDialog(requireContext(), { _, year, month, day ->
                TimePickerDialog(requireContext(), { _, hour, minute ->
                    val pickedDate = Calendar.getInstance()
                    pickedDate.set(year, month, day, hour, minute,0)
                    reminder = pickedDate.time
                    binding.reminderNoteLayout.visibility = View.VISIBLE
                    val formatter =
                        SimpleDateFormat(getString(R.string.reminder_label_formatter_pattern),
                            Locale.getDefault())
                    val date = formatter.format(reminder)
                    binding.reminderTextView.text = date
                    if(reminder != null) {
                        val title = binding.noteTitle.text.toString()
                        val content = binding.noteNotes.text.toString()
                        val noteInfo = NoteInfo(
                            title = title, content = content,
                            fnid = bundleFnId, dateModified = bundleDateModified,
                            nid = bundleNid!!, archived = bundleArchived!!, reminder = reminder)
                        noteViewModel.updateNoteToDb(requireContext(), noteInfo, currentUser)
                        scheduleNotification(noteInfo)
                    }
                }, startHour, startMinute, false).show()
            }, startYear, startMonth, startDay)

            dateTimeDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            dateTimeDialog.show()
        }

        binding.reminderNoteLayout.setOnClickListener {
            if(bundleReminder != null) {
                val title = binding.noteTitle.text.toString()
                val content = binding.noteNotes.text.toString()
                val alertDialog = AlertDialog.Builder(requireContext())
                    .setMessage(getString(R.string.reminder_delete_message))
                    .setPositiveButton(getString(R.string.yes_word)
                    ) { _, _ ->
                        val noteInfo = NoteInfo(
                            title = title, content = content,
                            fnid = bundleFnId, dateModified = bundleDateModified,
                            nid = bundleNid!!, archived = bundleArchived!!, reminder = null
                        )
                        noteViewModel.updateNoteToDb(requireContext(), noteInfo, currentUser)
                        binding.reminderNoteLayout.visibility = View.GONE
                        scheduleNotification(noteInfo, true)

                    }.setNegativeButton(getString(R.string.cancel_word)
                    ) { _, _ ->  }.create()
                alertDialog.show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleNotification(note: NoteInfo, cancel: Boolean = false) {
        val date = Utilities.dateToString(note.dateModified)
        val reminderDate = Utilities.dateToString(note.reminder)
        val inputData = Data.Builder()
            .putString(TITLE_EXTRA, note.title)
            .putString(MESSAGE_EXTRA,note.content)
            .putString(FNID,note.fnid)
            .putLong(NID,note.nid)
            .putString(DATE,date)
            .putBoolean(ARCHIVED,note.archived)
            .putString(REMINDER,reminderDate)
            .build()

        if(!cancel) {
            val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(note.reminder!!.time - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag(note.title)
                .build()
            WorkManager.getInstance(requireContext()).enqueueUniqueWork(note.fnid,
                ExistingWorkPolicy.REPLACE,notificationWork)
            Toast.makeText(requireContext(),
                "Notification set at ${note.reminder}",Toast.LENGTH_SHORT).show()
        }
        else {
            WorkManager.getInstance(requireContext()).cancelUniqueWork(note.fnid)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "Fundoo Notify Channel"
        val desc = "Fundoo Notification"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun setNoteContentFromBundle() {
        bundleNid = arguments?.getLong("sqlNid")
        bundleTitle = arguments?.getString("title")
        bundleContent = arguments?.getString("content")
        bundleFnId = arguments?.getString("noteKey").toString()
        bundleArchived = arguments?.getBoolean("archived")
        var dateTime = Utilities.stringToDate(arguments?.getString("dateModified"))
        bundleDateModified = dateTime
        var reminderInDate = Utilities.stringToDate(arguments?.getString("reminder"))
        bundleReminder = reminderInDate
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
                activity?.supportFragmentManager?.popBackStack()
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
            activity?.supportFragmentManager?.popBackStack()
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
                    val note = NoteInfo(
                        title = title,
                        content = content, dateModified = null
                    )
                    noteViewModel.addNoteToDb(requireContext(), note, currentUser)
                }
            } else {
                val noteInfo = NoteInfo(
                    title = title, content = content,
                    fnid = bundleFnId, dateModified = bundleDateModified,
                    nid = bundleNid!!, archived = bundleArchived!!, reminder = bundleReminder
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
                Log.i("Note", "$bundleNid, $bundleFnId, $bundleTitle, $bundleContent")
                val noteInfo = NoteInfo(
                    bundleTitle!!, bundleContent!!,
                    bundleFnId, dateModified = bundleDateModified, nid = bundleNid!!
                )
                noteViewModel.deleteNoteToDb(requireContext(), noteInfo)
            }
        }
    }
}