package com.example.noteapp.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
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
    private fun formatDate(date : String) : String{
        val year  = date.subSequence(0,4)
        var month = date.subSequence(5,7)
        val day = date.subSequence(8,10)
        when(month){
            "01" -> month = "Jan"
            "02" -> month = "Feb"
            "03" -> month = "Mar"
            "04" -> month = "Apr"
            "05" -> month = "May"
            "06" -> month = "Jun"
            "07" -> month = "Jul"
            "08" -> month = "Aug"
            "09" -> month = "Sep"
            "10" -> month = "Oct"
            "11" -> month = "Nov"
            "12" -> month = "Dec"
        }
        val formatted = "$month,$day,$year"
        return formatted
    }
    inner class NoteViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        @SuppressLint("ResourceAsColor")
        fun bind(note: UserData){
            // check priority color
            if(note.priority.toString().equals("Critical")){
                itemView.setBackgroundColor(Color.parseColor("#000000"))
                itemView.constraint_item.setBackgroundResource(R.drawable.background_item)
                itemView.text_title.text = note.title
                itemView.text_date.text = formatDate(note.date.toString())

            }
            else if(note.priority.toString().equals("High")){
                itemView.setBackgroundColor(Color.parseColor("#000000"))
                itemView.constraint_item.setBackgroundResource(R.drawable.background_item_2)
                itemView.text_title.text = note.title
                itemView.text_date.text = formatDate(note.date.toString())
            }
            else{
                itemView.setBackgroundColor(Color.parseColor("#000000"))
                itemView.constraint_item.setBackgroundResource(R.drawable.background_item_3)
                itemView.text_title.text = note.title
                itemView.text_date.text = formatDate(note.date.toString())
            }

            //check if note has image
            if(note.url.toString() == "null" ){
                itemView.item_image.visibility = View.GONE
            }
            else {
                itemView.item_image.visibility = View.VISIBLE
            }

            itemView.setOnClickListener{
                onItemClick.invoke(note)
            }
        }
    }

}