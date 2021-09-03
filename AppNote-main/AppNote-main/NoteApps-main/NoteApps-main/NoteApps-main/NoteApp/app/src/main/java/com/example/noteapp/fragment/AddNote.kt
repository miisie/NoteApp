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
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.noteapp.R
import com.example.noteapp.UserData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.google.firebase.storage.ktx.component3
import com.google.firebase.storage.ktx.component4
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
    private val Url : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        check()

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

        implement()

        setPrior()
    }

    private fun implement() {
        // submit button

        submitbtn.setOnClickListener {
            //uplaod image
            uploadImg()

            //back to home
            fragmentManager?.beginTransaction()?.apply {
                replace(R.id.frame_layout_id, HomeFragment())
                addToBackStack(null)
                commit()
            }
        }

        // back button
        backBtn.setOnClickListener {
            fragmentManager?.beginTransaction()?.apply {
                replace(R.id.frame_layout_id, HomeFragment())
                addToBackStack(null)
                commit()
            }
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
            imageNote.visibility = View.VISIBLE
        }


    }

    private fun init() {
        imageUri = Uri.EMPTY
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

    private fun check(){
        val bundle : String = arguments?.getString("prioritY").toString()
        Log.d("check",bundle)
        if (bundle != "null"){
            Log.d("check2",bundle)
        }
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
            Log.d("picture","${data?.data}")
            imageUri = data?.data!!
            imageNote.setImageURI(imageUri)
        }
    }

    @SuppressLint("NewApi")
    private fun uploadImg(){
        val progressDialog = ProgressDialog(this.context)
        progressDialog.setMessage("Uploading..")
        progressDialog.setCancelable(false)
        progressDialog.show()

        if(imageUri == Uri.EMPTY){
            val userData = UserData(date.text.toString(),title.text.toString(),content.text.toString(),priority.text.toString(),Url)
            database.child(title.text.toString()).setValue(userData).addOnSuccessListener {
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
                    val userData = UserData(date.text.toString(),title.text.toString(),content.text.toString(),priority.text.toString(),it.toString())
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