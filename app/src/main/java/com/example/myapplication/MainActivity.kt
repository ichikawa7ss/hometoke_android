package com.example.myapplication

import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.nifcloud.mbaas.core.*
import kotlinx.android.synthetic.main.activity_main.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    // TODO パーツの適切な配置
    // TODO オートレイアウト
    // TODO 質問画像のキャッシュ化
    // TODO シングルトンを利用して友達一覧を取得
    // TODO 友達画像のキャッシュ化
    // TODO 褒めた時にアラートをだす
    // TODO 友達画像をいい感じにする
    // TODO エラーアラートをだす
    // TODO タブの表示方法を調べる
    // TODO ファイルをマージしてそのためのgitに格納する

    // DLした質問オブジェクト
    var dataQuestions = listOf<NCMBObject>()

    // DLしたレシーバーデータ格納用
    var dataReceivers = listOf<NCMBObject>()

    // receiverのID,名前,デバイストークン
    val objectIds = mutableListOf<String>("","","","")
    val receiverNames = mutableListOf<String>("","","","")
    val receiverDeviceTokens = mutableListOf<String>("","","","")

    var questionId : String = ""
    var questionGenderCondition : String = ""
    var questionTitle : String = ""
    var questionTempPhrase : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // singltonクラスの宣言
        FriendData.onCreateApplication(applicationContext)

        // NCMB初期化
        NCMB.initialize(applicationContext, "1115bda19d0575ef1b6650b35fbfaac587e5dd28bf61f23c9d03405052fa3be1", "ebf5c8d490aa0bc70fa7cc617f0b426422812c3ddccda0bc16de3c0088890de7")

        // TODO 【ここから】テスト用userInfoを新規登録画面作成後に消す
        val userInfo: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())

        val editor = userInfo.edit()
        editor.putString("userName","市川しょま")
        editor.putString("objectId","SOcuIKHKOBVdjKn7")
        editor.putString("loginFlg","1")
        editor.putString("gender","男")
        editor.putString("elementarySchool","あきる野市立東秋留小学校")
        editor.putString("juniorHighSchool","あきる野市立秋多中学校")
        editor.putString("highSchool","あきる野市立秋留台高等学校")
        editor.putString("entryYear","2000")
        editor.putString("registTitle","ホメ界の新星")
        editor.putString("questionId","")

        // userInfo.edit().remove("updateFriendsTime").commit()
        editor.commit()

        if (userInfo.getString("updateFriendsTime", null) == null) {
            val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            editor.putString("updateFriendsTime", df.format(Date()))
            editor.apply()
        }
        // TODO【ここまで】

        updateMFriends()

        allShuffulBtn.setOnClickListener {
            decideQuestion()
        }

        friendShuffulBtn.setOnClickListener {
            displayReceiver(receiverImage0, receiverName0,0)
            displayReceiver(receiverImage1, receiverName1,1)
            displayReceiver(receiverImage2, receiverName2,2)
            displayReceiver(receiverImage3, receiverName3,3)
        }

        receiverImage0.setOnClickListener{
            serveReceiver(0)
        }
        receiverImage1.setOnClickListener{
            serveReceiver(1)
        }
        receiverImage2.setOnClickListener{
            serveReceiver(2)
        }
        receiverImage3.setOnClickListener{
            serveReceiver(3)
        }


        decideQuestion()

    }

    fun decideQuestion() {
        // 質問の検索・取得
        val query = NCMBQuery<NCMBObject>("m_questions")

        query.whereEqualTo("releaseFlg","1")
        query.findInBackground { objs, e ->
            this.dataQuestions = objs
            val doubleVal: Double = objs.size.toDouble()
            var noRepeat = false

            if (this.dataQuestions.isEmpty()) {
                Log.w("[WARN]","取得できる質問がありません")
            } else {
                val num = (0..(this.dataQuestions.size.toInt() - 1)).random()
                val objectId = this.dataQuestions[num].objectId

                // TODO("最初の質問（画像読み込みタイミング）は別でオブジェクトIDを取得しておく")
                // firstQImageId = objectId!

                questionGenderCondition = objs[num].getString("genderCondition")
                questionTitle = objs[num].getString("title")
                questionTempPhrase = objs[num].getString("questionPhrase")
                questionId = objs[num].objectId

                questionPhrase.text = questionTempPhrase
                val qImageFileName = questionId
                val file = NCMBFile("$qImageFileName.png")
                Log.d("[DEBUG]", this.questionId)
                file.fetchInBackground { data, eFile ->
                    if (eFile != null) {
                        //失敗
                        questionImage.setImageResource(R.drawable.noquestionimage)
                        Log.e("[ERROR]", eFile.toString())
                    } else {
                        //成功
                        questionImage.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.size))
                        Log.d("[DEBUG]", "質問画像読み込み成功")
                    }
                }
                // 友達の検索・取得
                val queryReceiver = NCMBQuery<NCMBObject>("m_users")
                queryReceiver.findInBackground { objs, eFile ->
                    if (eFile != null) {
                        //失敗
                        Log.e("[ERROR]", eFile.toString())
                    } else {
                        //成功
                        Log.d("[DEBUG]", "友達データ${objs.size}件　読み込み成功")

                        this.dataReceivers = objs

                        displayReceiver(receiverImage0, receiverName0,0)
                        displayReceiver(receiverImage1,receiverName1,1)
                        displayReceiver(receiverImage2, receiverName2,2)
                        displayReceiver(receiverImage3, receiverName3,3)

                    }
                }
            }
        }
    }
    
    fun displayReceiver(receiverImage: ImageView, receiverName:TextView, receiverNum: Int) {
        // receiverを決めて表示する
        var isDoubling = true
        var countRandom = 0
        while (isDoubling && countRandom < 20) {
            // isDoublingがtrue->被りあり->乱数発生、レシーバー表示を被りがなくなるまで繰り返す
            // 乱数をふる
            if (this.dataReceivers.isEmpty()) {
                // すべての画像をユーザなしに設定
                receiverImage.setImageResource(R.drawable.noimage)
                receiverName.text = "ユーザがいません"
                return
            }
            val numRondom = (0..(this.dataReceivers.size.toInt() - 1)).random()
            val objectId = this.dataReceivers[numRondom].objectId
            for (num in 0..receiverNum) {
                // レシーバー番号の分だけ配列をチェックする
                if(objectIds[num] == objectId) {
                    // objectIdがobjectIdsに入っていたらbreak
                    isDoubling = true
                    countRandom += 1
                    if (countRandom == 20) {
                        receiverImage.setImageResource(R.drawable.noimage)
                        receiverName.text = "ユーザがいません"
                    }
                    break
                } else if (num == receiverNum) {
                    // 被りなし　&& 最後の試行の時にレシーバーを表示
                    isDoubling = false
                    // 新しいレシーバーラベルを出す
                    val receiverNameTemp = this.dataReceivers[numRondom].getString("userName")
                    receiverName.text = receiverNameTemp

                    // TODO("レシーバーのデバイストークンを取得")
//                    // レシーバーのデバイストークンを取得
//                    if (this.dataReceivers[numRondom].getString( "deviceToken") != null) {
//                        // デバイストークンがnilじゃなければ
//                        receiverDeviceTokens[receiverNum] = receiverDeviceToken as! String
//                    } else {
//                        // nilなら
//                        print("deviceToken is nothing")
//                        receiverDeviceTokens[receiverNum] = ""
//                    }

                    objectIds[receiverNum] = objectId
                    receiverNames[receiverNum] = receiverNameTemp

                    val friendImageFileName = objectId
                    val file = NCMBFile("$friendImageFileName.png")
                    file.fetchInBackground { data, eFile ->
                        if (eFile != null) {
                            //失敗
                            receiverImage.setImageResource(R.drawable.noimage)
                            Log.e("[ERROR]", eFile.toString())
                        } else {
                            //成功
                            receiverImage.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.size))
                            Log.d("[DEBUG]", data.toString())
                            Log.d("[DEBUG]", "友達画像読み込み成功")
                        }
                    }
                }
            }
        }
    }

    fun serveReceiver (receiverNum: Int) {
        val receiverName : String = receiverNames[receiverNum]
        if (receiverName.isEmpty()) Log.e("[ERROR]", "このメンバーは褒められません") else {
            val displaySrc = "$receiverName さんを「$questionTitle」とホメました"

            // ホメるテーブル格納用のNCMBObjectを作成
            val obj = NCMBObject("e_serve")
            // ホメるテーブルのカラムに値を設定
            obj.put("questionId", questionId)
            obj.put("readFlg","0" )
            obj.put("questionTitle", this.questionTitle)
//            obj.put(, "serverId")
//            obj.put(, "serverTitle")
            obj.put("receiverId",objectIds[receiverNum] )
            obj.put("questionPhrase",this.questionTempPhrase)

            // データストアへの保存を実施
            obj.saveInBackground{ error ->
                if (error != null) {
                    // 失敗
                    Log.e("[ERROR]", error.toString())
                } else {
                    Log.d("[DEBUG]", obj.toString())
                    Log.d("[DEBUG]", "e_serveデータ保存成功")
                }
            }
        }
    }

    fun updateMFriends () {
        // 更新日付を設定してm_friendsを更新
        // 初期に実施した検索条件とは別

        // 学校が一致する友達をqueryで取得する
        // sharedPreference を呼び出し
        val userInfoSP : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())

        val query1 = NCMBQuery<NCMBObject>("m_users")
        query1.whereEqualTo("elementarySchool","あきる野市立東秋留小学校")
        query1.whereEqualTo("entryYear", userInfoSP.getString("entryYear", ""))
        val query2 = NCMBQuery<NCMBObject>("m_users")
        query2.whereEqualTo("juniorHighSchool","あきる野市立秋多中学校")
        query2.whereEqualTo("entryYear", userInfoSP.getString("entryYear", ""))
        val query3 = NCMBQuery<NCMBObject>("m_users")
        query3.whereEqualTo("highSchool","あきる野市立秋留台高等学校")
        query3.whereEqualTo("entryYear", userInfoSP.getString("entryYear", ""))
        val query = NCMBQuery<NCMBObject>("m_users")
        query.or(arrayListOf(query1,query2,query3) as Collection<NCMBQuery<NCMBBase>>?)
        // AND条件で追加分の友達のみを指定
        query.whereGreaterThan("createDate",userInfoSP.getString("updateFriendsTime", null).toDate())
        query.findInBackground { results, e ->
            // 友達がいた場合, 取得した友達をm_friendsに保存する
            if (results.size > 0) {
                for (i in 0..(results.size - 1)) {
                    //　自分のデータは登録しない
                    if (results[i].objectId != userInfoSP.getString("objectId", "")) {
                        val obj = NCMBObject("m_friends")
                        obj.put("userId", userInfoSP.getString("objectId", ""))
                        obj.put("friendId", results[i])
                        obj.put("blockFlg", "0")
                        obj.saveInBackground { error ->
                            if (error == null) {
                                // sharedPreferenceの友達更新日時を更新
                                val editor = userInfoSP.edit()
                                val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                                editor.putString("updateFriendsTime", df.format(Date()))
                                editor.apply()
                                Log.d("[DEBUG]", "更新日付：${Date()} ,「${results[i].getString("userName")}」を友達追加")

                            } else {
                                // 保存に失敗した場合の処理
                                Log.e("[ERROR]", "友達が保存できませんでした")
                                Log.e("[ERROR]", error.toString())
                            }
                        }
                    }
                }
            } else if (e == null){
                // 異常なエラーがなければ
                Log.d("[DEBUG]", "追加する友人がいません。アプリを広めましょう")
            } else {
                Log.e("[ERROR]", "ニフクラへのアクセスに失敗")
                Log.e("[ERROR]", e.toString())
            }
        }
    }

    fun String.toDate(pattern: String = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"): Date? {
        val sdFormat = try {
            SimpleDateFormat(pattern)
        } catch (e: IllegalArgumentException) {
            null
        }
        val date = sdFormat?.let {
            try {
                it.parse(this)
            } catch (e: ParseException){
                null
            }
        }
        return date
    }
}

