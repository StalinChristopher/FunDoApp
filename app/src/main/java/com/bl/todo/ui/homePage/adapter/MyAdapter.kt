package com.bl.todo.ui.homePage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bl.todo.R
import com.bl.todo.ui.wrapper.NoteInfo
import com.google.android.material.textview.MaterialTextView
import kotlin.collections.ArrayList

class MyAdapter(private val notesList: ArrayList<NoteInfo>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() , Filterable {

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

    }

    override fun getItemCount(): Int {
        return adapterNotesList.size
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
