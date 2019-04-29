package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.nifcloud.mbaas.core.NCMB
import kotlinx.android.synthetic.main.activity_receive_view.*

class ReceiveView() : AppCompatActivity(){

    val array_title = arrayOf<String>(
        "Mercury",
        "Venus",
        "The Earth",
        "Mars",
        "Jupiter",
        "Saturn",
        "Uranus",
        "Neptune",
        "Pluto")

    val array_server = arrayOf<String>(
        "Solar System",
        "Solar System",
        "Solar System",
        "Solar System",
        "Solar System",
        "Solar System",
        "Solar System",
        "Solar System",
        "Solar System"
    )

    val array_question = arrayOf<String>(
        "大丈夫？暑くない？",
        "お金持ってそう",
        "母なる大地",
        "住めそうで住めない",
        "キモ美しい",
        "悪魔の星",
        "いつも寝てる",
        "顔色悪いよ？ちょっと青すぎない？",
        "出来損ない"
    )

    val array_imgid = arrayOf<Int>(
        R.drawable.noimage,
        R.drawable.noimage,
        R.drawable.noimage,
        R.drawable.noimage,
        R.drawable.noimage,
        R.drawable.noimage,
        R.drawable.noimage,
        R.drawable.noimage,
        R.drawable.noimage
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NCMB.initialize(applicationContext, "1115bda19d0575ef1b6650b35fbfaac587e5dd28bf61f23c9d03405052fa3be1", "ebf5c8d490aa0bc70fa7cc617f0b426422812c3ddccda0bc16de3c0088890de7")
        setContentView(R.layout.activity_receive_view)

        // ListView = TableView の表示
        val myListAdapter = MyArrayAdapter(this,array_imgid,array_title,array_server,array_question)
        listView.adapter = myListAdapter


        // リストがタップされたら手紙画面へ遷移するための設定
        listView.setOnItemClickListener {parent, view, position, id ->
            val intent = Intent(this, ReceiveDetailView::class.java)

            // タップされた position の情報を取得して加工
            val selected_imgid     = array_imgid[position]
            val selected_title     = "「" + array_title[position] + "」"
            val selected_server    = "From " + array_server[position]
            val selected_user_name = "yourname " + "さん"
            val selected_question  = array_question[position]

            // intent = 画面間で渡される入れ物 に表示したい情報をセット
            intent.putExtra("intent_imgid", selected_imgid)
            intent.putExtra("intent_title", selected_title)
            intent.putExtra("intent_server", selected_server)
            intent.putExtra("intent_user_name", selected_user_name)
            intent.putExtra("intent_question", selected_question)

            // intent を手紙画面へ
            startActivity(intent)
        }

    }
}



// リスト項目を保持 & 再利用するための入れ物
data class ViewHolder(
    val holderImageView: ImageView,
    val holderTitleLabel: TextView,
    val holderserver_user: TextView,
    val holderquestionPhraseLabel: TextView
)



// ListView に 1 行ずつリスト項目を引き渡すための入れ物
class MyArrayAdapter (private val context: Activity,
                      private val imgid: Array<Int>,
                      private val title: Array<String>,
                      private val server: Array<String>,
                      private val question: Array<String>) : ArrayAdapter<String>(context, R.layout.item_receive_cell, title) {

    // inflater という謎の必須設定
    private var inflater : LayoutInflater? = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

    // ここからが処理
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder : ViewHolder? = null
        var view = convertView

        // ViewHolder に値があれば再利用して表示
        // なければ新たに取得し、ViewHolder へ格納
        if (view == null) {

            view = inflater!!.inflate(R.layout.item_receive_cell, parent, false)
            viewHolder = ViewHolder(
                view.findViewById(R.id.myImageView),
                view.findViewById(R.id.myTitleLabel),
                view.findViewById(R.id.server_user),
                view.findViewById(R.id.questionPhraseLabel)
            )
            view.tag = viewHolder

        } else {
            viewHolder = view.tag as ViewHolder
        }

        // リストの情報を設定
        viewHolder.holderImageView.setImageResource(imgid[position])
        viewHolder.holderTitleLabel.text = title[position]
        viewHolder.holderserver_user.text = "From " + server[position]
        viewHolder.holderquestionPhraseLabel.text = question[position]

        return view!!
    }
}