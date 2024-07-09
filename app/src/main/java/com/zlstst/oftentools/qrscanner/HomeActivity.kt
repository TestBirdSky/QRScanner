package com.zlstst.oftentools.qrscanner

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.zlstst.oftentools.qrscanner.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var bind: ActivityHomeBinding

    fun getBinding(): ActivityHomeBinding {
        return ActivityHomeBinding.inflate(layoutInflater)
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


        bind.settingsBtn.setOnClickListener(this)
        bind.checkBtn.setOnClickListener(this)
        bind.makeBtn.setOnClickListener(this)
    }

    val permissionUtil = PermissionUtil(this) {
        startActivity(Intent(this, CheckActivity::class.java))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            bind.settingsBtn.id -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            bind.checkBtn.id -> {
                per()
            }
            bind.makeBtn.id -> {
                startActivity(Intent(this, MakeActivity::class.java))
            }
        }
    }

    fun per() {
        if (!permissionUtil.isCameraPermission()) {
            permissionUtil.requestCameraPermission()
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