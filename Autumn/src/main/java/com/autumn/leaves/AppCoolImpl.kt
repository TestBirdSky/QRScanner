package com.autumn.leaves

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.webkit.WebView
import com.autumn.leaves.flows.AutumnServiceHelper
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * Date：2024/6/28
 * Describe:
 */
class AppCoolImpl(private val context: Context) {
    private var isAppCool = false

    val getLeaversCache by lazy { LeaversCache(context) }

    private val flowApp: Flow<String> = flow {
        processInit(context)
        val name = getCoolName()
        if (name == context.packageName) {
            emit("data_init")
            leavesFirebaseAndG.leavesInit()
            emit("cool")
        } else {
            isAppCool = false
            emit("wind")
        }
        emit("adjust_init")
    }

    private val leavesFirebaseAndG by lazy { LeavesFirebaseAndG() }
    private var mServiceHelper: AutumnServiceHelper? = null

    init {
        isAppCool = getCoolName() == context.packageName
        CoroutineScope(Dispatchers.Default).launch {
            flowApp.collect {
                WindHelper.log("flowApp-->$it")
                if (it == "cool") {
                    isAppCool = true
                    runCatching {
                        Firebase.initialize(context)
                    }
                    leavesFirebaseAndG.registerFlow()
                    getLeaversCache.initTrad()
                } else if (it == "adjust_init") {
                    if (isAppCool) {
                        leavesFirebaseAndG.getAdjStr(context)
                        CozyApple(context, getLeaversCache).registerCozy()
                    }
                } else if (it == "data_init") {
                    isAppCool = true
                    WindHelper.mOpStr =
                        (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkOperator
                    mServiceHelper = AutumnServiceHelper(getLeaversCache)
                } else if (it == "wind") {
                    isAppCool = false
                }
            }
        }
    }

    fun spInit(): Boolean {
        Hawk.init(context).build()
        WindHelper.initData(context)
        return isAppCool
    }

    private fun processInit(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName: String = Application.getProcessName()
            if (!context.packageName.equals(processName)) {
                WebView.setDataDirectorySuffix(processName)
            }
        }
    }

    private fun getCoolName(): String {
        runCatching {
            val am = context.getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager
            val runningApps = am.runningAppProcesses ?: return ""
            for (info in runningApps) {
                when (info.pid) {
                    android.os.Process.myPid() -> return info.processName
                }
            }
        }
        return ""
    }


}