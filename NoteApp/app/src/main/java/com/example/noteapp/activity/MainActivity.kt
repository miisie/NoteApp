package com.example.noteapp.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.noteapp.PassData
import com.example.noteapp.R
import com.example.noteapp.fragment.AddNote
import com.example.noteapp.fragment.HomeFragment
import com.example.noteapp.fragment.NoteDetail
import com.example.noteapp.fragment.WaitingScreen


class MainActivity : AppCompatActivity(), PassData {
    private lateinit var title: String
    private lateinit var content: String
    private lateinit var priority: String
    private lateinit var image: String
    private lateinit var date: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        /*bundle()*/
        addFragmentToActivity(supportFragmentManager, WaitingScreen(), R.id.frame_layout_id)
    }

   /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 898){
            if(resultCode == RESULT_OK){
                title = intent.getStringExtra("Title").toString()
                content = intent.getStringExtra("Content").toString()
                priority = intent.getStringExtra("Priority").toString()
                image = intent.getStringExtra("Image").toString()
                date = intent.getStringExtra("Date").toString()
                packageData()
            }
        }
    }*/
   /* private fun bundle(){
        if(intent.getStringExtra("Title").toString() == "null"){
            addFragmentToActivity(supportFragmentManager, WaitingScreen(), R.id.frame_layout_id)
        }
        else{
            title = intent.getStringExtra("Title").toString()
            content = intent.getStringExtra("Content").toString()
            priority = intent.getStringExtra("Priority").toString()
            image = intent.getStringExtra("Image").toString()
            date = intent.getStringExtra("Date").toString()
            packageData()
        }
    }*/

    /*private fun packageData(){
        val addNote = AddNote()
        val data = Bundle().apply {
            putString("titlE",title)
            putString("contenT",content)
            putString("prioritY",priority)
            putString("datE",date)
            putString("imagE",image)
        }
        addNote.arguments = data
        addFragmentToActivity(supportFragmentManager, addNote, R.id.frame_layout_id)
    }*/

    private fun addFragmentToActivity(manager: FragmentManager, fragment: Fragment?, frameId: Int) {
        val transaction: FragmentTransaction = manager.beginTransaction()
        if (fragment != null) {
            transaction.add(frameId, fragment)
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    //close keyboard
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun passFromHomeToNote(
        title: String,
        content: String,
        priority: String,
        date: String,
        image: String
    ) {
        val noteDetail = NoteDetail()
        val data = Bundle().apply {
            putString("title",title)
            putString("content",content)
            putString("priority",priority)
            putString("date",date)
            putString("image",image)
        }
        noteDetail.arguments = data
        addFragmentToActivity(supportFragmentManager, noteDetail, R.id.frame_layout_id)
    }

    override fun passFromNoteToEdit(
        title: String,
        content: String,
        priority: String,
        date: String,
        image: String
    ) {
        val addNote = AddNote()
        val data = Bundle().apply {
            putString("titlE",title)
            putString("contenT",content)
            putString("prioritY",priority)
            putString("datE",date)
            putString("imagE",image)
        }
        addNote.arguments = data
        addFragmentToActivity(supportFragmentManager, addNote, R.id.frame_layout_id)
    }
}