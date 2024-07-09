package com.zlstst.oftentools.qrscanner

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import com.google.zxing.BarcodeFormat
import com.zlstst.oftentools.qrscanner.databinding.ActivityMakeValueBinding
import java.io.OutputStream

class MakeValueActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        var qrContent = ""
        var qrType = BarcodeFormat.QR_CODE

    }

    lateinit var bind: ActivityMakeValueBinding

    fun getBinding(): ActivityMakeValueBinding {
      return  ActivityMakeValueBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bind = getBinding()
        setContentView(bind.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bind.backBtn.setOnClickListener(this)
//        bind.copy.setOnClickListener(this)
//        bind.share.setOnClickListener(this)
        bind.save.setOnClickListener(this)



        val bitmap: Bitmap = QRCodeEncoder.syncEncodeQRCode(qrContent, BGAQRCodeUtil.dp2px(this, 180f));
        bind.qrIv.setImageBitmap(bitmap)
        saveBmp = bitmap;
    }

    var saveBmp:Bitmap?=null
    override fun onClick(v: View?) {
        when (v?.id) {
            bind.backBtn.id -> {
                finish()
            }
//            bind.copy.id -> {
//
//            }
//            bind.share.id -> {
//
//            }
            bind.save.id -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveBmp()
                }else{
                    per()
                }

            }
        }
    }

    fun saveBmp(){
        val tmpBmp = saveBmp
        tmpBmp?.let {
            saveBMP("QR_${System.currentTimeMillis()}", it)
            showTips(R.string.save_success)
        }
    }
    fun showTips(res:Int){
        Toast.makeText(this,getString(res), Toast.LENGTH_SHORT).show()
    }

    fun saveBMP(fileName:String,bitmap:Bitmap){


        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        } else {
            values.put(MediaStore.MediaColumns.DATA, "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_PICTURES}/$fileName")
        }

        val uri: Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
            }
        }
    }
    val permissionUtil = PermissionUtil(this) {
        saveBmp()
    }

    fun per() {
        if (!permissionUtil.isWritePermission()) {
            permissionUtil.requestWritePermission()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        permissionUtil.onActivityResult(requestCode, resultCode, data);
    }
}