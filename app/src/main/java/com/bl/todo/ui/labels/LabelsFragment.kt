package com.bl.todo.ui.labels

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bl.todo.R
import com.bl.todo.databinding.LabelFragmentBinding
import com.bl.todo.ui.SharedViewModel
import com.bl.todo.ui.wrapper.LabelDetails
import com.bl.todo.ui.wrapper.NoteInfo

class LabelsFragment : Fragment(R.layout.label_fragment) {
    private lateinit var binding : LabelFragmentBinding
    private lateinit var sharedViewModel : SharedViewModel
    private lateinit var labelViewModel: LabelViewModel
    private lateinit var labelRecyclerView: RecyclerView
    private lateinit var labelAdapter: LabelAdapter
    private var labelList: ArrayList<LabelDetails> = ArrayList()
    private var mode = ADD_LABEL
    private var noteInfo : NoteInfo? = null
    private var checkedLabelList : ArrayList<LabelDetails> = ArrayList()
    private var labelsFromDbList : ArrayList<LabelDetails> = ArrayList()
    private lateinit var dialog: Dialog

    companion object {
        const val ADD_LABEL = 0
        const val SELECT_LABEL = 1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LabelFragmentBinding.bind(view)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        labelViewModel = ViewModelProvider(requireActivity())[LabelViewModel::class.java]
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_loading)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        setLabelMode()
        initializeRecyclerView()
        allListeners()
        labelViewModel.getUserData(requireContext())
        labelViewModel.getAllLabels(requireContext())
        allObservers()

    }

    private fun setLabelMode() {
        mode = arguments?.getInt("MODE")!!
        when(mode) {
            SELECT_LABEL -> {
                binding.createNewLabelLayout.isVisible = false
                binding.labelPageFloatingButton.isVisible = true
                noteInfo = arguments?.getSerializable("NOTE") as NoteInfo
                Log.i("LabelsFragment","note: $noteInfo")
                labelsFromDbList = arguments?.getSerializable("NOTELIST") as ArrayList<LabelDetails>
            }
        }
    }

    private fun allObservers() {
        labelViewModel.addLabelStatus.observe(viewLifecycleOwner) {
            labelList.add(it)
            labelAdapter.notifyItemInserted(labelAdapter.itemCount)
        }

        labelViewModel.getAllLabelStatus.observe(viewLifecycleOwner) {
            if( it != null) {
                labelList.clear()
                labelList.addAll(it)
                labelAdapter.notifyDataSetChanged()
            }
        }

        labelViewModel.deleteLabelStatus.observe(viewLifecycleOwner) {
            if(it != null) {
                val pos = labelList.indexOf(it)
                labelList.remove(it)
                labelAdapter.notifyItemRemoved(pos)

            }
        }

        labelViewModel.editLabelStatus.observe(viewLifecycleOwner) {
            if(it != null) {
                var pos : Int
                labelList.forEachIndexed { index,item ->
                    if(item.labelFid == it.labelFid) {
                        item.dateModified = it.dateModified
                        item.labelName = it.labelName
                        pos = index
                        labelAdapter.notifyItemChanged(pos)
                    }
                }
            }
        }

        labelViewModel.labelNoteLinkStatus.observe(viewLifecycleOwner) {
            if(it != null) {
                Log.i("LabelFragment","labels linked")
                labelViewModel.resetLinkLabelAndStatus()
                activity?.supportFragmentManager?.popBackStack()
            }
        }
    }

    private fun allListeners() {
        binding.labelPageBackButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        binding.labelPageFloatingButton.setOnClickListener {
            for ( item in labelList) {
                if(item.isChecked) {
                    checkedLabelList.add(item)
                }
            }
            val tempLabels = ArrayList<LabelDetails>()
            tempLabels.addAll(labelsFromDbList)
            tempLabels.removeAll(checkedLabelList)
            tempLabels.forEach {
                var linkId = "${noteInfo?.fnid}_${it.labelFid}"
                labelViewModel.removeLabelAndNoteLink(requireContext(), linkId)
            }
            labelViewModel.linkLabelAndNote(requireContext(), checkedLabelList, noteInfo!!)
        }

        binding.createNewLabelEditText.setOnFocusChangeListener { _, b ->
            if(b) {
                binding.createLabelButton.visibility = View.VISIBLE
                binding.closeButton.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.label_close_key))
                binding.closeButton.tag = "close"
            } else {
                binding.createLabelButton.visibility = View.GONE
                binding.closeButton.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.label_add))
                binding.createNewLabelEditText.setText("")
                binding.closeButton.tag = "add"
            }
        }

        binding.closeButton.setOnClickListener {
            if(binding.closeButton.tag == "add") {
                binding.createNewLabelEditText.requestFocus()
            } else {
                binding.createNewLabelEditText.clearFocus()
            }
        }

        binding.createLabelButton.setOnClickListener {
            var labelName = binding.createNewLabelEditText.text.toString()
            var label = LabelDetails(labelName = labelName, dateModified = null)
            binding.createNewLabelEditText.setText("")
            labelViewModel.addNewLabel(requireContext(), label)
        }
    }

    private fun initializeRecyclerView() {
        labelAdapter = LabelAdapter(requireContext(), labelList, labelViewModel, mode, labelsFromDbList)
        labelRecyclerView = binding.labelRecyclerView
        labelRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        labelRecyclerView.setHasFixedSize(true)
        labelRecyclerView.adapter = labelAdapter
    }
}