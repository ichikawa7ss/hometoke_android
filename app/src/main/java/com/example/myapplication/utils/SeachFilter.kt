package com.example.myapplication.utils

import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Filter
import kotlin.collections.ArrayList

/* 部分一致検索を可能にするため、ArrayAdapterクラスを拡張*/
class SearchFilter(context: Context, resource: Int, schoolList: MutableList<String>) : ArrayAdapter<String>(context, resource, schoolList) {
    // フィルターを初期化
    private var filter: SearchFilter? = null
    // フィルター適用前のリストを保持するリスト
    private var mOriginalValues: ArrayList<String>? = null //schoolList as ArrayList<String>
    private val mLock = Any()
    // 検索対象リスト
    private var mObjects: MutableList<String>? = schoolList

    // getFilterでSearchFilterを実行するように設定
    override fun getFilter(): Filter {
        if (filter == null) {
            filter = SearchFilter()
        }
        return filter as SearchFilter
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    inner class SearchFilter : Filter() {
        // Filiterの処理
        override fun performFiltering(prefix: CharSequence?): FilterResults {
            // フィルタリング結果を格納
            val results = FilterResults()

            if (mOriginalValues == null) {
                synchronized(mLock) {
                    // フィルター適用前のリストを保管
                    mOriginalValues = ArrayList(mObjects)
                }
            }

            if (prefix == null || prefix.isEmpty()) {
                // 検索文字列がなかった場合
                val list: java.util.ArrayList<String>
                synchronized(mLock) {
                    list = ArrayList(mOriginalValues)
                }
                results.values = list
                results.count = list.size
            } else {
                // 険悪文字列が指定された場合
                val prefixString = prefix.toString().toLowerCase()

                val values: ArrayList<String>
                synchronized(mLock) {
                    values = ArrayList(mOriginalValues)
                }

                val count = values.size
                val newValues = java.util.ArrayList<String>()

                for (i in 0 until count) {
                    val value = values[i]
                    val valueText = value.toLowerCase()

                    // First match against the whole, non-splitted value
                    if (valueText.indexOf(prefixString) != -1) {
                        newValues.add(value)
                    }
                }

                results.values = newValues
                results.count = newValues.size
            }

            return results
        }

        // フィルタリング後のデータを作成し、Activityに返却
        override fun publishResults(constraint: CharSequence, results: FilterResults?) {
            // Adapterのメソッドでデータの内容を更新する
            if (results != null) {
                mObjects = results.values as? MutableList<String>
                Log.d("tag", mObjects.toString())
                clear()
                addAll(mObjects)
            }
            if (results != null) {
                if (results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }
}