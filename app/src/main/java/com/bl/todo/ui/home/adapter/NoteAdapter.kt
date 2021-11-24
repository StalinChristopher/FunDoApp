package com.bl.todo.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bl.todo.R
import com.bl.todo.ui.wrapper.NoteInfo
import com.google.android.material.textview.MaterialTextView
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class NoteAdapter(private val notesList: ArrayList<NoteInfo>) :
    RecyclerView.Adapter<NoteAdapter.MyViewHolder>() , Filterable {

    private lateinit var mListener: OnItemClickListener
    private var adapterNotesList : ArrayList<NoteInfo> = ArrayList()
    init {
        adapterNotesList = notesList
    }

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
        val currentItem = adapterNotesList[position]
        holder.title.text = currentItem.title
        holder.content.text = currentItem.content
        if(currentItem.reminder != null) {
            holder.reminderRecyclerLayout.visibility = View.VISIBLE
            val formatter = SimpleDateFormat("dd MMM, hh:mm aa")
            val date = formatter.format(currentItem.reminder)
            holder.reminderTextView.text = date
        } else {
            holder.reminderRecyclerLayout.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return adapterNotesList.size
    }

    class MyViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        val title: MaterialTextView = itemView.findViewById(R.id.cardTitle)
        val content: MaterialTextView = itemView.findViewById(R.id.cardContent)
        val reminderRecyclerLayout : RelativeLayout = itemView.findViewById(R.id.reminderRecyclerLayout)
        val reminderTextView : TextView = itemView.findViewById(R.id.reminderTextView)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                adapterNotesList = if(p0.toString().isEmpty()){
                    notesList
                } else {
                    val filteredList = ArrayList<NoteInfo>()
                    adapterNotesList.forEach {
                        if( it.title.contains(p0.toString(),true)
                            || it.content.contains(p0.toString(),true)){
                            filteredList.add(it)
                        }
                    }
                    filteredList
                }
                var filerResults = FilterResults()
                filerResults.values = adapterNotesList
                return filerResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                adapterNotesList = p1?.values as ArrayList<NoteInfo>
                notifyDataSetChanged()
            }
        }
    }
}
