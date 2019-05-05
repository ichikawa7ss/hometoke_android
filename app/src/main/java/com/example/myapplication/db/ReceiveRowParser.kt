package com.example.myapplication.db

import com.example.myapplication.entity.ReceiveEntity
import org.jetbrains.anko.db.MapRowParser

class ReceiveRowParser: MapRowParser<ReceiveEntity> {

    override fun parseRow(columns: Map<String, Any?>): ReceiveEntity {
        return ReceiveEntity(
            (columns["_id"] as Long).toInt(),
            columns["questionTitle"] as String,
            columns["serverTitle"] as String,
            columns["questionPhrase"] as String,
            columns["serveDate"] as String,
            columns["stampImageBlob"] as ByteArray,
            columns["readFlg"] as String
        )
    }
}