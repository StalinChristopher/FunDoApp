package com.bl.todo.ui.homePage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bl.todo.R
import com.bl.todo.ui.wrapper.NoteInfo
import com.google.android.material.textview.MaterialTextView
import kotlin.collections.ArrayList

class MyAdapter(private val notesList: ArrayList<NoteInfo>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {

        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.single_list_item_layout,
            parent, false
        )
        return MyViewHolder((itemView), mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = notesList[position]
        holder.title.text = currentItem.title
        holder.content.text = currentItem.content

    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    class MyViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        val title: MaterialTextView = itemView.findViewById(R.id.cardTitle)
        val content: MaterialTextView = itemView.findViewById(R.id.cardContent)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}