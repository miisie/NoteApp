package com.example.noteapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.noteapp.fragment.AddNote
import com.example.noteapp.fragment.HomeFragment


class MainActivity : AppCompatActivity(){
    private lateinit var title: String
    private lateinit var content: String
    private lateinit var priority: String
    private lateinit var image: String
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
            packageData()
        }
    }

    private fun packageData(){
        val data = Bundle().apply {
            Log.d("title",intent.getStringExtra("Title").toString())
            Log.d("content",intent.getStringExtra("Content").toString())
            Log.d("priority",intent.getStringExtra("Priority").toString())
            putString("titlE",title)
            putString("contenT",content)
            putString("prioritY",priority)
            putString("imagE",image)
        }
        AddNote().arguments = data
        addFragmentToActivity(supportFragmentManager, AddNote(), R.id.frame_layout_id)
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