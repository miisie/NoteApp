package com.example.noteapp.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.noteapp.MainActivity
import com.example.noteapp.R
import com.example.noteapp.SingleNoteData
import com.example.noteapp.UserData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.google.firebase.storage.ktx.component3
import com.google.firebase.storage.ktx.component4
import kotlinx.android.synthetic.main.fragment_add_note.view.*
import java.net.URI
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class AddNote : Fragment() {
    private val IMAGE_PICK_CODE = 1000
    private val PERMISSION_CODE = 1001

    private lateinit var database : DatabaseReference
    private lateinit var submitbtn : Button
    private lateinit var date : TextView
    private lateinit var title : EditText
    private lateinit var content : EditText
    private lateinit var backBtn: ImageView
    private lateinit var priority: Button
    private lateinit var imageBtn: ImageView
    private lateinit var imageNote: ImageView
    private lateinit var imageUri : Uri
    private lateinit var Url : String
    private var CHECK : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_add_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        checkEditOrNew()

        implement()

        setPrior()
    }

    private fun implement() {
        // submit button

        submitbtn.setOnClickListener {
            if(!title.text.isNullOrEmpty() && title.text.isNotBlank()){
                //uploadData
                checkNewSaveOrEditSave()

                //back to home
                fragmentManager?.beginTransaction()?.apply {
                    replace(R.id.frame_layout_id, HomeFragment())
                    addToBackStack(null)
                    commit()
                }
            }
            else {
                title.error = "This fill cannot be blank"
            }
        }

        // back button
        backBtn.setOnClickListener {
            backEditOrNew()
        }

        // add image
        imageBtn.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(this.context?.let { it1 ->
                        ContextCompat.checkSelfPermission(
                            it1,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    } == PackageManager.PERMISSION_DENIED) {
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions,PERMISSION_CODE)
                }
                else {
                    pickImageFromGallery()
                }
            }
            else {
                pickImageFromGallery()
            }
            /*imageNote.visibility = View.VISIBLE*/
        }


    }

    private fun init() {
        imageUri = Uri.EMPTY
        Url = "null"
        CHECK = false
        submitbtn = view?.findViewById(R.id.submitbtn)!!
        date = view?.findViewById(R.id.date)!!
        title = view?.findViewById(R.id.title)!!
        content = view?.findViewById(R.id.content)!!
        backBtn = view?.findViewById(R.id.back_button)!!
        priority = view?.findViewById(R.id.priority)!!
        imageBtn = view?.findViewById(R.id.add_image)!!
        imageNote = view?.findViewById(R.id.image_note)!!
        date.text = getCurrentDate()
        database = FirebaseDatabase
            .getInstance("https://noteapp-b945c-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Note")
        /*database = FirebaseDatabase
            .getInstance("https://note-app-d7239-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Note")*/
    }

    private fun checkEditOrNew() {
        val bundle : Bundle? = arguments
        if (bundle != null){
            CHECK = true
            title.text = Editable.Factory.getInstance().newEditable(bundle?.getString("titlE").toString())
            /*title.setText(bundle?.getString("titlE").toString())*/
            content.setText(bundle?.getString("contenT").toString())
            priority.text = bundle?.getString("prioritY")
            if(bundle?.getString("imagE").toString() != "null"){
                bundle?.getString("imagE","").let{
                    Glide.with(this)
                        .load(bundle?.getString("imagE","").toString())
                        .into(imageNote)
                }
            }
        }
    }

    private fun backEditOrNew(){
        if(CHECK == false){
            fragmentManager?.beginTransaction()?.apply {
                replace(R.id.frame_layout_id, HomeFragment())
                addToBackStack(null)
                commit()
            }
        }
        else{
            backNoteDetails()
        }
    }

    private fun backNoteDetails() {
        val bundle : Bundle? = arguments
        val intent = Intent(requireContext(), SingleNoteData::class.java)
        intent.putExtra("date", bundle?.getString("datE").toString())
        intent.putExtra("title", bundle?.getString("titlE").toString())
        intent.putExtra("content", bundle?.getString("titlE").toString())
        intent.putExtra("priority", bundle?.getString("prioritY").toString())
        intent.putExtra("image",bundle?.getString("imagE").toString())
        startActivity(intent)
    }

    private fun checkNewSaveOrEditSave(){
        if(CHECK == false){
            uploadImg(title.text.toString(),content.text.toString(),priority.text.toString(),date.text.toString())
        }
        else{
            editData(title.text.toString(),content.text.toString(),priority.text.toString(),date.text.toString())
        }
    }

    private fun editData(title: String, content :String, priority : String, date: String){
        val bundle : Bundle? = arguments
        if(title != bundle?.getString("titlE").toString()){
            database.child(bundle?.getString("titlE").toString()).removeValue()
        }
        if (imageUri == Uri.EMPTY && bundle?.getString("imagE").toString() != "null"){
            Url = bundle?.getString("imagE").toString()
        }
        uploadImg(title, content, priority, date)
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,IMAGE_PICK_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PERMISSION_CODE -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery()
                }
                else {
                    Toast.makeText(this.context,"Permission denied",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            imageUri = data?.data!!
            imageNote.setImageURI(imageUri)
        }
    }

    @SuppressLint("NewApi")
    private fun uploadImg(Title: String, Content :String, Priority : String, Date: String){
        val progressDialog = ProgressDialog(this.context)
        progressDialog.setMessage("Uploading..")
        progressDialog.setCancelable(false)
        progressDialog.show()

        if(imageUri == Uri.EMPTY){
            val userData = UserData(Date,Title,Content,Priority,Url)
            database.child(Title).setValue(userData).addOnSuccessListener {
                title.text.clear()
                content.text.clear()
                priority.text = "Level 1"
                if(progressDialog.isShowing) progressDialog.dismiss()

            }.addOnFailureListener {
                if(progressDialog.isShowing) progressDialog.dismiss()
            }
        }
        else{

            val formatter = SimpleDateFormat("YYYY_MM_dd_HH_mm_ss", Locale.getDefault())
            val now = Date()
            val filename = formatter.format(now)
            val storageReference = FirebaseStorage
                .getInstance("gs://noteapp-b945c.appspot.com")
                .getReference("image/$filename")
            storageReference.putFile(imageUri).addOnSuccessListener {
                imageNote.setImageURI(null)
                if(progressDialog.isShowing) progressDialog.dismiss()
                it.storage.downloadUrl.addOnSuccessListener {
                    val userData = UserData(Date,Title,Content,Priority,it.toString())
                    database.child(title.text.toString()).setValue(userData).addOnSuccessListener {
                        title.text.clear()
                        content.text.clear()
                        priority.text = "Level 1"

                    }.addOnFailureListener {

                    }
                }
            }.addOnFailureListener{
                if(progressDialog.isShowing) progressDialog.dismiss()
            }
        }
    }


    private fun getCurrentDate():String{
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(Date())
    }

    private fun setPrior(){
        val popupMenu = PopupMenu(requireContext(),priority)
        popupMenu.menu.add(Menu.NONE , 0, 0, "Level 1")
        popupMenu.menu.add(Menu.NONE , 1, 1, "Level 2")
        popupMenu.menu.add(Menu.NONE , 2, 2, "Level 3")
        popupMenu.setOnMenuItemClickListener {
            val id = it.itemId
            if(id == 0){
                priority.text = "Level 1"
            }
            else if (id == 1){
                priority.text = "Level 2"
            }

            else if (id == 2){
                priority.text = "Level 3"
            }
            false
        }
        priority.setOnClickListener {
            popupMenu.show()
        }
    }
}