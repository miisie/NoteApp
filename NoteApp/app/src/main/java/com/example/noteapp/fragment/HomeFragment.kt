package com.example.noteapp.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteapp.Communicator.PassData
import com.example.noteapp.adapter.NoteAdapter
import com.example.noteapp.R
import com.example.noteapp.data.UserData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private lateinit var sortingBtn: Button
    private lateinit var searchView: SearchView
    private lateinit var text: TextView
    private lateinit var addNoteBtn: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: StaggeredGridLayoutManager
    private lateinit var database: DatabaseReference
    private lateinit var noteList: ArrayList<UserData>
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var TempArr: ArrayList<UserData>
    private lateinit var communicator: PassData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        implement()

        getNoteData()

    }

    private fun init() {

        communicator = activity as PassData

        sortingBtn = view?.findViewById(R.id.sorting)!!

        searchView = view?.findViewById(R.id.search_view)!!

        text = view?.findViewById(R.id.text1)!!

        addNoteBtn = view?.findViewById(R.id.BtnCreateNote)!!

        recyclerView = view?.findViewById(R.id.note_recycler)!!
        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        noteList = arrayListOf()
        TempArr = arrayListOf()
        noteAdapter = NoteAdapter(TempArr) { note -> showNoteDetails(note) }
        recyclerView.adapter = noteAdapter
    }

    private fun implement() {
        // Sorting button
        sortingBtn.setOnClickListener {
            val popup = PopupMenu(this.context, sortingBtn)
            popup.inflate(R.menu.pop_up_menu)
            popup.setOnMenuItemClickListener {
                if(it.title == "Time") {
                    sortingTime()
                }
                else if(it.title == "Priority") {
                    sortingPriority()
                }
                else {
                    sortingTitle()
                }
                true
            }
            popup.show()
        }

        // Add note button
        addNoteBtn.setOnClickListener {
            fragmentManager?.beginTransaction()?.apply {
                replace(R.id.frame_layout_id, AddNote())
                addToBackStack("Add Note")
                commit()
            }
        }

        // Search View
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                TempArr.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if(searchText.isNotEmpty()) {
                    noteList.forEach {

                        if(it.title?.toLowerCase(Locale.getDefault())!!.contains(searchText)) {

                            TempArr.add(it)
                        }
                    }
                    recyclerView.adapter?.notifyDataSetChanged()
                }
                else {

                    TempArr.clear()
                    TempArr.addAll(noteList)
                    recyclerView.adapter?.notifyDataSetChanged()
                }
                return true
            }

        })
    }

    private fun getNoteData() {
        database = FirebaseDatabase
            .getInstance("https://noteapp-b945c-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Note")
        /*database =
             FirebaseDatabase.getInstance("https://note-app-d7239-default-rtdb.asia-southeast1.firebasedatabase.app")
                 .getReference("Note")*/
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    TempArr.clear()
                    noteList.clear()
                    for (userSnapshot in snapshot.children) {
                        val note = userSnapshot.getValue(UserData::class.java)
                        noteList.add(note!!)
                    }
                    val sortedList = noteList.sortedWith(compareBy({ it.priority }, { it.date }))
                    noteList.clear()
                    TempArr.clear()
                    noteAdapter.clear()
                    noteList.addAll(sortedList)
                    TempArr.addAll(sortedList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun sortingTime() {
        val sortedList = noteList.sortedWith(compareBy { it.date }).reversed()
        noteList.clear()
        noteAdapter.clear()
        noteList.addAll(sortedList)
        TempArr.addAll(noteList)
    }

    private fun sortingPriority() {
        val sortedList = noteList.sortedWith(compareBy({it.priority},{it.date}))
        noteList.clear()
        noteAdapter.clear()
        noteList.addAll(sortedList)
        TempArr.addAll(noteList)
    }

    private fun sortingTitle() {
        var sortedList = noteList.sortedWith(compareBy { it.title })
        noteList.clear()
        noteAdapter.clear()
        noteList.addAll(sortedList)
        TempArr.addAll(noteList)
    }

    private fun showNoteDetails(note: UserData) {
       communicator.passFromHomeToNote(note.title.toString(),note.content.toString(),note.priority.toString(),note.date.toString(),note.url.toString())
    }
}