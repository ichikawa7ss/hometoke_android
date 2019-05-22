package com.example.myapplication.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.Confirmation
import com.example.myapplication.R

class CheckPersonalInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_personal_info)
    }

    fun moveToConfirmationView() {
        println("次へボタン押下")

        // 次へボタンが押下された場合
        val confirmationIntent = Intent(this, Confirmation::class.java)

        confirmationIntent.putExtra(
            "checkInputBirthday",
            intent.getBooleanExtra("checkImputBirthday",true)
        )

        startActivity(confirmationIntent)
    }

}

