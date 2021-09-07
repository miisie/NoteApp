package com.example.noteapp.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.noteapp.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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
    private lateinit var storageReference: StorageReference
    private lateinit var imgName: String
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
        database = FirebaseDatabase
            .getInstance("https://noteapp-b945c-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Note")
        storageReference = FirebaseStorage
            .getInstance("gs://noteapp-b945c.appspot.com")
            .getReference("image")
        imgName = "null"
    }

    private fun extra(extras: Bundle){
        if (extras != null) {
            NoteDetails(extras)
        } else {
            finish()
        }
    }

    private fun NoteDetails(extras: Bundle){
        if(extras.getString("image","").toString() != "null"){
            extras.getString("image","")?.let { image ->
                Glide.with(this)
                    .load(extras.getString("image","").toString())
                    .transform(CenterCrop())
                    .into(Image)
            }
            getImageName(extras.getString("image",""))

        }
        date.text = extras.getString("date","")
        title.text = extras.getString("title","")
        content.text = extras.getString("content","")
        priority.text = extras.getString("priority","")
        if(priority.text.toString() == "Level 1"){
            priority.setTextColor(Color.parseColor("#FFC0CB"))
        }
        else if(priority.text.toString() == "Level 2"){
            priority.setTextColor(Color.parseColor("#FFD580"))
        }
        else{
            priority.setTextColor(Color.parseColor("#CBC3E3"))
        }
    }

    private fun editFragment(){
        startActivity(Intent(this, MainActivity::class.java)
            .putExtra("Title",title.text.toString())
            .putExtra("Content",content.text.toString())
            .putExtra("Priority",priority.text.toString())
            .putExtra("Date",date.text.toString())
            .putExtra("Image",extras.getString("image","").toString())
        )
    }
    private fun deleteData(title: String, imgName: String){
        database.child(title).removeValue()
        Log.d("check",imgName)
        if(imgName != "null"){
            storageReference.child(imgName).delete()
        }
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun popUpEditDelete(){
        val popupMenu = PopupMenu(this,editButton)
        popupMenu.inflate(R.menu.pop_up_3dot)
        popupMenu.setOnMenuItemClickListener {
            if(it.title == "Edit"){
                editFragment()
            }
            else{
                deleteData(title.text.toString(),imgName)
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

    private fun getImageName(imgUrl : String){
        imgName = imgUrl.substringAfter('F',imgUrl).subSequence(0,19).toString()
    }

    private fun implement(){

        backButton.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java)
                .putExtra("initState", false))
        }

        editButton.setOnClickListener{
            popUpEditDelete()
        }
    }
    //close keyboard
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

}