package com.example.noteapp.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.noteapp.Communicator.PassData
import com.example.noteapp.R
import com.example.noteapp.fragment.AddNote
import com.example.noteapp.fragment.FullImage
import com.example.noteapp.fragment.NoteDetail
import com.example.noteapp.fragment.WaitingScreen


class MainActivity : AppCompatActivity(), PassData {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        addFragmentToActivity(supportFragmentManager, WaitingScreen(), R.id.frame_layout_id)
    }

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
        image: String,
        webLink: String
    ) {
        val noteDetail = NoteDetail()
        val data = Bundle().apply {
            putString("title",title)
            putString("content",content)
            putString("priority",priority)
            putString("date",date)
            putString("image",image)
            putString("weblink",webLink)
        }
        noteDetail.arguments = data
        addFragmentToActivity(supportFragmentManager, noteDetail, R.id.frame_layout_id)
    }

    override fun passFromNoteToEdit(
        title: String,
        content: String,
        priority: String,
        date: String,
        image: String,
        webLink: String
    ) {
        val addNote = AddNote()
        val data = Bundle().apply {
            putString("titlE",title)
            putString("contenT",content)
            putString("prioritY",priority)
            putString("datE",date)
            putString("imagE",image)
            putString("weblinK",webLink)
        }
        addNote.arguments = data
        addFragmentToActivity(supportFragmentManager, addNote, R.id.frame_layout_id)
    }

    override fun passFromNoteToImage(image: String) {
        val imageNote = FullImage()
        val data = Bundle().apply {
            putString("IMAGE",image)
        }
        imageNote.arguments = data
        addFragmentToActivity(supportFragmentManager, imageNote, R.id.frame_layout_id)
    }
}