package com.example.noteapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.noteapp.fragment.AddNote
import com.example.noteapp.fragment.HomeFragment


class SingleNoteData : AppCompatActivity() {
    private lateinit var title : TextView
    private lateinit var content: TextView
    private lateinit var priority: TextView
    private lateinit var backButton : ImageView
    private lateinit var Image : ImageView
    private lateinit var date : TextView
    private lateinit var editButton: ImageView
    private lateinit var extras : Bundle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_note_data)
        init()
        extra(extras)
        implement()
    }

    private fun init(){
        extras = intent.extras!!
        title = findViewById(R.id.title_data)
        content = findViewById(R.id.content_data)
        priority = findViewById(R.id.priority_data)
        backButton = findViewById(R.id.back_button)
        Image = findViewById(R.id.image_data)
        date = findViewById(R.id.date_data)
        editButton = findViewById(R.id.editbtn)
    }

    private fun extra(extras: Bundle){
        if (extras != null) {
            NoteDetails(extras)
        } else {
            finish()
        }
    }

    private fun NoteDetails(extras: Bundle){
        extras.getString("image","")?.let { image ->
            Glide.with(this)
                .load(extras.getString("image","").toString())
                .transform(CenterCrop())
                .into(Image)
        }
        date.text = extras.getString("date","")
        title.text = extras.getString("title","")
        content.text = extras.getString("content","")
        priority.text = extras.getString("priority","")
    }

    private fun editFragment(){
        startActivity(Intent(this,MainActivity::class.java)
            .putExtra("Title",title.text.toString())
            .putExtra("Content",content.text.toString())
            .putExtra("Priority",priority.text.toString())
            .putExtra("Image",Image.toString())
        )
    }

    private fun implement(){

        backButton.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }

        editButton.setOnClickListener{
            editFragment()
        }
    }

}