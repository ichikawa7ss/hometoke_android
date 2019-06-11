package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.myapplication.db.ReceiveOpenHelper
import com.nifcloud.mbaas.core.*
import kotlinx.android.synthetic.main.activity_confirmation.*
import kotlinx.android.synthetic.main.activity_select_picture.*
import org.jetbrains.anko.db.insert
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Confirmation : AppCompatActivity() {

    // 登録されたユーザのオブジェクトID
    private var objectId : String = ""
    private var password : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        // 初期化
        NCMB.initialize(
            applicationContext,
            "4d5dd8d3a2c9d8030304c97a8e4fee8b5d8a6ccbe215cb1504679484290e2432",
            "61982facb497f1f345b17ce84a0bc307f095cea9b7ccd56df908207ada19cc25"
        )

        // TODO　デバッグ後消す
        saveProfileImg(intent.getStringExtra("filepath"))

        // Preferenceの設定
        val dataStore = getSharedPreferences("DataStore", Context.MODE_PRIVATE)

        val checkInputBirthday = intent.getBooleanExtra("checkInputBirthday",true)

        // preferenceからのデータ受け取り
        userName.text = dataStore.getString("userName","")
        userSex.text = dataStore.getString("gender","")
        birthYear.text = dataStore.getString("userBirthYear","")
        birthMonth.text = dataStore.getString("userBirthMonth","")
        birthDay.text = dataStore.getString("userBirthDay","")
        ElementarySchool.text = dataStore.getString("elementarySchool","")
        JuniorHighSchool.text = dataStore.getString("juniorHighSchool","")
        HighSchool.text = dataStore.getString("highSchool","")
        if (checkInputBirthday) {
            elementarySchoolEntryYear.text = dataStore.getString("elementalySchoolEntryYear","")
            juniorHighSchoolEntryYear.text = dataStore.getString("juniorHighSchoolEntryYear","")
            highSchoolEntryYear.text = dataStore.getString("highSchoolEntryYear","")
        } else {
            elementarySchoolEntryYear.text = ""
            juniorHighSchoolEntryYear.text = ""
            highSchoolEntryYear.text = ""
            birthMonthLabel.text = ""
            birthDayLabel.text = ""
            birthYearLabel.text = ""
        }
        mailAddress.text = dataStore.getString("mailAddress","")
        this.password = dataStore.getString("password","")

        // 完了ボタン押下
        CompleteBtn.setOnClickListener() {

            // ログインフラグを立てる

            val PREFERENCES_FILE_NAME = "preference"

            val settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0) // 0 -> MODE_PRIVATE
            val editor = settings.edit()
            editor.putLong("logged-in", 1).apply()

            // ユーザ登録・初回褒め登録・友達登録
            registM_users()
            // 会員管理
            registAccountMng()
        }
    }

    // m_userへの登録
    private fun registM_users() {

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
        obj.put("juniorHighSchool", JuniorHighSchool.text.toString())
        obj.put("loginFlg", "1")
        obj.put("mailAddress", mailAddress.text.toString())
        obj.put("registTitle", "ホメ界の新星")
        obj.put("userName", userName.text.toString())

        // ニフクラへの保存実行
        obj.saveInBackground { e ->
            if (e != null) {
                // 保存に失敗した場合の処理
                Log.e("[ERROR]", e.toString())
                throw e
            } else {
                // 保存に成功した場合の処理
                Log.d("[DEBUG]", "保存成功　ユーザー")
                Log.d("[DEBUG]", obj.toString())
                // objectIdを取得
                this.objectId = obj.objectId
                // sharedPreferencesの保存　←　画面遷移後のnullをなくすために早期にpreferenceを保存
                saveSharedPrefarence()
                // プロフィール画像の登録
                saveProfileImg(intent.getStringExtra("filepath"))
                // 初回のホメの登録
                registE_serveFirst()
                // 友達登録を実施
                registM_friends()
            }
            // 次画面遷移
            moveToTutorialView()
        }
    }

    // 初回のホメの登録
    private fun registE_serveFirst() {

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
        // 保存に成功したらSQLiteにも保存する
        obj.saveInBackground { e ->
            if (e != null) {
                // 保存に失敗した場合の処理
                Log.e("[ERROR]", "保存失敗　メッセ")
                Log.e("[ERROR]", e.toString())
                throw e
            } else {
                // 保存に成功した場合の処理
                Log.d("[DEBUG]", "保存成功　メッセ")
                Log.d("[DEBUG]", obj.toString())

                val imageName = obj.getString("questionId")
                val fileName = NCMBFile("${imageName}.png")

                // 質問画像をBITMAPへ変化してSQLiteに格納
                fileName.fetchInBackground { imgData, error ->
                    Log.d("[DEBUG]", "アクセス成功　ファイルストア")

                    // SQLiteへの登録
                    // OpenHelperの呼び出して、DB操作用のオブジェクトを用意
                    val helper = ReceiveOpenHelper(applicationContext)

                    helper.use {
                        // 最初にホメられた日時を取得する
                        val splitDate = obj.getString("createDate").split("-")
                        val serveDate = splitDate[0] + "/" + splitDate[1] + "/" + splitDate[2].substring(0, 2)

                        // SQLiteに挿入
                        insert(
                            ReceiveOpenHelper.TABLE_NAME,
                            "questionTitle" to obj.getString("questionTitle"),
                            "serverTitle" to obj.getString("serverTitle"),
                            "questionPhrase" to obj.getString("questionPhrase"),
                            "serveDate" to serveDate,
                            "stampImageBlob" to imgData as ByteArray,
                            "readFlg" to obj.getString("readFlg")
                        )

                        Log.d("[DEBUG]", "保存成功　SQLite")
                    }
                }
            }
        }
    }

    // 会員管理への登録
    private fun registAccountMng() {
        // 会員管理登録用のオブジェクト
        val user = NCMBUser()
        // 会員管理への登録情報
        user.userName = userName.text.toString()
        user.setPassword(this.password)
        user.mailAddress = mailAddress.text.toString()
        user.saveInBackground { e ->
            if (e != null) {
                // 保存に失敗した場合の処理
                Log.e("[ERROR]", "保存失敗　会員管理")
                Log.e("[ERROR]", e.toString())
            } else {
                // 保存に成功した場合の処理
                Log.d("[DEBUG]", "保存成功　会員管理")
                Log.d("[DEBUG]", user.toString())
            }
        }
    }

    // 友達を登録する
    private fun registM_friends() {

        // ユーザマスタを検索
        val query = NCMBQuery<NCMBObject>("m_users")
        val query1 = NCMBQuery<NCMBObject>("m_users")
        val query2 = NCMBQuery<NCMBObject>("m_users")
        val query3 = NCMBQuery<NCMBObject>("m_users")

        // 検索条件の設定
        query1.whereEqualTo("elementarySchool", ElementarySchool.text.toString())
        query1.whereEqualTo("entryYear", elementarySchoolEntryYear.text.toString())

        query2.whereEqualTo("juniorHighSchool", JuniorHighSchool.text.toString())
        query2.whereEqualTo("entryYear", elementarySchoolEntryYear.text.toString())

        query3.whereEqualTo("highSchool", HighSchool.text.toString())
        query3.whereEqualTo("entryYear", elementarySchoolEntryYear.text.toString())

        query.or(Arrays.asList(query1, query2, query3) as Collection<NCMBQuery<NCMBBase>>?)

        // 検索の実行
        query.findInBackground { objects, e ->
            if (e != null){
                // 検索失敗時の処理

                Log.e("[ERROR]", e.message)
            } else {
                // 検索成功時に友達マスタを登録する
                for (user in objects) {
                    // 友達テーブルへの登録
                    if (user.objectId != this.objectId) {
                        val obj = NCMBObject("m_friends")
                        obj.put("blockFlg", "0")
                        obj.put("friendId", user)
                        obj.put("userId", this.objectId)
                        obj.saveInBackground { e ->
                            if (e != null) {
                                // 保存に失敗した場合の処理
                                Log.e("[ERROR]", "保存失敗　友達")
                                Log.e("[ERROR]", e.toString())
                                throw e
                            } else {
                                // 保存に成功した場合の処理

                                Log.d("[DEBUG]", "保存成功　友達")
                                Log.d("[DEBUG]", obj.toString())
                            }
                        }
                    }
                }
            }
        }
    }

    // SharedPrefarenceへの登録
    private fun saveSharedPrefarence() {
        // preference,editer,dataFormatの用意
        // Preferenceの設定
        val dataStore = getSharedPreferences("DataStore", Context.MODE_PRIVATE)
        val editor = dataStore.edit()

        @SuppressLint("SimpleDateFormat")
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

        editor.putString("objectId",this.objectId)
        editor.putString("registTitle","ホメ界の新星")
        editor.putString("updateFriendsTime", df.format(Date()))
        editor.putString("updateReceiveTableTime", df.format(Date())).apply()

    }

    private fun saveProfileImg (filepath:String) {
        // 読み出し確認
        val file = File(filepath)

        try {
            // ファイルパスをBitmapへ変換
            FileInputStream(file).use({ inputStream0 ->
                val bitmap = BitmapFactory.decodeStream(inputStream0)

                // 画像をニフクラへ保存
                val byteArrayStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayStream)
                val dataByte = byteArrayStream.toByteArray()

                //ACL 読み込み:可 , 書き込み:可
                val acl = NCMBAcl()
                acl.publicReadAccess = true
                acl.publicWriteAccess = true

                // 登録するファイル情報
                val file : NCMBFile = NCMBFile("${this.objectId}.png", dataByte, acl)
                file.saveInBackground { e ->
                    if (e != null) {
                        // エラー時の処理
                        Log.d("[DEBUG]", "画像の登録に失敗しました")
                    } else {
                        // 登録成功時の処理
                        Log.d("[DEBUG]", "画像の登録に成功")
                    }

                }

            })
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    // 次画面遷移
    private fun moveToTutorialView() {

        // 次画面intentの生成

        // TODO チュートリアル画面の作成
        val intent = Intent(getApplication(), NDServeActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        // 次画面遷移
        startActivity(intent);
    }
}
