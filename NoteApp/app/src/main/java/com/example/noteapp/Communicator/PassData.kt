package com.example.noteapp.Communicator

interface PassData {
    fun passFromHomeToNote(title:String,content:String,priority:String,date:String,image:String)
    fun passFromNoteToEdit(title:String,content:String,priority:String,date:String,image:String)
    fun passFromNoteToImage(image:String)
}