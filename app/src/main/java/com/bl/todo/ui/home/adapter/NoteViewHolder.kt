package com.bl.todo.ui.home.adapter

import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bl.todo.R
import com.bl.todo.ui.wrapper.NoteInfo
import com.google.android.material.textview.MaterialTextView
import java.text.SimpleDateFormat

class NoteViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
    private val title: MaterialTextView = itemView.findViewById(R.id.cardTitle)
    private val content: MaterialTextView = itemView.findViewById(R.id.cardContent)
    private val reminderRecyclerLayout: RelativeLayout = itemView.findViewById(R.id.reminderRecyclerLayout)
    private val reminderTextView: TextView = itemView.findViewById(R.id.reminderTextView)

    init {
        itemView.setOnClickListener {
            listener.onItemClick(adapterPosition)
        }
    }

    fun bind(currentItem: NoteInfo) {
        title.text = currentItem.title
        if (currentItem.content.isEmpty()) {
            content.isVisible = false
        } else {
            content.isVisible = true
            content.text = currentItem.content
        }
        if(currentItem.reminder != null) {
            reminderRecyclerLayout.visibility = View.VISIBLE
            val formatter = SimpleDateFormat("dd MMM, hh:mm aa")
            val date = formatter.format(currentItem.reminder!!)
            reminderTextView.text = date
        } else {
            reminderRecyclerLayout.visibility = View.GONE
        }
    }
}