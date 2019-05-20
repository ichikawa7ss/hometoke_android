package com.example.myapplication

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_sign_up_view.*
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AlertDialog
import android.text.InputType

class SignUpView : AppCompatActivity() {

    // エラーステータス
    private var errStatus : Int = 0
    private var checkBirthday = true

    // 作業用
    private var year : Int = 2000
    private var month : Int = 4
    private var day : Int = 2

    // 性別、学年、生年月日
    private var userSex : String = "男"
    private var userEntryYear : Int = 0
    private var elementalySchoolEntryYear : String = ""
    private var juniorHighSchoolEntryYear : String = ""
    private var highSchoolEntryYear : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_view)

        val PREFERENCES_FILE_NAME = "preference"

        val settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0) // 0 -> MODE_PRIVATE
        val editor = settings.edit()
        editor.putLong("logged-in", 1).apply()

        // 入力値の型の指定
        birthYear.inputType = InputType.TYPE_CLASS_NUMBER
        birthMonth.inputType = InputType.TYPE_CLASS_NUMBER
        birthDay.inputType = InputType.TYPE_CLASS_NUMBER

        // 次へボタン押下時
        moveRegisterSchoolsViewBtn.setOnClickListener {
            moveRegisterSchoolsView()
        }

        // 男性ボタン押下時
        manBtn.setOnClickListener {
            setSexIsMan()
        }

        // 女性ボタン押下時
        womanBtn.setOnClickListener {
            setSexIsWoman()
        }

        // その他ボタン押下時
        otherBtn.setOnClickListener {
            setSexIsOther()
        }

    }

    // 生年月日の設定、学年の計算
    private fun setBirthDayAndGrade() {

        if (birthYear.text.isNotEmpty() && birthMonth.text.isNotEmpty() && birthDay.text.isNotEmpty()) {
            year = Integer.parseInt(birthYear.text.toString())
            month = Integer.parseInt(birthMonth.text.toString())
            day = Integer.parseInt(birthDay.text.toString())
            checkBirthday = true
        } else {
            checkBirthday = false
        }

        // 学年の計算
        //4月1日生まれの場合だけ特殊
        if (month == 4 && day == 1) this.userEntryYear = year + 6
        //早生まれ（1〜3月生まれ）
        else if (month <= 3) this.userEntryYear = year + 6
        //遅生まれ（4月2日〜12/31）
        else if (month in 4..12) this.userEntryYear = year + 7

        //各入学年の設定
        this.elementalySchoolEntryYear = this.userEntryYear.toString()
        this.juniorHighSchoolEntryYear = (this.userEntryYear + 6).toString()
        this.highSchoolEntryYear = (this.userEntryYear + 9).toString()

    }

    // 性別に男性のセット、ボタンUI切り替え
    private fun setSexIsMan(){
        this.userSex = "男"

        // ボタンUI切り替え
        manBtn.setImageResource(R.drawable.manon)
        womanBtn.setImageResource(R.drawable.womanoff)
        otherBtn.setImageResource(R.drawable.otheroff)

    }

    // 性別に男性のセット、ボタンUI切り替え
    private fun setSexIsWoman(){
        this.userSex = "女"

        // ボタンUI切り替え
        manBtn.setImageResource(R.drawable.manoff)
        womanBtn.setImageResource(R.drawable.womanon)
        otherBtn.setImageResource(R.drawable.otheroff)

    }

    // 性別に男性のセット、ボタンUI切り替え
    private fun setSexIsOther(){
        this.userSex = "その他"

        // ボタンUI切り替え
        manBtn.setImageResource(R.drawable.manoff)
        womanBtn.setImageResource(R.drawable.womanoff)
        otherBtn.setImageResource(R.drawable.otheron)

    }

    // パスワードの一致チェック
    private fun checkPassword() : Int {
        if (userPassword.text.toString() != userPasswordConfirm.text.toString()) {
            showAlert("エラー", "パスワードが一致しません")
            return 1
        }
        return 0
    }

    // 必須入力チェック
    private fun checkRequiredVal() : Int {
        if (userName.text.isEmpty() || userMailAddress.text.isEmpty()
            || userPassword.text.isEmpty() || userPasswordConfirm.text.isEmpty()
        ) {
            showAlert("エラー", "必須項目を入力してください")
            return 1
        }
        return 0
    }

    // 次画面遷移とデータ渡し
    private fun moveRegisterSchoolsView(){

        // 必須チェック
        errStatus = checkRequiredVal()
        if (errStatus == 1) {
            return
        }

        // パスワードのチェック
        errStatus = checkPassword()
        if (errStatus == 1) {
            return
        }

        // 誕生日と学年の計算
        setBirthDayAndGrade()

        // データをPreferenceにセット
        val dataStore: SharedPreferences = getSharedPreferences("DataStore", Context.MODE_PRIVATE)
        val editor = dataStore.edit()

        editor.putString("userName",userName.text.toString())
        editor.putString("gender",this.userSex)
        editor.putString("entryYear","2000")
        editor.putString("userBirthYear", this.year.toString())
        editor.putString("userBirthMonth", this.month.toString())
        editor.putString("userBirthDay", this.day.toString())
        editor.putString("elementalySchoolEntryYear", this.elementalySchoolEntryYear)
        editor.putString("juniorHighSchoolEntryYear", this.juniorHighSchoolEntryYear)
        editor.putString("highSchoolEntryYear", this.highSchoolEntryYear)
        editor.putString("mailAddress",userMailAddress.text.toString())
        editor.putString("password",userMailAddress.text.toString())

        editor.apply()

        // 次画面intentの生成
        val intent = Intent(getApplication(), SelectPicture::class.java)

        intent.putExtra("checkInputBirthday",checkBirthday)

        // 次画面遷移
        startActivity(intent)

    }

    // アラートの生成
    private fun showAlert(title:String, message: String){
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK", null)
            show()
        }
    }

}
