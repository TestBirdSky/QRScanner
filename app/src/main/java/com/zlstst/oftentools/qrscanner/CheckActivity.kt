package com.zlstst.oftentools.qrscanner

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.bingoogolapple.qrcode.core.QRCodeView
import cn.bingoogolapple.qrcode.core.ScanResult
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder
import com.google.zxing.BarcodeFormat
import com.zlstst.oftentools.qrscanner.databinding.ActivityCheckBinding

class CheckActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var bind: ActivityCheckBinding

    fun getBinding(): ActivityCheckBinding {
      return  ActivityCheckBinding.inflate(layoutInflater)
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
        bind.closeBtn.setOnClickListener(this)
        bind.selectorBtn.setOnClickListener(this)

        bind.zxingview.setDelegate(object : QRCodeView.Delegate {
            override fun onScanQRCodeSuccess(result: ScanResult?) {
                if (result!=null){
                    start(result.result,result.format)
                }
            }

            override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {

            }

            override fun onScanQRCodeOpenCameraError() {

            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            bind.backBtn.id -> {
                finish()
            }
            bind.closeBtn.id -> {
                if (isFlaOpen){
                    bind.zxingview.closeFlashlight(); // 关闭闪光灯
                    bind.closeBtn.setImageResource(R.mipmap.ic_check_close)
                }else{
                    bind.      zxingview.openFlashlight(); // 打开闪光灯
                    bind.closeBtn.setImageResource(R.mipmap.ic_check_open)
                }
                isFlaOpen = isFlaOpen.not()
            }
            bind.selectorBtn.id -> {
                accessUtil.startSystemContent()
            }
        }
    }
    var isFlaOpen = false

    override fun onStart() {
        super.onStart()
        bind.zxingview.startCamera()
        bind.zxingview.startSpotAndShowRect()
    }

    override fun onStop() {
        bind.zxingview.stopCamera()
        super.onStop()
    }

    override fun onDestroy() {
        bind.zxingview.onDestroy()
        super.onDestroy()
    }

    val accessUtil :AccessUtil = AccessUtil(this) { picFile: PicFile? ->
        if (picFile!=null){
            val result =  QRCodeDecoder.syncDecodeQRCode(picFile.path)
            if (null!=result){
                start(result.text,result.barcodeFormat)
            }else{
                showTips(R.string.text_dest2)
            }
        }else{

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        accessUtil.onActivityResult(requestCode,resultCode,data)
    }

    fun start(content:String, format: BarcodeFormat){
        CheckValueActivity.qrContent = content
        CheckValueActivity.qrType = format
        startActivity(Intent(this,CheckValueActivity::class.java))
        finish()
    }
    fun showTips(res:Int){
        Toast.makeText(this,getString(res), Toast.LENGTH_SHORT).show()
    }
}