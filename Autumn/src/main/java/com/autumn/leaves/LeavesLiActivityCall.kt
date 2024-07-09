package com.autumn.leaves

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle

/**
 * Date：2024/7/5
 * Describe:
 */
class LeavesLiActivityCall(private val leaversCache: LeaversCache) :
    Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        WindHelper.log("onActivityCreated-->$activity")
        leaversCache.leavers.add(activity)
        leaversCache.checkAllow(activity) { setStyle(activity) }
    }

    override fun onActivityStarted(activity: Activity) {
        WindHelper.log("onActivityStarted-->$activity")
        leaversCache.num++
        leaversCache.numJump()
    }

    override fun onActivityResumed(activity: Activity) {
        WindHelper.log("onActivityResumed-->$activity")
        leaversCache.adjSet(true)
    }

    override fun onActivityPaused(activity: Activity) {
        leaversCache.adjSet(false)
    }

    override fun onActivityStopped(activity: Activity) {
        WindHelper.log("onActivityStopped-->$activity")
        leaversCache.num--
        leaversCache.numJump()

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        WindHelper.log("onActivityDestroyed-->$activity")
        leaversCache.leavers.remove(activity)
    }

    private fun setStyle(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.setTranslucent(true)
        } else {
            activity.window.setBackgroundDrawableResource(R.color.color_ra)
        }
    }
}