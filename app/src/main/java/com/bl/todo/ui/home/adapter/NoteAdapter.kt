package com.bl.todo.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bl.todo.R
import com.bl.todo.ui.wrapper.NoteInfo
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class NoteAdapter(private val notesList: ArrayList<NoteInfo>) :
    RecyclerView.Adapter<NoteViewHolder>() , Filterable {

    private lateinit var mListener: OnItemClickListener
    private var adapterNotesList : ArrayList<NoteInfo> = ArrayList()
    init {
        adapterNotesList = notesList
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.single_list_item_layout,
            parent, false
        )
        return NoteViewHolder(itemView,mListener)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentItem = adapterNotesList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return adapterNotesList.size
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
