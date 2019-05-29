package com.example.myapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_ifcheck_view.*

class IFcheckView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ifcheck_view)

        // テータの登録
        userName.text = intent.getStringExtra("userName")
        userSex.text = intent.getStringExtra("userSex")
        userBirthYear.text = intent.getStringExtra("userBirthYear")
        userBirthMonth.text = intent.getStringExtra("userBirthMonth")
        userBirthDay.text = intent.getStringExtra("userBirthDay")
        // userEntryYear.text = intent.getStringExtra("userEntryYear")
        userMailAddress.text = intent.getStringExtra("userMailAddress")
        userPassword.text = intent.getStringExtra("userPassword")

    }
}
