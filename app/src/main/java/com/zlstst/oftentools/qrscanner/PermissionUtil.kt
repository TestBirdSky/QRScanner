package com.zlstst.oftentools.qrscanner

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.SystemClock
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.zlstst.oftentools.qrscanner.databinding.DialogCameraBinding

class PermissionUtil public constructor(val mActivity: Activity,val call:()->Unit){
    fun isCameraPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            call.invoke()
            return true
        }else{
            return false
        }
    }

    fun requestCameraPermission(){
        permissionCheckTag = SystemClock.uptimeMillis()
        ActivityCompat.requestPermissions(mActivity, arrayOf(Manifest.permission.CAMERA),2000)
    }
    fun showCameraDialog(){

        val dialog = Dialog(mActivity);
        val bind = DialogCameraBinding.inflate(mActivity.layoutInflater)
        dialog.setContentView(bind.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bind.closeBtn.setOnClickListener { dialog.dismiss() }
        bind.confirmBtn.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.setData(Uri.parse("package:${mActivity.packageName}"))
            mActivity.startActivityForResult(intent, 2001)
        }

        dialog.show()
    }



    var permissionCheckTag:Long = 100L
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray){
        if (2000 == requestCode){
            if (SystemClock.uptimeMillis()-500<permissionCheckTag){
                showCameraDialog()
            }else{
                if ( isCameraPermission()){

                }
            }
        }else if (3000 == requestCode){
            if (SystemClock.uptimeMillis()-500<permissionCheckTag){
                showWriteDialog()
            }else{
                if ( isWritePermission()){

                }
            }
        }
    }
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (2001 == requestCode){
           if ( isCameraPermission()){
           }
        }else if (3001 == requestCode){
            if ( isWritePermission()){
            }
        }
    }


    fun isWritePermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            call.invoke()
            return true
        }else{
            return false
        }
    }

    fun requestWritePermission(){
        permissionCheckTag = SystemClock.uptimeMillis()
        ActivityCompat.requestPermissions(mActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),3000)
    }

    fun showWriteDialog(){

        val dialog = Dialog(mActivity);
        val bind = DialogCameraBinding.inflate(mActivity.layoutInflater)
        dialog.setContentView(bind.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bind.closeBtn.setOnClickListener { dialog.dismiss() }
        bind.confirmBtn.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.setData(Uri.parse("package:${mActivity.packageName}"))
            mActivity.startActivityForResult(intent, 3001)
        }

        dialog.show()
    }
}