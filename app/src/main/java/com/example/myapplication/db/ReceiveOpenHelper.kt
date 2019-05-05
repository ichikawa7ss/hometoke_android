package com.example.myapplication.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class ReceiveOpenHelper (context: Context): ManagedSQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // DBとテーブルを作成
        db?.createTable(
            TABLE_NAME,
            true,
            "_id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            "questionTitle" to TEXT + NOT_NULL,
            "serverTitle" to TEXT + NOT_NULL,
            "questionPhrase" to TEXT + NOT_NULL,
            "serveDate" to TEXT + NOT_NULL,
            "stampImageBlob" to BLOB,
            "readFlg" to TEXT
        )

    }


    // 定数などを準備（インスタンス化せずに、”クラス名.定数名”で利用できる定数）
    companion object {
        private val DB_NAME = "ReceiveEntity"
        private val DB_VERSION = 1
        val TABLE_NAME = "Receive"

        // 自分自身のオブジェクトを保持する変数（オブジェクトを複数作らないように、ここで一つに制限）
        private var instance: ReceiveOpenHelper? = null

        // 自分自身のオブジェクトを保持する変数（オブジェクト）
        @Synchronized
        fun getInstance(ctx: Context) = instance ?: ReceiveOpenHelper(ctx.applicationContext)

    }

}