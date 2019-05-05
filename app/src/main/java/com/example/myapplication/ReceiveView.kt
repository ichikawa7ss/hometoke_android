package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.db.ReceiveOpenHelper
import com.example.myapplication.db.ReceiveRowParser
import com.example.myapplication.entity.ReceiveEntity
import com.nifcloud.mbaas.core.*
import kotlinx.android.synthetic.main.activity_receive_view.*
import org.jetbrains.anko.db.SqlOrderDirection
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ReceiveView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NCMB.initialize(applicationContext, "1115bda19d0575ef1b6650b35fbfaac587e5dd28bf61f23c9d03405052fa3be1", "ebf5c8d490aa0bc70fa7cc617f0b426422812c3ddccda0bc16de3c0088890de7")
        setContentView(R.layout.activity_receive_view)


        // TODO 【ここから】テスト用userInfoを新規登録画面作成後に消す
        val userInfo: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())

        val editor = userInfo.edit()
        editor.putString("userName","市川しょま")
        editor.putString("objectId","SOcuIKHKOBVdjKn7")
        editor.putString("questionId","")

        editor.apply()

        if (userInfo.getString("updateFriendsTime", null) == null) {
            @SuppressLint("SimpleDateFormat")
            val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            editor.putString("updateFriendsTime", df.format(Date()))
            editor.apply()
        }
        // TODO【ここまで】

    //uploadReceiveRecord()

        val helper = ReceiveOpenHelper(applicationContext)

        helper.use {
            val list = select(ReceiveOpenHelper.TABLE_NAME)
                .orderBy("_id",SqlOrderDirection.DESC)
                .parseList(ReceiveRowParser())

            listView.adapter = ReceiveAdapter(this@ReceiveView,list)

            // リストがタップされたら手紙画面へ遷移するための設定
            listView.setOnItemClickListener {parent, view, position, id ->
                val intent = Intent(this@ReceiveView, ReceiveDetailView::class.java)

                // タップされた position の情報を取得して加工
                val selected_imgid     = list[position].stampImageBlob
                val selected_title     = "「" + list[position].questionTitle + "」"
                val selected_server    = "From " + list[position].serverTitle
                val selected_user_name = "${userInfo.getString("userName", "")}さん"
                val selected_question  = list[position].questionPhrase

                // intent = 画面間で渡される入れ物 に表示したい情報をセット
                intent.putExtra("intent_imgid", selected_imgid)
                intent.putExtra("intent_title", selected_title)
                intent.putExtra("intent_server", selected_server)
                intent.putExtra("intent_user_name", selected_user_name)
                intent.putExtra("intent_question", selected_question)

                // intent を手紙画面へ
                startActivity(intent)
                Log.d("[DEBUG]", "手紙画面への遷移完了")
            }
        }
    }

    // ホメられたデータをSQLiteに書き込み
    // 更新されてないデータだけを検索して新規登録
    fun uploadReceiveRecord() {

        // preference,editer,dataFormatの用意
        val pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        val editor = pref.edit()
        editor.putString("updateReceiveTableTime", "2019-03-05T23:58:08.469+09:00").apply()

        @SuppressLint("SimpleDateFormat")
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

        // 自身のobjectIDの呼び出し
        val myObjectID = pref.getString("objectId", null)
        // ホメられたテーブルから新しいデータを格納
        var newReceiveData = listOf<NCMBObject>()

        // todo 確認画面に移す
        if (pref.getString("updateReceiveTableTime", null) == null) {
            editor.putString("updateReceiveTableTime", "2019-03-05T23:58:08.469+09:00").apply()
        }

        // OpenHelperの呼び出して、DB操作用のオブジェクトを用意
        val helper = ReceiveOpenHelper(applicationContext)

        // 【ニフクラ】e_serveの新しいデータを検索
        // テーブルを指定
        val query = NCMBQuery<NCMBObject>("e_serve")

        query.whereEqualTo("receiverId", myObjectID)
        // createDateが更新時より新しいデータだけを読み込み
        query.whereGreaterThan("createDate",pref.getString("updateReceiveTableTime", null).toDate())
        query.addOrderByAscending("ascendingKey");
        query.findInBackground { objs, error ->
            if (error == null && objs.size > 0){
                // 異常なエラーがなければ
                Log.d("[DEBUG]", "新しいホメられたデータを取得します")

                // 読み込みデータを格納
                newReceiveData = objs

                for (obj in newReceiveData) {
                    // NCMBFileを宣言
                    val imageName = obj.getString("questionId")
                    val fileName = NCMBFile("${imageName}.png")

                    // 質問画像をBITMAPへ変化してSQLiteに格納
                    fileName.fetchInBackground { imgData, error ->
                        Log.d("[DEBUG]", "ファイルストアにアクセスしました")
                        if (error == null) {
                            // DBのデータから時刻だけを取り除く
                            // 日付を"-"でパース
                            val splitDate = obj.getString("createDate").split("-")
                            //
                            val serveDate = splitDate[0] + "/" + splitDate[1] + "/" + splitDate[2].substring(0,2)

                            try {
                                // useを使うと使い終わったらDBを閉じてくれる
                                helper.use {

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
                                }
                            } catch (e: Exception) {
                                Log.e("[ERROR]",e.toString())
                            }

                        Log.d("[DEBUG]", "アプリ内DBに保存しました")

                        } else {
                            Log.e("[ERROR]",error.toString())
                        }
                    }
                }
                editor.putString("updateReceiveTableTime", df.format(Date())).apply()
            } else if(error != null) { // データストア検索に失敗した場合
                Log.e("[ERROR]", "褒められたテーブル検索失敗")
                Log.e("[ERROR]", error.toString())
            }
        }
    }

    private fun String.toDate(pattern: String = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"): Date? {
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



// リスト項目を保持 & 再利用するための入れ物
data class ViewHolder(
    val holderImageView: ImageView,
    val holderTitleLabel: TextView,
    val holderserver_user: TextView,
    val holderquestionPhraseLabel: TextView,
    val holderServeDate: TextView
)

class ReceiveAdapter (private val context: Context,
                        private val ReceiveData:List<ReceiveEntity>): BaseAdapter() {

    // inflater という謎の必須設定
    private var inflater : LayoutInflater? = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

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
                view.findViewById(R.id.questionPhraseLabel),
                view.findViewById(R.id.serveDate)
            )
            view.tag = viewHolder

        } else {
            viewHolder = view.tag as ViewHolder
        }

        // リストの情報を設定
        viewHolder.holderImageView.setImageBitmap(BitmapFactory.decodeByteArray(ReceiveData[position].stampImageBlob, 0, ReceiveData[position].stampImageBlob!!.size))
        viewHolder.holderTitleLabel.text = ReceiveData[position].questionTitle
        viewHolder.holderserver_user.text = "From " + ReceiveData[position].serverTitle
        viewHolder.holderquestionPhraseLabel.text = ReceiveData[position].questionPhrase
        viewHolder.holderServeDate.text = ReceiveData[position].serveDate

        return view!!
    }

    override fun getItem(position: Int) = ReceiveData[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = ReceiveData.size
}


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
                view.findViewById(R.id.questionPhraseLabel),
                view.findViewById(R.id.serveDate)
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