package com.example.myapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_select_picture.*
import android.content.DialogInterface
import android.content.Intent
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import com.nifcloud.mbaas.core.NCMB
import com.nifcloud.mbaas.core.NCMBFile
import com.nifcloud.mbaas.core.NCMBAcl
import android.graphics.BitmapFactory
import com.isseiaoki.simplecropview.CropImageView
import java.io.ByteArrayOutputStream
import com.example.myapplication.activity.RegisterSchoolsActivity

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SelectPicture : AppCompatActivity() {

    // ユーザ名
    private var userName = "defaultNm"

    // カメラステータス
    companion object {
        const val CAMERA_REQUEST_CODE = 1
        const val CAMERA_PERMISSION_REQUEST_CODE = 2
        const val CAMERA_ROLE_REQUEST_CODE = 3
        const val CAMERA_ROLE_PERMISSION_REQUEST_CODE = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_picture)

        // ニフクラ
        NCMB.initialize( applicationContext,
            "1115bda19d0575ef1b6650b35fbfaac587e5dd28bf61f23c9d03405052fa3be1",
            "ebf5c8d490aa0bc70fa7cc617f0b426422812c3ddccda0bc16de3c0088890de7")


        // Preferenceの設定
        val dataStore = getSharedPreferences("DataStore", Context.MODE_PRIVATE)

        // Preferenceの設定からのデータ受け取り
        userName = dataStore.getString("userName","")

        // 初期画像の設定
        cropImageView.imageBitmap = decodeSampledBitmapFromFile(R.drawable.noimage,Constants.CROP_VIEW_WIDTH,Constants.CROP_VIEW_HEIGHT)
        cropImageView.setCropMode(CropImageView.CropMode.CIRCLE)

        // パーミッションの取得
        grantCameraPermission()

        // プロフィール画像の設定ボタンを押下
        SetProfileImageBtn.setOnClickListener {
            showSelector()
        }

        // 次へボタン押下時
        moveToConfirmationBtn.setOnClickListener {
            savePicture()
            moveToConfirmation()
        }
    }

    // 画像のサイズを規定のサイズに合わせる
    // サイズを変えずに画面遷移するとメモリリークとなる
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {

        // 画像の元サイズ
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        Log.d("[DEBUG]","元画像のサイズ : $height * $width")

        // デフォルト画像サイズと要求画像サイズを比較
        if (height > reqHeight || width > reqWidth) { //　画像より大きかったらリサイズの大きさを決める
            if (width > height) {
                inSampleSize = Math.round(height.toFloat() / reqHeight.toFloat())
            } else {
                inSampleSize = Math.round(width.toFloat() / reqWidth.toFloat())
            }
        }
        return inSampleSize
    }

    private fun decodeSampledBitmapFromFile(id: Int, reqWidth: Int, reqHeight: Int): Bitmap {

        // inJustDecodeBounds=true で画像のサイズをチェック
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources,id,options)
        Log.d("[DEBUG]","画像の枠のサイズ : $reqHeight * $reqWidth")

        // inSampleSize を計算
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // inSampleSize をセットしてデコード
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(resources,id,options)

    }

    // 画像設定方法の選択
    private fun showSelector(){
        val items = arrayOf("画像を撮影する", "カメラロールから選択", "キャンセル")
        AlertDialog.Builder(this).apply {
            setItems(items, DialogInterface.OnClickListener { dialog, which ->
                if (which == 0) {
                    // 画像を撮影する押下時
                    takePicture()
                    Log.d("[DEBUG]", "画像を撮影する")
                } else if (which == 1) {
                    // 画像カメラロールから選択時
                    selectPicture()
                    Log.d("[DEBUG]", "カメラロールから選択")
                }
            })
            show()
        }
    }

    // 画像撮影
    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
        }
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    // カメラロールから選択
    private fun selectPicture() {

        // ファイル取得アプリへのintent生成
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        // photos or Gallary に限定
        intent.type = "image/*"

        // Chooserを起動する
        startActivityForResult(
            intent, CAMERA_ROLE_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // カメラでとった画像はget"data"のselecteImageに入ってる
            data?.extras?.get("data")?.let { selectedImage ->

                selectedImage as Bitmap
                //  画像はリサイズした上で表示
                cropImageView.imageBitmap =
                    Bitmap.createScaledBitmap(selectedImage,Constants.CROP_VIEW_WIDTH,Constants.CROP_VIEW_HEIGHT,true)
            }
        } else if (requestCode == CAMERA_ROLE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            data?.data.let { uri ->

                // 撮影した画像を画面に表示
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

                //  画像はリサイズした上で表示
                cropImageView.imageBitmap =
                    Bitmap.createScaledBitmap(bitmap,Constants.CROP_VIEW_WIDTH,Constants.CROP_VIEW_HEIGHT,true)
            }
        }
    }

    // カメラアプリのパーミッションを取得
    private fun grantCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), CAMERA_ROLE_PERMISSION_REQUEST_CODE)
    }

    // 写真(デフォルト)をDBに保存する
    private fun savePicture() {
        // 画像をニフクラへ保存
        // 画像の変換
        val byteArrayStream = ByteArrayOutputStream()
        cropImageView.croppedBitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayStream)
        val dataByte = byteArrayStream.toByteArray()

        //ACL 読み込み:可 , 書き込み:可
        val acl = NCMBAcl()
        acl.publicReadAccess = true
        acl.publicWriteAccess = true

        // 登録するファイル情報
        val file : NCMBFile = NCMBFile("${userName}_profile.png", dataByte, acl)
        file.saveInBackground { e ->
            if (e != null) {
                // エラー時の処理
                Log.d("[DEBUG]", "画像の登録に失敗しました")
            } else {
                // 登録成功時の処理
                Log.d("[DEBUG]", "画像の登録に成功")
            }

        }
    }

    // 次画面へ遷移する
    private fun moveToConfirmation () {

        // 次画面intentの生成
        val registSchoolsIntent = Intent(application, RegisterSchoolsActivity::class.java)

        registSchoolsIntent.putExtra(
            "checkInputBirthday",
            intent.getBooleanExtra("checkImputBirthday",true)
        )

        // 次画面遷移
        startActivity(registSchoolsIntent)
    }

}
