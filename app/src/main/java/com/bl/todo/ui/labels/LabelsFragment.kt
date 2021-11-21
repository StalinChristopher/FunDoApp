package com.bl.todo.ui.labels

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bl.todo.R
import com.bl.todo.databinding.LabelFragmentBinding
import com.bl.todo.ui.SharedViewModel
import com.bl.todo.ui.wrapper.LabelDetails
import com.bl.todo.ui.wrapper.UserDetails
import com.bl.todo.util.SharedPref

class LabelsFragment : Fragment(R.layout.label_fragment) {
    private lateinit var binding : LabelFragmentBinding
    private lateinit var sharedViewModel : SharedViewModel
    private lateinit var labelViewModel: LabelViewModel
    private lateinit var labelRecyclerView: RecyclerView
    private lateinit var labelAdapter: LabelAdapter
    private var labelList: ArrayList<LabelDetails> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LabelFragmentBinding.bind(view)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        labelViewModel = ViewModelProvider(requireActivity())[LabelViewModel::class.java]
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        initializeRecyclerView()
        allListeners()
        labelViewModel.getUserData(requireContext())
        labelViewModel.getAllLabels(requireContext())
        allObservers()

    }

    private fun allObservers() {
        labelViewModel.addLabelStatus.observe(viewLifecycleOwner) {
            labelList.add(it)
            labelAdapter.notifyDataSetChanged()
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
    }

    private fun allListeners() {
        binding.labelPageBackButton.setOnClickListener {
            sharedViewModel.setGotoHomePageStatus(true)
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
        labelAdapter = LabelAdapter(requireContext(), labelList, labelViewModel)
        labelRecyclerView = binding.labelRecyclerView
        labelRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        labelRecyclerView.setHasFixedSize(true)
        labelRecyclerView.adapter = labelAdapter
    }
}