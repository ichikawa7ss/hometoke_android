package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v4.widget.DrawerLayout
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.db.ReceiveOpenHelper
import com.example.myapplication.db.ReceiveRowParser
import com.example.myapplication.entity.ReceiveEntity
import com.nifcloud.mbaas.core.*
import kotlinx.android.synthetic.main.content_ndreceive.*
import org.jetbrains.anko.db.SqlOrderDirection
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule

class NDReceiveActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var myName = String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ndreceive)
        val toolbar: Toolbar = findViewById(R.id.toolbar_receive)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout_receive)
        val navView: NavigationView = findViewById(R.id.nav_view_receive)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        Log.d("[DEBUG]", "ほめられる画面を表示します")

        // ほめられる画面表示
        showReceiveView()

        navView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout_receive)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_serve -> {
                // ホメるActivityへの遷移

                // 次画面intentの生成
                val intentServe = Intent(application, NDServeActivity::class.java)
                // 次画面遷移
                startActivity(intentServe)
            }
            R.id.nav_receive -> {
                // ホメられるActivityへの遷移

                // 次画面intentの生成
                val intentReceive = Intent(application, NDReceiveActivity::class.java)
                // 次画面遷移
                startActivity(intentReceive)
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout_receive)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun showReceiveView() {
        NCMB.initialize(applicationContext, "1115bda19d0575ef1b6650b35fbfaac587e5dd28bf61f23c9d03405052fa3be1", "ebf5c8d490aa0bc70fa7cc617f0b426422812c3ddccda0bc16de3c0088890de7")

        myName = getSharedPreferences("DataStore", Context.MODE_PRIVATE).getString("userName","")

        // SQLiteのデータ読み込み・listView表示
        loadListView()
        Log.d("[DEBUG]", "ListViewを表示します！")


        // 30秒ごとにSQLiteを更新
        Timer().schedule(30000, 30000) {
            Log.d("[DEBUG]", "タイマー起動")
            uploadReceiveLocalRecord()
            Log.d("[DEBUG]", "テーブルを更新します!")

        }

        swipeRefreshLayout.setOnRefreshListener {
            loadListView()
            swipeRefreshLayout.isRefreshing = false
            Log.d("[DEBUG]", "スワイプしまーす！")
        }
    }

    override fun onResume() {
        super.onResume()
        loadListView()
    }

    // SQLiteからホメられたデータの読み込み
    // セルタップ時の動作を記述
    fun loadListView() {
        val helper = ReceiveOpenHelper(applicationContext)

        helper.use {
            val list = select(ReceiveOpenHelper.TABLE_NAME)
                .orderBy("_id", SqlOrderDirection.DESC)
                .parseList(ReceiveRowParser())

            listView.adapter = ReceiveAdapter(this@NDReceiveActivity, list)


            // リストがタップされたら手紙画面へ遷移するための設定
            listView.setOnItemClickListener { _, _, position, _ ->
                val intent = Intent(this@NDReceiveActivity, ReceiveDetailView::class.java)

                // タップされた position の情報を取得して加工
                val selectedImgId = list[position].stampImageBlob
                val selectedTitle = "「" + list[position].questionTitle + "」"
                val selectedServer = "From " + list[position].serverTitle
                val selectedUserName = "${myName}さん"
                val selectedQuestion = list[position].questionPhrase
                val selectedReadFlg = list[position].readFlg

                // intent = 画面間で渡される入れ物 に表示したい情報をセット
                intent.putExtra("intent_id", list[position]._id)
                intent.putExtra("intent_imgid", selectedImgId)
                intent.putExtra("intent_title", selectedTitle)
                intent.putExtra("intent_server", selectedServer)
                intent.putExtra("intent_user_name", selectedUserName)
                intent.putExtra("intent_question", selectedQuestion)
                intent.putExtra("intent_readFlg", selectedReadFlg)

                // intent を手紙画面へ
                startActivity(intent)
                Log.d("[DEBUG]", "手紙画面への遷移完了")
            }
        }
    }

    // ホメられたデータをSQLiteに書き込み
    // 更新されてないデータだけを検索して新規登録
    private fun uploadReceiveLocalRecord() {

        // preference,editer,dataFormatの用意
        val dataStore = getSharedPreferences("DataStore", Context.MODE_PRIVATE)
        val editor = dataStore.edit()

        @SuppressLint("SimpleDateFormat")
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

        // 自身のobjectIDの呼び出し
        val myObjectID = dataStore.getString("objectId", null)
        // ホメられたテーブルから新しいデータを格納
        var newReceiveData = listOf<NCMBObject>()

        // OpenHelperの呼び出して、DB操作用のオブジェクトを用意
        val helper = ReceiveOpenHelper(applicationContext)

        // 【ニフクラ】e_serveの新しいデータを検索
        // テーブルを指定
        val query = NCMBQuery<NCMBObject>("e_serve")

        query.whereEqualTo("receiverId", myObjectID)

        // createDateが更新時より新しいデータだけを読み込み
        query.whereGreaterThan("createDate",dataStore.getString("updateReceiveTableTime", null).toDate())
        query.addOrderByAscending("ascendingKey")
        query.findInBackground { objs, error ->
            if (error == null && objs.size > 0){
                // 異常なエラーがなければ
                Log.d("[DEBUG]", "新しいホメられたデータを取得します")

                // 読み込みデータを格納
                newReceiveData = objs

                for (obj in newReceiveData) {
                    // NCMBFileを宣言
                    val imageName = obj.getString("questionId")
                    val fileName = NCMBFile("$imageName.png")

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

// 既読のリスト
data class ViewHolder(
    val holderImageView: ImageView,
    val holderTitleLabel: TextView,
    val holderserver_user: TextView,
    val holderquestionPhraseLabel: TextView,
    val holderServeDate: TextView
)

// 未読のリスト
data class UnreadViewHolder(
    val holderserver_user: TextView,
    val holderServeDate: TextView
)


class ReceiveAdapter (
    context: Context,
    private val ReceiveData:List<ReceiveEntity>): BaseAdapter() {

    // inflater という謎の必須設定
    private var inflater : LayoutInflater? = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder : ViewHolder?
        val unreadViewHolder: UnreadViewHolder?

        var view = convertView

        if (getItemViewType(position) == Constants.READ_CELL) {
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
        } else if (getItemViewType(position) == Constants.UNREAD_CELL) {
            // ViewHolder に値があれば再利用して表示
            // なければ新たに取得し、ViewHolder へ格納
            if (view == null) {

                view = inflater!!.inflate(R.layout.item_unread_cell, parent, false)
                unreadViewHolder = UnreadViewHolder(
                    view.findViewById(R.id.unreadServerUser),
                    view.findViewById(R.id.unreadServeDate)
                )
                view.tag = unreadViewHolder

            } else {
                unreadViewHolder = view.tag as UnreadViewHolder
            }

            // リストの情報を設定
            unreadViewHolder.holderserver_user.text = "From " + ReceiveData[position].serverTitle
            unreadViewHolder.holderServeDate.text = ReceiveData[position].serveDate

        }
        return view!!
    }

    override fun getItem(position: Int) = ReceiveData[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = ReceiveData.size

//    override fun getViewTypeCount() = 2

    override fun getItemViewType(position: Int): Int {
        return if (ReceiveData[position].readFlg == "0") {
            Constants.UNREAD_CELL
        } else {
            Constants.READ_CELL
        }
    }
}
