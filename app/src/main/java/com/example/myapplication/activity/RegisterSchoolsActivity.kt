package com.example.myapplication.activity

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.example.myapplication.Confirmation
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_register_schools.*

/* 学校情報登録画面のActivity*/
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class RegisterSchoolsActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val EXTRA_SCHOOL_TYPE = "com.example.hometoke.SCHOOL_TYPE"
        var schoolName: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_schools)

        // 小学校名入力フィールド
        val inputElementarySch: EditText = findViewById(R.id.elementary_sch)
        inputElementarySch.setOnClickListener(this)

        // 中学校名入力フィールド
        val inputJuniorHighSch: EditText = findViewById(R.id.junior_highsch)
        inputJuniorHighSch.setOnClickListener(this)

        // 高等学校名入力フィールド
        val inputHighSch: EditText = findViewById(R.id.highsch)
        inputHighSch.setOnClickListener(this)

        moveToConfirmationViewBtn.setOnClickListener {
            moveToCheckPersonalInfoView()
        }
    }

    // クリックイベント
    override fun onClick(v: View?) {
        if(v != null) {
            when(v.id) {
                R.id.elementary_sch -> {
                    // 小学校のテキストフィールドが選択された場合
                    val intent = Intent(this, SearchSchoolActivity::class.java)
                    // 小中高の区別にRequestCodeを使用
                    val requestCode = 1000
                    intent.putExtra(EXTRA_SCHOOL_TYPE, requestCode)
                    startActivityForResult(intent, requestCode)
                }

                R.id.junior_highsch -> {
                    // 中学校のテキストフィールドが選択された場合
                    val intent = Intent(this, SearchSchoolActivity::class.java)
                    // 小中高の区別にRequestCodeを使用
                    val requestCode = 1001
                    intent.putExtra(EXTRA_SCHOOL_TYPE, requestCode)
                    startActivityForResult(intent, requestCode)
                }

                R.id.highsch -> {
                    // 高等学校のテキストフィールドが選択された場合
                    val intent = Intent(this, SearchSchoolActivity::class.java)
                    // 小中高の区別にRequestCodeを使用
                    val requestCode = 1002
                    intent.putExtra(EXTRA_SCHOOL_TYPE, requestCode)

                    startActivityForResult(intent, requestCode)
                }
            }
        }
    }

    // SearchSchoolActivityからの結果を受け取る
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (resultCode == Activity.RESULT_OK && intent != null && intent.extras != null) {
            // 学校名をSearchSchoolActivityから取得
            schoolName = intent.extras.getString(
                EXTRA_SCHOOL_TYPE
            )
            // リクエストコードによって学校名を表示するテキストフィールドを指定
            when(requestCode) {
                1000 -> {
                    // 小学校
                    val inputElementarySch: EditText = findViewById(R.id.elementary_sch)
                    inputElementarySch.setText(schoolName)
                }
                1001 -> {
                    // 中学校
                    val inputJuniorHighSch: EditText = findViewById(R.id.junior_highsch)
                    inputJuniorHighSch.setText(schoolName)
                }

                1002 -> {
                    // 高等学校
                    val inputHighSch: EditText = findViewById(R.id.highsch)
                    inputHighSch.setText(schoolName)
                }
            }

        }
    }

    private fun moveToCheckPersonalInfoView() {

        // 次へボタンが押下された場合
        val checkPersonalInfoIntent = Intent(this, CheckPersonalInfoActivity::class.java)

        // 生年月日の入力チェックをintentで渡す
        checkPersonalInfoIntent.putExtra("checkInputBirthday", intent.getBooleanExtra("checkImputBirthday",true))

        // 画像のパスをintentで渡す
        checkPersonalInfoIntent.putExtra("filepath",intent.getStringExtra("filepath"))

        startActivity(checkPersonalInfoIntent)
    }
}
