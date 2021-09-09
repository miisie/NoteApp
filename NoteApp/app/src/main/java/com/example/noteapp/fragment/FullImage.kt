package com.example.noteapp.fragment

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.noteapp.Communicator.PassData
import com.example.noteapp.R
import org.xmlpull.v1.XmlPullParser
import java.net.HttpURLConnection
import java.net.URL

class FullImage : Fragment() {
    private lateinit var back: ImageView
    private lateinit var image: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_full_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        image = view.findViewById(R.id.full_image)
        back = view.findViewById(R.id.back_button)
        back.setOnClickListener {
            activity?.onBackPressed()
        }
        Glide.with(this)
            .load(arguments?.getString("IMAGE").toString())
            .into(image)
    }

}