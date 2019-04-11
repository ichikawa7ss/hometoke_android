package com.example.myapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_start_page_test.*
import android.content.Intent

class startPageTest : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_page_test)

        moveToSignUpViewBtn.setOnClickListener {
            val intent = Intent(this, SignUpView::class.java)
            startActivity(intent)
        }
    }



}
