package com.example.myapplication

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_detail_view.*

class ReceiveDetailView : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_view)


        // 戻るボタン "<" の作成　詳細不明
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "ホメられた"

        // 褒められた画面から送られてきた情報を取り出す
        val intent = getIntent()

//        intent.putExtra("intent_imgid", selected_imgid)
        // 画面作成
        val imgData = intent.getByteArrayExtra("intent_imgid")
        Log.d("[DEBUG]", "画像情報：${imgData.toString()}")

        stamp_image.setImageBitmap(BitmapFactory.decodeByteArray(imgData, 0, imgData.size))
        stamp_title.text     = intent.getStringExtra("intent_title")
        stamp_server.text    = intent.getStringExtra("intent_server")
        stamp_user_name.text = intent.getStringExtra("intent_user_name")
        stamp_question.text  = intent.getStringExtra("intent_question")

    }

    // 戻るボタン "<" が押された時の設定
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

}