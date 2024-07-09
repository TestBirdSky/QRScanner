package com.autumn.leaves

import android.content.Context
import android.os.Build
import android.util.Base64
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.android.installreferrer.api.ReferrerDetails
import com.autumn.leaves.flows.CrispFlows
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * Date：2024/6/28
 * Describe:
 */
class LeavesFirebaseAndG : BaseNetRequest() {
    private var curUnit = 1000L
    private val timeOne = 60000L
    private var lastRefreshTime = 0L
    private var mCacheJsonInstall by AppleCache()

    private val flowStatus: Flow<String> = flow {
        while (true) {
            if (System.currentTimeMillis() - lastRefreshTime > timeOne * 50) {
                emit("refresh")
            }
            if (WindHelper.mReferrer.isBlank()) {
                emit("refreshReferrer")
            }
            if (WindHelper.mCloakInfo.isBlank()) {
                emit("refreshCloak")
            }
            delay(15 * curUnit)
        }
    }

    fun registerFlow() {
        CoroutineScope(Dispatchers.IO).launch {
            if (WindHelper.mCloakInfo.isNotBlank() && WindHelper.mReferrer.isNotBlank()) {
                curUnit = timeOne
            }
            flowStatus.collect {
                WindHelper.log("registerFlow-->refresh")
                when (it) {
                    "refresh" -> leavesInit(true)
                    "refreshReferrer" -> {
                        installRequest { refStr ->
                            WindHelper.mReferrer = refStr
                            //todo delete
                            if (IS_TEST) {
                                WindHelper.log("mGoogleReferStr-->${WindHelper.mReferrer}")
                                WindHelper.mReferrer += "not%20set"
                            }
                            if (WindHelper.mCloakInfo.isNotBlank() && WindHelper.mReferrer.isNotBlank()) {
                                curUnit = timeOne
                            }
                        }
                    }

                    "refreshCloak" -> {
                        request(null, success = { claStr ->
                            WindHelper.mCloakInfo = claStr
                            if (WindHelper.mCloakInfo.isNotBlank() && WindHelper.mReferrer.isNotBlank()) {
                                curUnit = timeOne
                            }
                        })
                    }
                }
            }
        }
    }

    fun leavesInit(isRefresh: Boolean = false) {
        WindHelper.log("leavesInit--->$isRefresh")
        runCatching {
            lastRefreshTime = System.currentTimeMillis()
            Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener {
                if (it.isSuccessful) {
                    refreshStr(Firebase.remoteConfig.getString("autumn_cool"))
                }
            }
        }
        if (isRefresh.not()) {
            runCatching {
                refreshStr(Firebase.remoteConfig.getString("autumn_cool"))
            }
            // todo del
            if (IS_TEST) {
                refreshStr(TEST_C)
            }
        }
    }

    private fun refreshStr(srt: String) {
        if (srt.isBlank()) return
        val bsS = String(Base64.decode(srt, Base64.DEFAULT))
        runCatching {
            val js = JSONObject(bsS)
            // todo
            WindHelper.mAdId = js.optString("", "")
            WindHelper.windStatus = js.optString("autumn_time", "gg")
            val s = js.optString("cool", "30-30-1")
            if (s.contains("-")) {
                refreshTime(s)
            }
            val ll = js.optString("leaves", "")
            if (ll.isNotBlank()) {
                val li = ll.split("-")
                CrispFlows.listStr.clear()
                CrispFlows.listStr.addAll(li)
                if (li.contains("not%20set")) {
                    CrispFlows.listStr.add("not set")
                }
            }
        }
    }

    private fun refreshTime(s: String) {
        runCatching {
            val lis = s.split("-")
            WindHelper.refreshSu(lis[0].toInt() * 1000L, lis[2].toInt() * 1000L)
            WindHelper.timeShowPAutumn = lis[1].toInt() * 1000L
        }
    }

    override fun createUrl(): String {
        TODO("Not yet implemented")
    }

    override fun createInstJson(referrerDetails: ReferrerDetails) {
        val js = getCommonBody(mApp)
        // todo
        JSONObject().apply {
            put("", "build/${Build.ID}")
            put("", referrerDetails.installReferrer)
            put("", referrerDetails.installVersion)
            put("", "")
            put("", referrerDetails.referrerClickTimestampSeconds)
            put("", referrerDetails.installBeginTimestampSeconds)
            put("", referrerDetails.referrerClickTimestampServerSeconds)
            put("", referrerDetails.installBeginTimestampServerSeconds)
            put("", CrispFlows.packInfoWindow().firstInstallTime)
            put("", CrispFlows.packInfoWindow().lastUpdateTime)
            mCacheJsonInstall = this.toString()
        }
        request(js, success = {
            mCacheJsonInstall = ""
        })
    }

    fun getAdjStr(context: Context) {
        // todo modify
        val environment = AdjustConfig.ENVIRONMENT_SANDBOX
//        if (BuildConfig.DEBUG) AdjustConfig.ENVIRONMENT_SANDBOX else AdjustConfig.ENVIRONMENT_PRODUCTION
        // todo modify adjust key
        val config = AdjustConfig(context, "ih2pm2dr3k74", environment)

        Adjust.addSessionCallbackParameter("customer_user_id", strToMd5(WindHelper.mAndroidId))

        config.setOnAttributionChangedListener {
            WindHelper.log("setOnAttributionChangedListener--->${it.network}")
            if (CrispFlows.autumnUser().not()) {
                val network = it.network
                if (network.isNotBlank()) {
                    CrispFlows.mNetworkStr = network
                    if (CrispFlows.autumnUser()) {
                        WindHelper.eventPost("netjust")
                    }
                }
            }
        }

        Adjust.onCreate(config)
    }
}