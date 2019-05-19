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

        MoveToConfirmationBtn.setOnClickListener {
            val intent = Intent(this, Confirmation::class.java)
            intent.putExtra("userName", "サンプル御名前")
            intent.putExtra("userSex", "男")
            intent.putExtra("userbirthYear", "2000")
            intent.putExtra("userbirthMonth", "4")
            intent.putExtra("userbirthDay", "2")
            intent.putExtra("elementalySchoolEntryYear", "2007")
            intent.putExtra("juniorHighSchoolEntryYear", "2013")
            intent.putExtra("highSchoolEntryYear", "2016")
            intent.putExtra("elementarySchool", "川崎市立井田小学校")
            intent.putExtra("juniorHighSchool", "川崎市立井田小中学校")
            intent.putExtra("highSchool", "神奈川県立多摩高等学校")
            intent.putExtra("mailAddress", "sample@gmail.com")
            intent.putExtra("password", "sample")

            startActivity(intent)
        }

        MoveToMainActivity.setOnClickListener() {
            val intent = Intent(this, ServeView::class.java)
            startActivity(intent)
        }

        MoveToRegistPicture.setOnClickListener(){
            val intent = Intent(this, SelectPicture::class.java)
            intent.putExtra("userName", "サンプル御名前")
            intent.putExtra("userSex", "男")
            intent.putExtra("userbirthYear", "2000")
            intent.putExtra("userbirthMonth", "4")
            intent.putExtra("userbirthDay", "2")
            intent.putExtra("elementalySchoolEntryYear", "2007")
            intent.putExtra("juniorHighSchoolEntryYear", "2013")
            intent.putExtra("highSchoolEntryYear", "2016")
            intent.putExtra("elementarySchool", "川崎市立井田小学校")
            intent.putExtra("juniorHighSchool", "川崎市立井田小中学校")
            intent.putExtra("highSchool", "神奈川県立多摩高等学校")
            intent.putExtra("mailAddress", "sample@gmail.com")
            intent.putExtra("password", "sample")
            startActivity(intent)
        }
    }



}
