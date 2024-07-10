package com.autumn.leaves

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.adjust.sdk.Adjust
import com.autumn.leaves.flows.CrispFlows
import com.autumn.leaves.ss.ListenerShowEvent
import com.facebook.appevents.AppEventsLogger
import com.tradplus.ads.base.bean.TPAdError
import com.tradplus.ads.base.bean.TPAdInfo
import com.tradplus.ads.open.LoadAdEveryLayerListener
import com.tradplus.ads.open.TradPlusSdk
import com.tradplus.ads.open.interstitial.InterstitialAdListener
import com.tradplus.ads.open.interstitial.TPInterstitial
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Currency

/**
 * Date：2024/7/5
 * Describe:
 */
class LeaversCache(val context: Context) : InterstitialAdListener {
    private val mScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var isInitSuccess = false
    private var isAllowAll = false
    var mListenerShowEvent: ListenerShowEvent? = null

    fun finishActivity(): Int {
        if (leavers.isEmpty()) return 0
        ArrayList(leavers).forEach {
            it.finishAndRemoveTask()
        }
        return 1
    }

    companion object {
        var isShowNotification = false
    }

    var num = 0
    val leavers = arrayListOf<Activity>()

    fun checkAllow(activity: Activity, invoke: () -> Unit) {
        if (isAllowAll.not()) {
            isAllowAll = CrispFlows.curCrispLevel == "Autumn"
        }
        if (isAllowAll) {
            invoke.invoke()
            val name = activity::class.java.canonicalName ?: ""
            when (name) {
                "com.bytedance.sdk.openadsdk.activity.TTFullScreenOActivity" -> { //外弹
                    mScope.launch {
                        WindHelper.eventPost("startup")
                        mListenerShowEvent?.showStart()
                        if (isCanUse() != null) {
                            if (activity is AppCompatActivity) {
                                val time = System.currentTimeMillis()
                                while (System.currentTimeMillis() - time < 500) {
                                    delay(50)
                                    if (activity.lifecycle.currentState == Lifecycle.State.RESUMED) {
                                        break
                                    }
                                }
                            } else {
                                delay(299)
                            }
                            userShow(activity)
                        } else {
                            mListenerShowEvent?.showFailed()
                            WindHelper.eventPost("showfailer", mapOf("string" to "ad not ready"))
                            activity.finishAndRemoveTask()
                        }
                    }
                }
            }
        }
    }

    fun loadAndTry() {
        isBroadLoadSuccess = true
        loadTrad()
    }

    fun numJump() {
        if (num <= 0) {
            num = 0
            if (isAllowAll) {
                ArrayList(leavers).forEach {
                    it.finishAndRemoveTask()
                }
            } else {
                mScope.launch {
                    delay(1500)
                    if (num <= 0) {
                        ArrayList(leavers).forEach {
                            it.finish()
                        }
                    }
                }
            }
        }
    }

    fun adjSet(isResume: Boolean) {
        if (isResume) {
            Adjust.onResume()
        } else {
            Adjust.onPause()
        }
    }

    fun initTrad() {
        TradPlusSdk.setTradPlusInitListener {
            isInitSuccess = true
            requestAdCircle()
        }
        TradPlusSdk.initSdk(context, "BAF3FB6E969AD320576893222E193B78")
    }

    var isStopRequestCircle = false
    private fun requestAdCircle() {
        mScope.launch {
            delay(5000)
            while (isInitSuccess) {
                loadTrad()
                delay(50_000)
                if (isStopRequestCircle) {
                    WindHelper.log("cancel circle request")
                    break
                }
            }
        }
    }

    private var isLoading = false
    private var lastLoadingTime = 0L
    private var lastSaveTime = 0L
    private var mTpInters: TPInterstitial? = null
    fun loadTrad() {
        if (WindHelper.mAdId.isBlank()) return
        if (isInitSuccess.not()) return
        if (isLoading && System.currentTimeMillis() - lastLoadingTime < 60_000) {
            return
        }
        if (isCanUse() != null) {
            WindHelper.log("can use --->")
            return
        }
        WindHelper.log("load ad --->")

        isLoading = true
        lastLoadingTime = System.currentTimeMillis()
        if (mTpInters == null) {
            mTpInters = TPInterstitial(context, WindHelper.mAdId)
//            mTpInters?.let {
//                it.setAllAdLoadListener(object : LoadAdEveryLayerListener {
//                    override fun onAdAllLoaded(p0: Boolean) {
//                        WindHelper.log("AD---onAdAllLoaded-->")
//                    }
//
//                    override fun oneLayerLoadFailed(p0: TPAdError?, p1: TPAdInfo?) {
//                        WindHelper.log("AD---oneLayerLoadFailed-->${p0?.errorCode}---${p0?.errorMsg}")
//                    }
//
//                    override fun oneLayerLoaded(p0: TPAdInfo?) {
//                        WindHelper.log("AD---oneLayerLoaded")
//
//                    }
//
//                    override fun onAdStartLoad(p0: String?) {
//                        WindHelper.log("AD---onAdStartLoad")
//
//                    }
//
//                    override fun oneLayerLoadStart(p0: TPAdInfo?) {
//                        WindHelper.log("AD---oneLayerLoadStart")
//                    }
//
//                    override fun onBiddingStart(p0: TPAdInfo?) {
//                        WindHelper.log("AD---onBiddingStart")
//
//                    }
//
//                    override fun onBiddingEnd(p0: TPAdInfo?, p1: TPAdError?) {
//
//                    }
//
//                    override fun onAdIsLoading(p0: String?) {
//
//                    }
//                })
//            }
        }
        mTpInters?.let {
            WindHelper.eventPost("reqprogress")
            it.setAdListener(this)
            it.loadAd()
            if (System.currentTimeMillis() - lastSaveTime > 10 * 60 * 1000) {
                lastSaveTime = System.currentTimeMillis()
            }
        }
    }

    private val mTimeOut = 60 * 1000 * 55
    private var closeEvent: (() -> Unit)? = null
    private var isBroadLoadSuccess = false

    private fun userShow(activity: Activity) {
        val tp = isCanUse()
        if (tp != null) {
            closeEvent = {
                activity.finishAndRemoveTask()
            }
            tp.showAd(activity, "")
//            mTpInters = null
        }
    }

    fun isCanUse(): TPInterstitial? {
        val tp = mTpInters ?: return null
        if (System.currentTimeMillis() - lastSaveTime > mTimeOut) return null
        if (tp.isReady) {
            return tp
        }
        return null
    }

    override fun onAdLoaded(p0: TPAdInfo?) {
        WindHelper.eventPost("getprogress")
        isLoading = false
        lastSaveTime = System.currentTimeMillis()
        if (isBroadLoadSuccess) {
            isBroadLoadSuccess = false
            mListenerShowEvent?.loadedSuccess()
        }
    }

    override fun onAdFailed(p0: TPAdError?) {
        WindHelper.eventPost("showfailer", mapOf("string" to "${p0?.errorMsg}_${p0?.errorCode}"))
        mScope.launch {
            delay(15000)
            isLoading = false
            loadTrad()
        }
    }

    override fun onAdImpression(p0: TPAdInfo?) {
        p0?.let {
            postFbEcpm(it.ecpm)
            WindHelper.postAd(it)
        }
        loadTrad()
    }

    override fun onAdClicked(p0: TPAdInfo?) {

    }

    override fun onAdClosed(p0: TPAdInfo?) {
        closeEvent?.invoke()
        closeEvent = null
    }

    override fun onAdVideoError(p0: TPAdInfo?, p1: TPAdError?) {
        closeEvent?.invoke()
        closeEvent = null
    }

    override fun onAdVideoStart(p0: TPAdInfo?) {}

    override fun onAdVideoEnd(p0: TPAdInfo?) {}

    private fun postFbEcpm(ecpm: String) {
        WindHelper.eventPost("showsuccess")
        runCatching {
            AppEventsLogger.newLogger(mApp).logPurchase(
                (ecpm.toDouble() / 1000).toBigDecimal(), Currency.getInstance("USD")
            )
        }
    }

}