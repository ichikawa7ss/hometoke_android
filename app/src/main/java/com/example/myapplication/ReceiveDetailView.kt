package com.example.myapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_detail_view.*

class ReceiveDetailView : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_view)


        // 戻るボタン "<" の作成　詳細不明
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "SubActivity"

        // 褒められた画面から送られてきた情報を取り出す
        val intent = getIntent()

        // 画面作成
        stamp_image.setImageResource(intent.getIntExtra("intent_imgid",0))
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