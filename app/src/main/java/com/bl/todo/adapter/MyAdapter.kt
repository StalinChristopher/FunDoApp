package com.bl.todo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bl.todo.R
import com.bl.todo.models.NewNote
import com.google.android.material.textview.MaterialTextView
import kotlin.collections.ArrayList

class MyAdapter( private val notesList : ArrayList<NewNote>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.single_list_item_layout,
            parent,false)
        return MyViewHolder((itemView))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem  = notesList[position]
        holder.title.text = currentItem.title
        holder.content.text = currentItem.content

    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val title : MaterialTextView = itemView.findViewById(R.id.cardTitle)
        val content : MaterialTextView = itemView.findViewById(R.id.cardContent)
    }
}