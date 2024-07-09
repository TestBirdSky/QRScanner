package com.autumn.leaves

import android.app.Application

/**
 * Date：2024/6/28
 * Describe:
 */
abstract class AppCool : Application() {
    private val appCoolImpl by lazy { AppCoolImpl(this) }

    override fun onCreate() {
        super.onCreate()
        mApp = this
        val isCool = appCoolImpl.spInit()
        if (isCool) {
            registerActivityLifecycleCallbacks(LeavesLiActivityCall(appCoolImpl.getLeaversCache))
        }
    }

}