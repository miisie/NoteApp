package com.example.noteapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.noteapp.R
import com.example.noteapp.fragment.AddNote
import com.example.noteapp.fragment.HomeFragment


class MainActivity : AppCompatActivity(){
    private lateinit var title: String
    private lateinit var content: String
    private lateinit var priority: String
    private lateinit var image: String
    private lateinit var date: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        bundle()

    }

    private fun bundle(){

        if(intent.getStringExtra("Title").toString() == "null"){
            addFragmentToActivity(supportFragmentManager, HomeFragment(), R.id.frame_layout_id)
        }
        else{
            title = intent.getStringExtra("Title").toString()
            content = intent.getStringExtra("Content").toString()
            priority = intent.getStringExtra("Priority").toString()
            image = intent.getStringExtra("Image").toString()
            date = intent.getStringExtra("Date").toString()
            packageData()
        }
    }

    private fun packageData(){
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

    private fun addFragmentToActivity(manager: FragmentManager, fragment: Fragment?, frameId: Int) {
        val transaction: FragmentTransaction = manager.beginTransaction()
        if (fragment != null) {
            transaction.add(frameId, fragment)
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }


}