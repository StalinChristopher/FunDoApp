package com.bl.todo.ui.labels

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bl.todo.R
import com.bl.todo.ui.MainActivity
import com.bl.todo.ui.labels.LabelsFragment.Companion.ADD_LABEL
import com.bl.todo.ui.wrapper.LabelDetails
import com.bl.todo.ui.wrapper.UserDetails

class LabelAdapter(var context: Context, private val labelList : ArrayList<LabelDetails>,
                   var labelViewModel: LabelViewModel, var mode: Int,
                   var labelListFromDb: ArrayList<LabelDetails>) :
    RecyclerView.Adapter<LabelAdapter.LabelViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.single_listitem_labelrecycler_view, parent, false)
        return LabelViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        val currentItem = labelList[position]
        if(mode == ADD_LABEL) {
            holder.labelEditText.setText(currentItem.labelName)

            holder.labelEditText.setOnFocusChangeListener { _, b ->
                if (b) {
                    holder.editLabelButton.setImageDrawable(
                        context.getDrawable(R.drawable.create_label_button))
                    holder.deleteLabelButton.setImageDrawable(
                        context.getDrawable(R.drawable.delete_icon))
                    holder.editLabelButton.tag = "save"
                    holder.deleteLabelButton.tag = "delete"
                } else {
                    holder.editLabelButton.setImageDrawable(context.getDrawable(R.drawable.edit_icon))
                    holder.deleteLabelButton.setImageDrawable(context.getDrawable(R.drawable.label_icon))
                    holder.editLabelButton.tag = "edit"
                    holder.deleteLabelButton.tag = "label"
                }
            }
            holder.deleteLabelButton.setOnClickListener {
                if(holder.deleteLabelButton.tag == "delete"){
                    labelViewModel.deleteLabel(context,currentItem)
                }
            }
            holder.editLabelButton.setOnClickListener {
                if(holder.editLabelButton.tag == "save"){
                    val newLabelName = holder.labelEditText.text
                    if(newLabelName.isEmpty()){
                        labelViewModel.deleteLabel(context, currentItem)
                    } else if(holder.labelEditText.text.toString() != currentItem.labelName) {
                        currentItem.labelName = newLabelName.toString()
                        labelViewModel.editLabel(context, currentItem)
                    }
                    holder.labelEditText.clearFocus()
                }
            }
        } else {
            holder.labelEditText.setText(currentItem.labelName)
            holder.checkBox.visibility = View.VISIBLE
            holder.deleteLabelButton.visibility = View.INVISIBLE
            holder.editLabelButton.isVisible = false
            Log.i("LabelAdapter", "from Db : $labelListFromDb")
            holder.checkBox.isChecked = false
            labelListFromDb.forEach { label ->
                if(label.labelFid == currentItem.labelFid) {
                    holder.checkBox.isChecked = true
                }
            }
            holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if(buttonView.isChecked) {
                    currentItem.isChecked = true
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return labelList.size
    }

    class LabelViewHolder( itemView : View) : RecyclerView.ViewHolder(itemView) {
        val labelEditText: EditText = itemView.findViewById(R.id.label_item_name)
        val deleteLabelButton : AppCompatImageButton = itemView.findViewById(R.id.deleteButton)
        val editLabelButton : AppCompatImageButton = itemView.findViewById(R.id.editLabelButton)
        val checkBox : CheckBox = itemView.findViewById(R.id.labelCheckBox)
    }
}