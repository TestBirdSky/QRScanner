package com.autumn.leaves

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.provider.Settings
import android.util.Log
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.autumn.leaves.flows.CrispFlows
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.tradplus.ads.base.bean.TPAdInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Locale
import java.util.UUID

/**
 * Date：2024/6/28
 * Describe:
 */
object WindHelper {

    @JvmStatic
    external fun autumnWind(context: Context, string: String): Int

    private val fallingNet by lazy { FallingNet() }
    var mReferrer by AppleCache(nameP = "RefStr")
    var mCloakInfo by AppleCache(nameP = "Cool_Net")
    var mAndroidId by AppleCache(nameP = "A_id")

    var mAdId by AppleCache(nameP = "T_Id")

    private val TAG = "Autumn_log--->"

    fun log(msg: String) {
        // todo del
        if (IS_TEST) {
            Log.e(TAG, msg)
        }
    }

    private var timeCheckAutumn = 20 * 1000L// 检测定时
    var timeShowPAutumn = 45 * 1000L// 显示间隔
    var windStatus = "111"
    private var timeWait = 60 * 1000L// 首次等待时间

    fun isFlowWaitFinish(): Boolean {
        return System.currentTimeMillis() - CrispFlows.packInfoWindow().firstInstallTime > timeWait
    }

    suspend fun timeCheckDelay() {
        delay(timeCheckAutumn)
    }

    fun getTimeCheckTime(): Long {
        return timeCheckAutumn
    }

    fun refreshSu(time1: Long, time2: Long) {
        timeCheckAutumn = time1
        timeWait = time2
    }

    val langAndCountry by lazy { "${Locale.getDefault().language}_${Locale.getDefault().country}" }
    var mOpStr = ""
    var verName = ""
    fun initData(context: Context) {
        log("initData--->")
        mAndroidId = Settings.System.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            .ifBlank { UUID.randomUUID().toString() }
        verName = CrispFlows.packInfoWindow().versionName
        if (CrispFlows.mGaidStr.isBlank()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    CrispFlows.mGaidStr = AdvertisingIdClient.getAdvertisingIdInfo(context).id ?: ""
                } catch (e: Exception) {
                    ""
                }
            }
        }
    }

    fun eventPost(name: String, map: Map<String, String>? = null) {
        fallingNet.eventPost(name, map, windStatus.contains("c_lloo"))
    }

    fun setEventNumToMax() {
        fallingNet.num = 4
    }

    fun postAd(tpAdInfo: TPAdInfo) {

        fallingNet.postAdJson(JSONObject().apply {
            put("began", tpAdInfo.ecpm.toDouble() * 1000L)
            put("cleft", "USD")
            put("sister", getSoceijs(tpAdInfo.adNetworkId.toInt()))
            put("chine", "tradplus")
            put("apostasy", tpAdInfo.tpAdUnitId)
            put("stitch", "tradplus_i")
            put("sadism", tpAdInfo.format ?: "Interstitial")
        })

        val adjustAdRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_SOURCE_PUBLISHER)
        adjustAdRevenue.setRevenue(tpAdInfo.ecpm.toDouble() / 1000, "USD")
        adjustAdRevenue.setAdRevenueUnit(tpAdInfo.adSourceId)
        adjustAdRevenue.setAdRevenuePlacement(tpAdInfo.adSourcePlacementId)
        //发送收益数据
        Adjust.trackAdRevenue(adjustAdRevenue)
    }

    private fun getSoceijs(index: Int): String {
        return when (index) {
            1 -> "Facebook"
            7 -> "vungle"
            9 -> "AppLovin"
            23 -> "inmobi"
            18 -> "Mintegral"
            36 -> "Appnext"
            19 -> "pangle"
            57 -> "Bigo"
            else -> "tradplus_${index}"
        }
    }

    fun queryInfo(uri: Uri, name: String, array: Array<String>): Cursor? {
        if (!uri.toString().endsWith("/directories")) {
            return null
        }
        val matrixCursor = MatrixCursor(array)
        matrixCursor.addRow(arrayOf<Any>(name, name, name, "0".toInt(), "1".toInt(), 1, 1))
        return matrixCursor
    }
}