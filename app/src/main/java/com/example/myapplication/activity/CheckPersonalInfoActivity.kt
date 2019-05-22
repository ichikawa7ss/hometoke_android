package com.example.myapplication.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.example.myapplication.Confirmation
import android.widget.CheckBox
import com.example.myapplication.R
import android.view.View
import kotlinx.android.synthetic.main.activity_check_personal_info.*


class CheckPersonalInfoActivity : AppCompatActivity() {

    var checkClick = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_personal_info)

        val checkbox = findViewById<View>(R.id.checkBox) as CheckBox
        checkbox.isChecked = false

        // set the listener upon the checkbox
        checkbox.setOnClickListener(View.OnClickListener
        {
            val check = checkbox.isChecked()
            if (check) {
                this.checkClick = true
            } else {
                this.checkClick = false
            }
        })

        ToConfirmButtonmoveToConfirmationBtn.setOnClickListener{
            moveToConfirmationView()
        }
    }

    fun moveToConfirmationView() {
        println("次へボタン押下")

        if (checkClick) {
            // 同意されていれば画面遷移
            val confirmationIntent = Intent(this, Confirmation::class.java)

            confirmationIntent.putExtra("checkInputBirthday", intent.getBooleanExtra("checkImputBirthday",true))

            startActivity(confirmationIntent)
        } else {
            // 同意がなければ
            AlertDialog.Builder(this).apply {
                setTitle("プライバシーポリシーを確認してください")
                setMessage("このアプリの利用にはプライバシーポリシーへの同意が必要です")
                setPositiveButton("OK", null)
                show()
            }
        }
    }

}

