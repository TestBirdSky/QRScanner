package com.zlstst.oftentools.qrscanner

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.zxing.BarcodeFormat
import com.zlstst.oftentools.qrscanner.databinding.ActivityMakeBinding

class MakeActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var bind: ActivityMakeBinding

    fun getBinding(): ActivityMakeBinding {
      return  ActivityMakeBinding.inflate(layoutInflater)
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
        bind.makeBtn.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            bind.backBtn.id -> {
                finish()
            }
            bind.makeBtn.id -> {
                checkContent()

            }
        }
    }

    fun checkContent(){
             val text:String =   bind.contentEt.text.toString().trim()
             if (   text.isEmpty()){
                 showTips()
             }else{
                 start(text,BarcodeFormat.QR_CODE)
             }
    }

    fun showTips(){

        Toast.makeText(this,getString(R.string.text_dest1),Toast.LENGTH_SHORT).show()
    }

    fun start(content:String, format: BarcodeFormat){
        MakeValueActivity.qrContent = content
        MakeValueActivity.qrType = format
        startActivity(Intent(this,MakeValueActivity::class.java))
        finish()
    }
}