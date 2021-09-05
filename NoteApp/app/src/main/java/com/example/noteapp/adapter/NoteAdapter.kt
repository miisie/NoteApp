package com.example.noteapp.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.R
import com.example.noteapp.data.UserData
import kotlinx.android.synthetic.main.note_item.view.*

class NoteAdapter(private var noteList: ArrayList<UserData>,
                  private val onItemClick:(note : UserData) -> Unit
                  ): RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(noteList[position])
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    fun clear() {
        noteList.clear()
        notifyDataSetChanged()
    }
    inner class NoteViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        @SuppressLint("ResourceAsColor")
        fun bind(note: UserData){
            if(note.priority.toString().equals("Level 1")){
                itemView.setBackgroundColor(Color.parseColor("#000000"))
                itemView.constraint_item.setBackgroundResource(R.drawable.background_item)
                itemView.text_title.text = note.title
                itemView.text_date.text = note.date

            }
            else if(note.priority.toString().equals("Level 2")){
                itemView.setBackgroundColor(Color.parseColor("#000000"))
                itemView.constraint_item.setBackgroundResource(R.drawable.background_item_2)
                itemView.text_title.text = note.title
                itemView.text_date.text = note.date
            }
            else{
                itemView.setBackgroundColor(Color.parseColor("#000000"))
                itemView.constraint_item.setBackgroundResource(R.drawable.background_item_3)
                itemView.text_title.text = note.title
                itemView.text_date.text = note.date
            }
            itemView.setOnClickListener{
                onItemClick.invoke(note)
            }
        }
    }

}