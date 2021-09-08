    package com.example.noteapp.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
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
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.noteapp.R
import com.example.noteapp.data.UserData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class AddNote : Fragment() {
    private val IMAGE_PICK_CODE = 1000
    private val PERMISSION_CODE = 1001
    private lateinit var database : DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var submitbtn : TextView
    private lateinit var date : TextView
    private lateinit var title : EditText
    private lateinit var content : EditText
    private lateinit var backBtn: ImageView
    private lateinit var priority: TextView
    private lateinit var imageBtn: ImageView
    private lateinit var imageNote: ImageView
    private lateinit var imageUri : Uri
    private lateinit var Url : String
    private lateinit var imgName: String
    private lateinit var savedImgName: String
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
                    commit()
                }
            }
            else {
                title.error = "This fill cannot be blank"
            }
        }

        // back button
        backBtn.setOnClickListener {
            activity?.onBackPressed()
        }

        // add image
        imageBtn.setOnClickListener {
            chooseUploadOrDelete()
        }


    }

    private fun init() {
        imageUri = Uri.EMPTY
        Url = "null"
        CHECK = false
        imgName = "null"
        savedImgName = "null"
        submitbtn = view?.findViewById(R.id.submitbtn)!!
        date = view?.findViewById(R.id.date)!!
        title = view?.findViewById(R.id.title)!!
        content = view?.findViewById(R.id.content)!!
        backBtn = view?.findViewById(R.id.back_button)!!
        priority = view?.findViewById(R.id.priority)!!
        imageBtn = view?.findViewById(R.id.add_image)!!
        imageNote = view?.findViewById(R.id.image_note)!!
        date.text = getCurrentDate()
        storageReference = FirebaseStorage
            .getInstance("gs://noteapp-b945c.appspot.com")
            .getReference("image")
        database = FirebaseDatabase
            .getInstance("https://noteapp-b945c-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Note")
        /*database = FirebaseDatabase
            .getInstance("https://note-app-d7239-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Note")*/
    }

    private fun chooseImage(){
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
    }

    private fun chooseUploadOrDelete(){
        val popupMenu = PopupMenu(context,imageBtn)
        popupMenu.inflate(R.menu.image_handle)
        val deleteButton = popupMenu.menu.findItem(R.id.Delete)
        if(imgName == "null" && imageUri == Uri.EMPTY){
            deleteButton.setVisible(false)
        }
        else{
            deleteButton.setVisible(true)
        }
        popupMenu.setOnMenuItemClickListener {
            if(it.title == "Upload"){
                chooseImage()
            }
            else{
                //edit
                if(CHECK == true){
                    //have image
                    if(imgName != "null"){
                        saveImgName(imgName)
                        imageNote.setImageResource(0)
                        imgName = "null"
                    }
                    //no image
                    else{
                        if(imageUri == Uri.EMPTY){
                            Toast.makeText(context,"No Image To Delete",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            imageUri = Uri.EMPTY
                            imageNote.setImageResource(0)
                        }
                    }
                }
                //new
                else{
                    if(imageUri == Uri.EMPTY){
                        Toast.makeText(context,"No Image To Delete",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        imageUri = Uri.EMPTY
                        imageNote.setImageResource(0)
                    }
                }
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
    private fun saveImgName(imgName : String){
        savedImgName  = imgName
    }

    private fun checkEditOrNew() {
        val bundle : Bundle? = arguments
        if (bundle != null){
            CHECK = true
            title.setText(bundle?.getString("titlE").toString())
            content.setText(bundle?.getString("contenT").toString())
            priority.text = bundle?.getString("prioritY")
            if(bundle?.getString("imagE").toString() != "null"){
                bundle?.getString("imagE","").let{
                    Glide.with(this)
                        .load(bundle?.getString("imagE").toString())
                        .into(imageNote)
                }
                getImageName(bundle.getString("imagE").toString())
            }
        }
    }

    private fun checkNewSaveOrEditSave(){
        if(CHECK == false){
            uploadImg(title.text.toString(),content.text.toString(),priority.text.toString(),date.text.toString())
        }
        else{
            editData(title.text.toString(),content.text.toString(),priority.text.toString(),date.text.toString(),savedImgName)
        }
    }

    private fun editData(title: String, content :String, priority : String, date: String, savedImgName :String){
        val bundle : Bundle? = arguments
        database.child(bundle?.getString("titlE").toString()).removeValue()
        if (imageUri == Uri.EMPTY && imgName != "null"){
            Url = bundle?.getString("imagE").toString()
        }
        if (savedImgName != "null"){
            storageReference.child(savedImgName).delete()
        }
        uploadImg(title, content, priority, date)
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        if (imgName != "null"){
            saveImgName(imgName)
        }
        imgName = "null"
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
                priority.text = "Low"
                if(progressDialog.isShowing) progressDialog.dismiss()

            }.addOnFailureListener {
                if(progressDialog.isShowing) progressDialog.dismiss()
            }
        }
        else{

            val formatter = SimpleDateFormat("YYYY_MM_dd_HH_mm_ss", Locale.getDefault())
            val now = Date()
            val filename = formatter.format(now)
            storageReference.child( "$filename").putFile(imageUri).addOnSuccessListener {
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
        val sdf = SimpleDateFormat("yyyy/MM/dd")
        Log.d("date",sdf.format(Date()).toString())
        return sdf.format(Date())
    }

    private fun setPrior(){
        val popupMenu = PopupMenu(requireContext(),priority)
        popupMenu.menu.add(Menu.NONE , 0, 0, "Critical")
        popupMenu.menu.add(Menu.NONE , 1, 1, "High")
        popupMenu.menu.add(Menu.NONE , 2, 2, "Normal")
        popupMenu.setOnMenuItemClickListener {
            val id = it.itemId
            if(id == 0){
                priority.text = "Critical"
                priority.setTextColor(Color.parseColor("#FFC0CB"))
            }
            else if (id == 1){
                priority.text = "High"
                priority.setTextColor(Color.parseColor("#FFD580"))
            }

            else if (id == 2){
                priority.text = "Normal"
                priority.setTextColor(Color.parseColor("#90ee90"))
            }
            false
        }
        priority.setOnClickListener {
            popupMenu.show()
        }
    }
    private fun getImageName(imgUrl : String){
        imgName = imgUrl.substringAfter('F',imgUrl).subSequence(0,19).toString()
    }
}