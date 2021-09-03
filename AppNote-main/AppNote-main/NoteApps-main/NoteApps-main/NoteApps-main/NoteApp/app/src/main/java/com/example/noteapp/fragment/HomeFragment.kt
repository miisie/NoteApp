package com.example.noteapp.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteapp.NoteAdapter
import com.example.noteapp.R
import com.example.noteapp.SingleNoteData
import com.example.noteapp.UserData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private lateinit var sortingBtn: ImageView
    private lateinit var searchView: SearchView
    private lateinit var text: TextView
    private lateinit var addNoteBtn: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: StaggeredGridLayoutManager
    private lateinit var database: DatabaseReference
    private lateinit var noteList: ArrayList<UserData>
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var TempArr: ArrayList<UserData>

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
                    for (userSnapshot in snapshot.children) {
                        val note = userSnapshot.getValue(UserData::class.java)
                        noteList.add(note!!)
                    }
                    val sortedList = noteList.sortedWith(compareBy({ it.priority }, { it.title }))
                    noteList.clear()
                    noteAdapter.clear()
                    noteList.addAll(sortedList)
                    TempArr.addAll(noteList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun sortingTime() {
        val sortedList = noteList.sortedWith(compareBy { it.date })
        noteList.clear()
        noteAdapter.clear()
        noteList.addAll(sortedList)
        TempArr.addAll(noteList)
    }

    private fun sortingPriority() {
        val sortedList = noteList.sortedWith(compareBy({it.priority},{it.title}))
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

    /*@SuppressLint("ServiceCast")
    private fun closeSoftKeyboard(context: Context, v: View) {
        val iMm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        iMm.hideSoftInputFromWindow(v.windowToken, 0)
        v.clearFocus()
    }*/

    private fun showNoteDetails(note: UserData) {
        val intent = Intent(requireContext(), SingleNoteData::class.java)
        intent.putExtra("date", note.date)
        intent.putExtra("title", note.title)
        intent.putExtra("content", note.content)
        intent.putExtra("priority", note.priority)
        intent.putExtra("image",note.url)
        startActivity(intent)
    }
}