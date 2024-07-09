package com.zlstst.oftentools.qrscanner

import java.io.File

class PicFile(val file: File) {

    val path:String
    init {
        path = file.path

    }
}