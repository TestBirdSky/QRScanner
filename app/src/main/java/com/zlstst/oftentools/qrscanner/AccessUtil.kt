package com.zlstst.oftentools.qrscanner

import android.app.Activity
import android.content.ClipData
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

class AccessUtil public constructor(val mActivity: Activity,val call:(PicFile?)->Unit) {
    fun startSystemContent(){
        val intent = Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        mActivity.startActivityForResult(intent, 5000);
    }


    fun uri2File(intent: Intent): PicFile? {
       val data =  intent.data
        if (data ==null){
            val clipData: ClipData? = intent.clipData
            if (clipData != null) {
                val itemCount: Int = clipData.itemCount;
                for (index in 0 until itemCount) {
                    val tmpUri: Uri? = clipData.getItemAt(index).uri
                    if (tmpUri!=null){
                        val ext =   getFileExt(tmpUri)
                        val file = uri2File(tmpUri,"cache_image_${System.currentTimeMillis()}${ext}")
                        if (file!=null){
                            return PicFile(file)
                        }
                    }
                }
            }
        }else{
            val ext =   getFileExt(data)
            val file = uri2File(data,"cache_image_${System.currentTimeMillis()}${ext}")
            if (file!=null){
                return PicFile(file)
            }
        }
        return null
    }

    fun getFileExt(fileUri: Uri): String {
        var ext :String?=null
        if (fileUri.scheme == ContentResolver.SCHEME_CONTENT){
            ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mActivity.contentResolver.getType(fileUri))
        }else{
            val tmpUri:Uri = Uri.fromFile(File(fileUri.path))
            ext = MimeTypeMap.getFileExtensionFromUrl("$tmpUri")
        }
        if (ext.isNullOrEmpty()){
            ext = "jpg"
        }
        return ".$ext"
    }

    fun uri2File(fileUri: Uri,fileName:String): File? {

        try {
            val rootFile =  mActivity.externalCacheDir
            val inputStream = mActivity.contentResolver.openInputStream(fileUri)

            if (inputStream!=null && rootFile!=null){
                val file = File(rootFile,fileName)
                val fos = FileOutputStream(file)
                val bis = BufferedInputStream(inputStream)
                val bos = BufferedOutputStream(fos)
                val byteArray = ByteArray(2048)
                var bytes = bis.read(byteArray)
                while (bytes > 0) {
                    bos.write(byteArray, 0, bytes)
                    bos.flush()
                    bytes = bis.read(byteArray)
                }
                bos.close()
                fos.close()

                return file
            }
        }catch (ex:Exception){
            ex.printStackTrace()
        }
        return  null
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (5000 == requestCode&&data!=null){
            val picFile = uri2File(data)
            call.invoke(picFile)
        }
    }
}