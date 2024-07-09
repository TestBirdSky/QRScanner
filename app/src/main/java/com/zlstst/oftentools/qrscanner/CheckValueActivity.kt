package com.zlstst.oftentools.qrscanner

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.zxing.BarcodeFormat
import com.zlstst.oftentools.qrscanner.databinding.ActivityCheckValueBinding

class CheckValueActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        var qrContent = ""
        var qrType = BarcodeFormat.QR_CODE

    }


    lateinit var bind: ActivityCheckValueBinding

    fun getBinding(): ActivityCheckValueBinding {
      return  ActivityCheckValueBinding.inflate(layoutInflater)
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

        bind.contentTv.text = qrContent

        bind.backBtn.setOnClickListener(this)
        bind.copy.setOnClickListener(this)
//        bind.share.setOnClickListener(this)
        bind.search.setOnClickListener(this)


        bind.contentTv.movementMethod = ScrollingMovementMethod()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            bind.backBtn.id -> {
                finish()
            }
            bind.copy.id -> {
                val clipboardManager = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("label", qrContent)
                clipboardManager.setPrimaryClip(clipData)
                showTips(R.string.copy_success)
            }
//            bind.share.id -> {
//
//            }
            bind.search.id -> {
                val intent = Intent()
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.google.com/search?q=${qrContent}"));
                startActivity(intent);
            }
        }
    }

    fun showTips(res:Int){
        Toast.makeText(this,getString(res), Toast.LENGTH_SHORT).show()
    }
}