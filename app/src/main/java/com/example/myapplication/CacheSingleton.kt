package com.example.myapplication

import android.content.Context
import com.nifcloud.mbaas.core.NCMBObject

class FriendData private constructor(context: Context) {
    companion object {
        private var _instance: FriendData? = null
        var friendInfo = listOf<NCMBObject>()


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
}

class CacheFriendsSingleton {

//    var fData = FriendData(friendInfo:[])
//
//    //友達キャッシュ用シングルトンを宣言
//    static let sharedFriend: CacheFriendsSingleton = CacheFriendsSingleton()
//    private override init() {}
//
//    // 友達データ（data）を追加
//    func addFriendData(data:NCMBObject) {
//        fData.friendInfo.append(data)
//    }
//
//    // 友達リストから全てのユーザーを取り出す
//    func getFriendList (parentObj: [NCMBObject]) -> [NCMBObject] {
//        // 返り値の配列を設定
//        let dataTemp = parentObj
//        var childObj : [NCMBObject] = []
//
//        // 友達リスト内の友達一人一人をチェック
//        for friend in dataTemp {
//            // フレンドIDを持つ友達データを取得
//            if let friendInfo:NCMBObject = friend.object(forKey: "friendId") as? NCMBObject {
//            childObj.append(friendInfo)
//        }
//        }
//        return childObj
//    }
//
//    // 引数なしで全ての友達を取得
//    func getFriendList () -> [NCMBObject] {
//        // 返り値の配列を設定
//        let dataTemp = self.fData.friendInfo
//        var childObj : [NCMBObject] = []
//
//        // 友達リスト内の友達一人一人をチェック
//        for friend in dataTemp {
//            // フレンドIDを持つ友達データを取得
//            if let friendInfo:NCMBObject = friend.object(forKey: "friendId") as? NCMBObject {
//            childObj.append(friendInfo)
//        }
//        }
//        return childObj
//    }
//
//
//    // 性別の検索条件に対して一致する友達のみを返す
//    func getSpecificGenderFriends (genderCondistion : String,data : [NCMBObject]) -> [NCMBObject] {
//        let dataTemp = data
//        // 返り値の配列を設定
//        var specificGenderFriend : [NCMBObject] = []
//
//        // 友達リスト内の友達一人一人をチェック
//        for friend in dataTemp {
//            // フレンドIDを持つ友達データを取得
//            if let friendInfo:NCMBObject = friend.object(forKey: "friendId") as? NCMBObject {
//            // 友達の性別を取得
//            let friendGender:String = friendInfo.object(forKey: "gender") as! String
//            // 質問属性が”すべて” もしくは友達の性別と同じなら返り値に追加
//            if (genderCondistion == "すべて" || genderCondistion == friendGender) {
//                specificGenderFriend.append(friendInfo)
//            }
//        }
//        }
//        return specificGenderFriend
//    }
}