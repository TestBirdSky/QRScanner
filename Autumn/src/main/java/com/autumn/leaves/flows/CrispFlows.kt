package com.autumn.leaves.flows

import android.app.ActivityManager
import android.app.job.JobService
import android.content.Context
import android.content.pm.PackageInfo
import com.autumn.leaves.AppleCache
import com.autumn.leaves.WindHelper
import com.autumn.leaves.mApp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

/**
 * Date：2024/6/28
 * Describe:
 */
object CrispFlows {
    var curCrispLevel by AppleCache(defApple = "Spring", nameP = "Crisp_")
    var mNetworkStr by AppleCache(defApple = "")

    val listStr = arrayListOf("bytedance", "adjust", "not%20set", "not set")
    var isStop = false

    val globalFlow by lazy { MutableStateFlow("") }


    val flowLevel: Flow<String> by lazy {
        flow {
            delay(500)
            while (isStop.not()) {
                WindHelper.log("curCrispLevel_$curCrispLevel")
                if (WindHelper.isFlowWaitFinish()) {
                    emit("wait_finish")
                    delay(1)
                }
                emit(curCrispLevel)
                delay(4000)
            }

        }
    }

    fun autumnUser(): Boolean {
        if (mNetworkStr.isBlank()) return false
        return mNetworkStr.contains("organic", true).not()
    }

    fun packInfoWindow(): PackageInfo {
        return mApp.packageManager.getPackageInfo(
            mApp.packageName, 0
        )
    }

    // 外弹
    fun crispEvent(context: Context, type: String) {

    }


    fun getCueStatus(context: Context): String { //这个函数可以放到其他文件减少关联
        val am: ActivityManager =
            context.getSystemService(JobService.ACTIVITY_SERVICE) as ActivityManager
        val list0: List<ActivityManager.RunningAppProcessInfo> = am.runningAppProcesses
        for (info in list0) {
            if (!info.processName.equals(context.applicationInfo.processName) || info.importance != 100) {
                continue
            }
            return "true"
        }
        return "H"
    }

}