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
                .url(createUrl()).build()
        } else {
            Request.Builder().get().url(createUrl()).build()
        }
        requestNet(request, success, failed)
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
        // todo
        return JSONObject().apply {
            put("", UUID.randomUUID().toString())
            put("", System.currentTimeMillis())
            put("", Build.MANUFACTURER)
            put("", WindHelper.mAndroidId)
            put("", TimeZone.getDefault().rawOffset / 3600000)
            put("", disId.ifBlank {
                disId = strToMd5(WindHelper.mAndroidId)
                disId
            })
            put("", Build.BRAND)
            put("", Locale.getDefault().country)
            put("", UUID.randomUUID().toString())
            put("", Build.VERSION.RELEASE)
            put("", WindHelper.langAndCountry)
            put("", Build.MODEL)
            put("", WindHelper.verName)
            put("", context.packageName)
            put("", WindHelper.mOpStr)
        }
    }

}