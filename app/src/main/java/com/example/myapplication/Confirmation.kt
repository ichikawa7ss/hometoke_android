package com.example.myapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.nifcloud.mbaas.core.NCMBObject
import com.nifcloud.mbaas.core.NCMB
import kotlinx.android.synthetic.main.activity_confirmation.*
import android.util.Log
import com.nifcloud.mbaas.core.NCMBException

class Confirmation : AppCompatActivity() {

    // 登録されたユーザのオブジェクトID
    private var objectId : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        // 初期化
        NCMB.initialize(applicationContext,"1115bda19d0575ef1b6650b35fbfaac587e5dd28bf61f23c9d03405052fa3be1", "ebf5c8d490aa0bc70fa7cc617f0b426422812c3ddccda0bc16de3c0088890de7")

        // 前画面からのデータ受け取り
        userNm.text = intent.getStringExtra("userNm")
        userSex.text = intent.getStringExtra("userSex")
        birthYear.text = intent.getStringExtra("userbirthYear")
        birthMonth.text = intent.getStringExtra("userbirthMonth")
        birthDay.text = intent.getStringExtra("userbirthDay")
        ElementarySchool.text = intent.getStringExtra("elementarySchool")
        JuniorHighSchool.text = intent.getStringExtra("juniorHighSchool")
        HighSchool.text = intent.getStringExtra("highSchool")
        elementarySchoolEntryYear.text = intent.getStringExtra("elementalySchoolEntryYear")
        juniorHighSchoolEntryYear.text = intent.getStringExtra("juniorHighSchoolEntryYear")
        highSchoolEntryYear.text = intent.getStringExtra("highSchoolEntryYear")
        mailAddress.text = intent.getStringExtra("mailAddress")

        // 完了ボタン押下時
        CompleteBtn.setOnClickListener() {
            try {
                registM_users() /* TODO 非同期で実装する ⭐️*/
                registE_serveFirst() /* TODO 非同期で実装する ⭐️*/
                registAccountMng()
                registM_friends()
                saveSharedPrefarence()
                moveToTutorialView()
            } catch (e : NCMBException) {
                Log.d("[DEBUG]", "catchしますた")
            }
        }
    }

    // m_userへの登録
    private fun registM_users () {

        // ユーザテーブル格納用のNCMBObjectを作成
        val obj = NCMBObject("m_users")

        // ユーザテーブル格納用のデータを登録
        obj.put("birthDay", birthDay.text.toString())
        obj.put("birthMonth", birthMonth.text.toString())
        obj.put("birthYear", birthYear.text.toString())
        obj.put("deviceToken", "")
        obj.put("elementarySchool", ElementarySchool.text.toString())
        obj.put("entryYear", elementarySchoolEntryYear.text.toString())
        obj.put("gender", userSex.text.toString())
        obj.put("highSchool", HighSchool.text.toString())
        obj.put("juniorHighSchool", juniorHighSchoolEntryYear.text.toString())
        obj.put("loginFlg", "1")
        obj.put("mailAddress", mailAddress.text.toString())
        obj.put("registTitle", "ホメ界の新星")
        obj.put("userName", userNm.text.toString())

        // ニフクラへの保存実行
        obj.saveInBackground { e ->
            if (e != null) {
                // 保存に失敗した場合の処理
                Log.d("[Error]", e.toString())
                throw e
            } else {
                // 保存に成功した場合の処理
                Log.d("[DEBUG]", "保存成功")
                Log.d("[DEBUG]", obj.toString())
                // objectIdを取得
                this.objectId = obj.objectId
            }
        }

    }

    // 初回のホメの登録
    private fun registE_serveFirst () {

        // e_serveテーブル格納用のNCMBObjectを作成
        val obj = NCMBObject("e_serve")

        // e_serveテーブル格納用のデータを登録
        obj.put("questionId", "EhjSthKAdd1zLtnR")
        obj.put("questionPhrase", "とても大切な人")
        obj.put("questionTitle", "ホメ界の新星")
        obj.put("readFlg", "0")
        obj.put("receiverId", this.objectId)
        obj.put("serverId", "W4fjxRY7OAVytgID")
        obj.put("serverTitle", "ホメ界の新人")

        // ニフクラへの保存実行
        obj.saveInBackground { e ->
            if (e != null) {
                // 保存に失敗した場合の処理
                Log.d("[Error]", e.toString())
                throw e
            } else {
                // 保存に成功した場合の処理
                Log.d("[DEBUG]", "保存成功")
                Log.d("[DEBUG]", obj.toString())
            }
        }

    }

    // 会員管理への登録
    private fun registAccountMng () {

    }

    // 友達を登録する
    private fun registM_friends () {

    }

    // SharedPrefarenceへの登録
    private fun saveSharedPrefarence(){

    }

    // 次画面遷移
    private fun moveToTutorialView () {

    }
}
