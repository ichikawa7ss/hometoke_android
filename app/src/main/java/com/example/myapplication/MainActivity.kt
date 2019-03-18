package com.example.myapplication

import android.content.Context
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.nifcloud.mbaas.core.NCMB
import com.nifcloud.mbaas.core.NCMBFile
import com.nifcloud.mbaas.core.NCMBObject
import com.nifcloud.mbaas.core.NCMBQuery
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

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
        // 初期化
        NCMB.initialize(applicationContext, "1115bda19d0575ef1b6650b35fbfaac587e5dd28bf61f23c9d03405052fa3be1", "ebf5c8d490aa0bc70fa7cc617f0b426422812c3ddccda0bc16de3c0088890de7")

        allShuffulBtn.setOnClickListener {
            decideQuestion()
        }

        friendShuffulBtn.setOnClickListener {
            displayReceiver(receiverImage0, receiverName0,0)
            displayReceiver(receiverImage1, receiverName1,1)
            displayReceiver(receiverImage2, receiverName2,2)
            displayReceiver(receiverImage3, receiverName3,3)
        }

    //　TODO("レシーバー画像のボタン化→ホメる機能の検証")

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
                //TODO("取得されなかった場合の動作")
            } else {
                val num = (0..(this.dataQuestions.size.toInt() - 1)).random()
                val objectId = this.dataQuestions[num].objectId

                //TODO("最初の質問（画像読み込みタイミング）は別でオブジェクトIDを取得しておく")
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
                        Log.d("[ERROR]", eFile.toString())
                    } else {
                        //成功
                        questionImage.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.size))
                        Log.d("[DEBUG]", "質問画像読み込み成功")
                    }
                }
                // 質問の検索・取得
                val queryReceiver = NCMBQuery<NCMBObject>("m_users")
                queryReceiver.findInBackground { objs, eFile ->
                    if (eFile != null) {
                        //失敗
                        Log.d("[ERROR]", eFile.toString())
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
                            Log.d("[ERROR]", eFile.toString())
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
        if (receiverName.isEmpty()) Log.d("[ERROR]", "このメンバーは褒められません") else {
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
                    Log.d("[ERROR]", error.toString())
                } else {
                    Log.d("[DEBUG]", obj.toString())
                    Log.d("[DEBUG]", "e_serveデータ保存成功")
                }
            }
        }
    }
}

