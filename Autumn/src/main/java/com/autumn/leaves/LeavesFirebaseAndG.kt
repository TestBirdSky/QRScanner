package com.autumn.leaves

import android.content.Context
import android.os.Build
import android.util.Base64
import android.webkit.WebSettings
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
        postLastIns()
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
                            WindHelper.log("refreshCloak-->$claStr")
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
            WindHelper.mAdId = js.optString("qeunteus", "FD9B4CB9EA7F0E1C5E23800526F8C26F")
            WindHelper.windStatus = js.optString("autumn_time", "gg")
            val s = js.optString("cool", "30-30-60")
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

    private val URL =
        if (BuildConfig.DEBUG) "https://test-delphine.designqrgefpobyjpp.com/wish/nosy"
        else "https://delphine.designqrgefpobyjpp.com/gino/bewitch/boreas"

    override fun createUrl(): String {
        return "$URL?bacillus=${Build.MODEL}&oboist=${System.currentTimeMillis()}"
    }

    private val mUserA: String by lazy {
        try {
            WebSettings.getDefaultUserAgent(mApp)
        } catch (e: Exception) {
            ""
        }
    }

    override fun createInstJson(referrerDetails: ReferrerDetails) {
        val js = getCommonBody(mApp).apply {
            put("bindweed", JSONObject().apply {
                put("buzzard", "build/${Build.ID}")
                put("abstract", referrerDetails.installReferrer)
                put("pedal", referrerDetails.installVersion)
                put("argo", mUserA)
                put("defy", "canonic")
                put("francium", referrerDetails.referrerClickTimestampSeconds)
                put("egret", referrerDetails.installBeginTimestampSeconds)
                put("fir", referrerDetails.referrerClickTimestampServerSeconds)
                put("pam", referrerDetails.installBeginTimestampServerSeconds)
                put("apropos", CrispFlows.packInfoWindow().firstInstallTime)
                put("fugitive", CrispFlows.packInfoWindow().lastUpdateTime)
                put("grover", referrerDetails.googlePlayInstantParam)
                mCacheJsonInstall = this.toString()
            })
        }
        CoroutineScope(Dispatchers.IO).launch {
            requestInstall(js, this)
        }
    }

    private fun postLastIns() {
        if (mCacheJsonInstall.isNotBlank()) {
            runCatching {
                val js = getCommonBody(mApp).apply {
                    put("bindweed", JSONObject(mCacheJsonInstall))
                }
                CoroutineScope(Dispatchers.IO).launch {
                    requestInstall(js, this)
                }
            }
        }
    }

    private suspend fun requestInstall(jsonObject: JSONObject, scope: CoroutineScope) {
        WindHelper.log("createInstJson000 request>")
        request(jsonObject, success = {
            WindHelper.log("requestInstall success$it")
            mCacheJsonInstall = ""
        }, failed = {
            scope.launch {
                delay(12000)
                requestInstall(jsonObject, scope)
            }
        })
    }

    fun getAdjStr(context: Context) {
        val environment =
            if (BuildConfig.DEBUG) AdjustConfig.ENVIRONMENT_SANDBOX else AdjustConfig.ENVIRONMENT_PRODUCTION
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