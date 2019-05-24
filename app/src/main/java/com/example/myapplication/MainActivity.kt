package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private val PREFERENCES_FILE_NAME = "preference"

    /** Called when the activity is first created.  */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val check = loginCheck()
        if (check == false) { // SignUpView に遷移
            val intent = Intent(applicationContext, SignUpView::class.java)
            startActivity(intent)
        } else { // NDServeActivity に遷移
            val intent = Intent(applicationContext, NDServeActivity::class.java)
            startActivity(intent)
        }
    }

    // ログイン判定
    private fun loginCheck(): Boolean? {
        val settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0) ?: return false // 0 -> MODE_PRIVATE
        val login = settings.getLong("loginFlg", 0).toInt()
        return login == 1
    }
}