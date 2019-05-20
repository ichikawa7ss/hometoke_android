package com.example.myapplication

import android.content.Context
import com.nifcloud.mbaas.core.NCMBObject

class FriendData private constructor(context: Context) {
    var friendList = mutableListOf<NCMBObject>()

    // kotlinでのシングルトンの実装が以下らしい
    companion object {
        private var _instance: FriendData? = null

        fun onCreateApplication(applicationContext: Context) {
            // Application#onCreateのタイミングでシングルトンが生成される
            _instance = FriendData(applicationContext)
        }

        val instance: FriendData
            get() {
                _instance?.let {
                    return it
                } ?: run {
                    // nullにはならないはず
                    throw RuntimeException("CacheSingleton should be initialized!")
                }
            }
    }


    // 友達データ（data）を追加
    fun addFriendData(data: NCMBObject) {
        friendList.add(data)
    }

    // 友達リストから全てのユーザーを取り出す
    fun getFriendList(parentObj: Collection<NCMBObject>): Collection<NCMBObject> {
        // 返り値の配列を設定
        val dataTemp = parentObj
        var childObj = mutableListOf<NCMBObject>()

        // 友達リスト内の友達一人一人をチェック
        for (friend in parentObj) {

            // フレンドIDを持つ友達データを取得
            if (!friend.getJSONObject("friendId").isNull("objectId")) {
                childObj.add(friend)
            }
        }
        return childObj
    }

    // 引数なしで全ての友達を取得
    fun getFriendList(): Collection<NCMBObject> {
        // 返り値の配列を設定
        val dataTemp = friendList
        var childObj = mutableListOf<NCMBObject>()

        // 友達リスト内の友達一人一人をチェック
        for (friend in dataTemp) {
            // フレンドIDを持つ友達データを取得
            if (!friend.getJSONObject("friendId").isNull("objectId")) {
                childObj.add(friend)
            }
        }
        return childObj
    }


    // 性別の検索条件に対して一致する友達のみを返す
    fun getSpecificGenderFriends(genderCondistion: String, data: Collection<NCMBObject>): Collection<NCMBObject> {
        val dataTemp = data
        // 返り値の配列を設定
        var specificGenderFriend = mutableListOf<NCMBObject>()

        // 友達リスト内の友達一人一人をチェック
        for (friend in dataTemp) {
            // フレンドIDを持つ友達データを取得
            if (!friend.getJSONObject("friendId").isNull("objectId")) {
                // 友達の性別を取得
                val friendGender: String = friend.getString("gender")
                // 質問属性が”すべて” もしくは友達の性別と同じなら返り値に追加
                if (genderCondistion == "すべて" || genderCondistion == friendGender) {
                    specificGenderFriend.add(friend)
                }
            }
        }
        return specificGenderFriend
    }
}