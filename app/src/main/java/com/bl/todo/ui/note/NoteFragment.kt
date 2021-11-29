package com.bl.todo.ui.note

import android.app.*
import android.content.Context
import android.content.Intent
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
import com.bl.todo.R
import com.bl.todo.common.Notification
import com.bl.todo.common.Notification.Companion.CHANNEL_ID
import com.bl.todo.common.Notification.Companion.MESSAGE_EXTRA
import com.bl.todo.common.Notification.Companion.NOTE
import com.bl.todo.common.Notification.Companion.NOTIFICATION_ID
import com.bl.todo.common.Notification.Companion.TITLE_EXTRA
import com.bl.todo.databinding.NoteFragmentBinding
import com.bl.todo.ui.SharedViewModel
import com.bl.todo.ui.wrapper.NoteInfo
import com.bl.todo.ui.wrapper.UserDetails
import com.bl.todo.common.SharedPref
import com.bl.todo.common.Utilities
import java.text.SimpleDateFormat
import java.util.*

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
                        scheduleNotification(noteInfo, title,content,reminder)
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
                        scheduleNotification(noteInfo, bundleTitle!!,bundleContent!!,reminder, true)

                    }.setNegativeButton(getString(R.string.cancel_word)
                    ) { _, _ ->  }.create()
                alertDialog.show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleNotification(note: NoteInfo,title: String, message: String, time: Date?, cancel: Boolean = false) {
        val intent = Intent(requireContext().applicationContext, Notification::class.java)
        intent.putExtra(TITLE_EXTRA, title)
        intent.putExtra(MESSAGE_EXTRA, message)
        intent.putExtra(NOTE,note)

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext().applicationContext,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if(!cancel) {
            time?.time?.let {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    it,
                    pendingIntent
                )
            }
            Toast.makeText(requireContext(),
                "Notification set at $time",Toast.LENGTH_SHORT).show()
        }
        else {
            alarmManager.cancel(pendingIntent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "Fundo Notify Channel"
        val desc = "Fundo Notification"
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