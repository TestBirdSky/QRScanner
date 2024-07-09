package com.zlstst.oftentools.qrscanner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.zlstst.oftentools.qrscanner.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var bind: ActivitySettingsBinding

    fun getBinding(): ActivitySettingsBinding {
      return  ActivitySettingsBinding.inflate(layoutInflater)
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

        bind.privacy.setOnClickListener(this)
        bind.backBtn.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            bind.backBtn.id -> {
                finish()
            }
            bind.privacy.id -> {
                val intent = Intent()
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.baidu.com/"));
                startActivity(intent);
            }
        }
    }
}