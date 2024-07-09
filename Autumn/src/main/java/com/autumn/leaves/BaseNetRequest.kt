package com.autumn.leaves

import android.content.Context
import android.os.Build
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.security.MessageDigest
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

/**
 * Date：2024/7/1
 * Describe:
 */
abstract class BaseNetRequest {

    protected val okHttpClient = OkHttpClient()

    abstract fun createUrl(): String

    protected fun request(body: Any?, success: (str: String) -> Unit, failed: () -> Unit = {}) {
        val request = if (body != null) {
            Request.Builder().post(body.toString().toRequestBody("application/json".toMediaType()))
                .addHeader("cochrane", WindHelper.mAndroidId)
                .addHeader("oboist", "${System.currentTimeMillis()}").addHeader("pavanne", "red")
                .url(createUrl()).build()
        } else {
            Request.Builder().get().url(getUrlC()).build()
        }
        requestNet(request, success, failed)
    }

    private var flit = ""
    private fun getUrlC(): String {
        disId = disId.ifBlank { strToMd5(WindHelper.mAndroidId) }
        val str = StringBuilder()
        str.append(if (IS_TEST) "com.designqr.gefpobyjpp" else mApp.packageName)
        str.append("&pavanne=red")
        str.append("&logjam=${WindHelper.verName}")
        str.append("&savoyard=${disId}")
        str.append("&bacillus=${Build.MODEL}&mainstay=${Build.VERSION.RELEASE}&flit=$flit")
        str.append("&cochrane=${WindHelper.mAndroidId}")
        return "https://sucrose.designqrgefpobyjpp.com/ibex/calve?cardioid=$str"
    }

    private fun requestNet(request: Request, success: (str: String) -> Unit, failed: () -> Unit) {
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                WindHelper.log("onFailure-->$e")
                failed.invoke()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful && response.code == 200) {
                    val bo = response.body?.string() ?: ""
                    success.invoke(bo)
                } else {
                    failed.invoke()
                }
            }
        })
    }

    abstract fun createInstJson(res: ReferrerDetails)

    protected fun installRequest(success: (ref: String) -> Unit) {
        val referrerClient = InstallReferrerClient.newBuilder(mApp).build()
        referrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(p0: Int) {
                runCatching {
                    if (p0 == InstallReferrerClient.InstallReferrerResponse.OK) {
                        val response: ReferrerDetails = referrerClient.installReferrer
                        success.invoke(response.installReferrer)
                        createInstJson(response)
                        referrerClient.endConnection()
                    } else {
                        referrerClient.endConnection()
                    }
                }.onFailure {
                    referrerClient.endConnection()
                }
            }

            override fun onInstallReferrerServiceDisconnected() = Unit
        })
    }

    //md5
    protected fun strToMd5(string: String): String {
        val hash = MessageDigest.getInstance("MD5").digest(string.toByteArray())
        val hex = StringBuilder(hash.size * 2)
        for (b in hash) {
            var str = Integer.toHexString(b.toInt())
            if (b < 0x10) {
                str = "0$str"
            }
            hex.append(str.substring(str.length - 2))
        }
        return hex.toString()
    }

    private var disId = ""

    protected fun getCommonBody(context: Context): JSONObject {
        return JSONObject().apply {
            put("suffuse", JSONObject().apply {
                put("spar", WindHelper.mOpStr)
                put("dater", UUID.randomUUID().toString())
                put("hasnt", Build.BRAND)
                put("unary", TimeZone.getDefault().rawOffset / 3600000)

            })
            put("rudolf", JSONObject().apply {
                put("cochrane", WindHelper.mAndroidId)
                put("cardioid", context.packageName)
                put("mainstay", Build.VERSION.RELEASE)
                put("holmes", Locale.getDefault().country)

            })
            put("tin", JSONObject().apply {
                put("oboist", System.currentTimeMillis())
                put("pavanne", "red")
            })
            put("tunic", JSONObject().apply {
                put("logjam", WindHelper.verName)
                put("flit", "")
                put("waltham", WindHelper.langAndCountry)
                put("bacillus", Build.MODEL)
                put("grimm", Build.MANUFACTURER)
                put("savoyard", disId.ifBlank {
                    disId = strToMd5(WindHelper.mAndroidId)
                    disId
                })
            })
        }
    }

}