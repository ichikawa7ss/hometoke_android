package com.example.myapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_regist_picture.*



class RegistPicture : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regist_picture)

        Picasso.with(this).load(R.drawable.noimage)
           .transform(CircleTransform()).into(profileImage)
    }
}
