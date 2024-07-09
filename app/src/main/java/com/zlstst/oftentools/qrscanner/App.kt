package com.zlstst.oftentools.qrscanner

import com.autumn.leaves.AppCool

class App : AppCool() {

 companion object {
     lateinit var app:App
 }
    override fun onCreate() {
        super.onCreate()

        app = this
    }


}