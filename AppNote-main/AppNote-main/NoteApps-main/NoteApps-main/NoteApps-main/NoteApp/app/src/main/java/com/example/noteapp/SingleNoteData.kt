package com.example.noteapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.noteapp.fragment.AddNote
import com.example.noteapp.fragment.HomeFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.lang.Exception


class SingleNoteData : AppCompatActivity() {
    private lateinit var title : TextView
    private lateinit var content: TextView
    private lateinit var priority: TextView
    private lateinit var backButton : ImageView
    private lateinit var Image : ImageView
    private lateinit var date : TextView
    private lateinit var editButton: ImageView
    private lateinit var extras : Bundle
    private lateinit var database: DatabaseReference
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
            /*.putExtra("Image",Image.toString())*/
        )
    }
    private fun deleteData(title: String){
        database = FirebaseDatabase
            .getInstance("https://noteapp-b945c-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Note").child(title)
        database.removeValue()
        startActivity(Intent(this,MainActivity::class.java))
    }

    private fun popUpEditDelete(){
        val popupMenu = PopupMenu(this,editButton)
        popupMenu.inflate(R.menu.pop_up_3dot)
        popupMenu.setOnMenuItemClickListener {
            if(it.title == "Edit"){
                editFragment()
            }
            else{
                deleteData(title.text.toString())
            }
            true
        }
        try {
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(popupMenu)
            mPopup.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java).invoke(mPopup,true)
        }catch(e:Exception){
            Log.e("Main","Error showing menu icon",e)
        }
        finally{
            popupMenu.show()
        }
    }

    private fun implement(){

        backButton.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }

        editButton.setOnClickListener{
            popUpEditDelete()
        }
    }

}