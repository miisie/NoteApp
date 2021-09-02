package com.example.noteapp.fragment

import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import com.example.noteapp.R
import com.example.noteapp.UserData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class AddNote : Fragment() {

    private lateinit var database : DatabaseReference
    private lateinit var submitbtn : Button
    private lateinit var date : EditText
    private lateinit var title : EditText
    private lateinit var content : EditText
    private lateinit var backBtn: ImageView
    private lateinit var priority: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_add_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        submitbtn = view.findViewById(R.id.submitbtn)
        date = view.findViewById(R.id.date)
        title = view.findViewById(R.id.title)
        content = view.findViewById(R.id.content)
        backBtn = view.findViewById(R.id.back_button)
        priority = view.findViewById(R.id.priority)
        submitButton()
        backPress()
        setPrior()
    }

    private fun submitButton(){
        submitbtn.setOnClickListener {
            val Date =date.text.toString()
            val Title =title.text.toString()
            val Content =content.text.toString()
            val Priority = priority.text.toString()

            database = FirebaseDatabase
                .getInstance("https://noteapp-b945c-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Note")
            /*database = FirebaseDatabase.getInstance("https://note-app-d7239-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Note")*/
            val userData = UserData(Date,Title,Content,Priority)
            database.child(Title).setValue(userData).addOnSuccessListener {
                date.text.clear()
                title.text.clear()
                content.text.clear()
                priority.text = "Level 1"

            }.addOnFailureListener {

            }
            fragmentManager?.beginTransaction()?.apply {
                replace(R.id.frame_layout_id, HomeFragment())
                addToBackStack(null)
                commit()
            }


        }
    }

    private fun backPress() {
        backBtn.setOnClickListener {

            fragmentManager?.beginTransaction()?.apply {
            replace(R.id.frame_layout_id, HomeFragment())
            addToBackStack(null)
            commit()
        }
        }
    }

    private fun getCurrentDate():String{
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(Date())
    }

    private fun setPrior(){
        val popupMenu = PopupMenu(requireContext(),priority)
        popupMenu.menu.add(Menu.NONE , 0, 0, "Level 1")
        popupMenu.menu.add(Menu.NONE , 1, 1, "Level 2")
        popupMenu.menu.add(Menu.NONE , 2, 2, "Level 3")
        popupMenu.setOnMenuItemClickListener {
            val id = it.itemId
            if(id == 0){
                priority.text = "Level 1"
            }
            else if (id == 1){
                priority.text = "Level 2"
            }

            else if (id == 2){
                priority.text = "Level 3"
            }
            false
        }
        priority.setOnClickListener {
            popupMenu.show()
        }
    }
}