package com.example.myapplication.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Filterable
import android.widget.ListView
import android.widget.SearchView
import com.example.myapplication.R
import com.example.myapplication.utils.SearchFilter
import java.io.InputStream
import java.io.InputStreamReader
import java.io.BufferedReader
import java.io.IOException

/* 学校検索画面のActivity*/
@Suppress("CAST_NEVER_SUCCEEDS", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SearchSchoolActivity : AppCompatActivity() {
    var school: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serch_school)

        val intent = intent
        val schoolType = intent.extras.getInt(ResisterSchoolsActivity.EXTRA_SCHOOL_TYPE)

        // SearchView
        val searchSchools: SearchView = findViewById(R.id.search_school)
        // ListView
        val schoolList: ListView = this.findViewById(R.id.school_list)

        // adapterを作成
        val adapter = SearchFilter(this, android.R.layout.simple_list_item_1, readCSV(schoolType))
        schoolList.adapter = adapter
        schoolList.isTextFilterEnabled = true

        //val searchFilter = SearchFilter(this, android.R.layout.simple_list_item_1, readCSV(schoolType))

        // SearchViewの初期表示状態を設定
        searchSchools.setIconifiedByDefault(false)

        // filterの作成
        //val filter = adapter.filter
        val filter = (schoolList.adapter as Filterable).filter
        // 初期状態では何も表示しない
        //TODO 流石に雑なので機会があれば修正をする
        filter.filter("____")


        // SearchViewにOnQueryChangeListenerを設定
        searchSchools.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // テキスト変更時の処理
            override fun onQueryTextChange(searchWord: String?): Boolean {
                if (searchWord == null || searchWord.isEmpty()) {
                    // 検索欄が未入力の場合、何も表示しない
                    //TODO 流石に雑なので機会があれば修正をする
                    filter.filter("____")
                } else {
                    // 検索語句でフィルターをかける
                    filter.filter(searchWord)
                }
                return true
            }

            // 検索ボタン押下時の処理（何もしない）
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
        })

        // SearchViewのSubmitボタンを使用不可にする
        searchSchools.isSubmitButtonEnabled = true

        // SearchViewに何も入力していない時のテキストを設定
        searchSchools.setQueryHint("学校名を入力して下さい。");

        // リストの項目を選択した際の処理
        schoolList.setOnItemClickListener { parent, view, pos, id ->
            val list: ListView = parent as ListView
            school = list.getItemAtPosition(pos) as String

            // 選択された学校名をPreferenceに保存
            saveSchoolName(schoolType, school)

            // 選択された学校名をResisterSchoolsActivityに渡す
            val intentSchSearch = Intent()
            intentSchSearch.putExtra(ResisterSchoolsActivity.EXTRA_SCHOOL_TYPE, school)
            setResult(Activity.RESULT_OK, intentSchSearch)

            // Activityを終了し、ResisterSchoolsActivityに遷移
            finish()
        }
    }


    // CSVの読み込み処理
    fun readCSV(schoolType: Int): MutableList<String> {
        var csvPath = ""
        // 各学校種別のCSVファイルを選択
        when(schoolType) {
            1000 -> {
                // 小学校
                csvPath = "ElementarySchool.csv"
            }
            1001 -> {
                // 中学校
                csvPath = "JuniorHighSchool.csv"
            }
            1002 -> {
                // 高等学校
                csvPath = "HighSchool.csv"
            }
        }

        // 学校リストを入れる配列
        val schoolList: MutableList<String> = mutableListOf()

        try {
            // assetsのCSVファイルを読み込み
            val assetManager: AssetManager = resources.assets
            val inputStream: InputStream = assetManager.open(csvPath)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferReader = BufferedReader(inputStreamReader)

            // CSVファルから配列に入れ直す
            while ((bufferReader.readLine()) != null) {
                // リストを1行ずつ読み込む
                val line: String = bufferReader.readLine()
                // 各学校名を配列に入れる
                schoolList.add(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return schoolList
    }

    // 学校名をPreferenceに保存する
    fun saveSchoolName(schoolType: Int, schoolName: String) {
        // Preferenceの設定
        val dataStore: SharedPreferences = getSharedPreferences("DataStore", Context.MODE_PRIVATE)
        val editor = dataStore.edit()
        // 各学校名をPreferenceに保存
        when (schoolType) {
            1000 -> {
                // 小学校
                editor.putString("elementarySchool", schoolName)
            }

            1001 -> {
                // 中学校
                editor.putString("juniorHighSchool", schoolName)
            }

            1002 -> {
                // 高等学校
                editor.putString("highSchool", schoolName)
            }
        }
        editor.apply()
    }
}
