package com.example.noteapp.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.ScrollView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.noteapp.Communicator.PassData
import com.example.noteapp.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.lang.Exception

class NoteDetail : Fragment() {
    private lateinit var title : TextView
    private lateinit var content: TextView
    private lateinit var priority: TextView
    private lateinit var backButton : ImageView
    private lateinit var Image : ImageView
    private lateinit var date : TextView
    private lateinit var editButton: ImageView
    private lateinit var communicator: PassData
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var imgName: String
    private var checkIfHasImage : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        noteDetail()

        implement()
    }
    private fun init(){
        checkIfHasImage = false
        communicator = activity as PassData
        title = view?.findViewById(R.id.title_data)!!
        content = view?.findViewById(R.id.content_data)!!
        priority = view?.findViewById(R.id.priority_data)!!
        backButton = view?.findViewById(R.id.back_button)!!
        Image = view?.findViewById(R.id.image_data)!!
        date = view?.findViewById(R.id.date_data)!!
        editButton = view?.findViewById(R.id.editbtn)!!
        database = FirebaseDatabase
            .getInstance("https://noteapp-b945c-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Note")
        storageReference = FirebaseStorage
            .getInstance("gs://noteapp-b945c.appspot.com")
            .getReference("image")
        imgName = "null"
    }

    private fun noteDetail(){
        title.text = arguments?.getString("title").toString()
        content.text = arguments?.getString("content").toString()
        priority.text = arguments?.getString("priority").toString()
        date.text = arguments?.getString("date").toString()
        if (arguments?.getString("image").toString() != "null"){
            checkIfHasImage = true
            arguments?.getString("image")?.let{
                Glide.with(this)
                    .load(arguments?.getString("image").toString())
                    .transform(CenterCrop())
                    .into(Image)
            }
           /* Image.setOnClickListener {
                Log.d("check2","check2")
                communicator.passFromNoteToImage(arguments?.getString("image").toString())
            }*/
            getImageName(arguments?.getString("image").toString())
        }
        if(priority.text.toString() == "Critical"){
            priority.setTextColor(Color.parseColor("#FFC0CB"))
        }
        else if(priority.text.toString() == "High"){
            priority.setTextColor(Color.parseColor("#FFD580"))
        }
        else{
            priority.setTextColor(Color.parseColor("#90ee90"))
        }
    }

    private fun getImageName(imgUrl : String){
        imgName = imgUrl.substringAfter('F',imgUrl).subSequence(0,19).toString()
    }

    private fun implement(){
        backButton.setOnClickListener{
            activity?.onBackPressed()
        }

        if(checkIfHasImage){
            Image.setOnClickListener {
                communicator.passFromNoteToImage(arguments?.getString("image").toString())
            }
        }

        editButton.setOnClickListener{
            popUpEditDelete()
        }
    }

    private fun popUpEditDelete(){
        val popupMenu = PopupMenu(activity,editButton)
        popupMenu.inflate(R.menu.pop_up_3dot)
        popupMenu.setOnMenuItemClickListener {
            if(it.title == "Edit"){
                communicator.passFromNoteToEdit(title.text.toString(),content.text.toString(),priority.text.toString(),date.text.toString(),arguments?.getString("image").toString())
            }
            else{
                deleteData(title.text.toString(),imgName)
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(R.id.frame_layout_id,HomeFragment())
                transaction?.commit()
            }
            true
        }
        try {
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(popupMenu)
            mPopup.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java).invoke(mPopup,true)
        }catch(e: Exception){
            Log.e("Main","Error showing menu icon",e)
        }
        finally{
            popupMenu.show()
        }
    }
    private fun deleteData(title: String, imgName: String){
        database.child(title).removeValue()
        Log.d("check",imgName)
        if(imgName != "null"){
            storageReference.child(imgName).delete()
        }
    }
}