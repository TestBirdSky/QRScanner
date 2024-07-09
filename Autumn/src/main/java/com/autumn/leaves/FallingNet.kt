package com.autumn.leaves

import android.os.Build
import com.android.installreferrer.api.ReferrerDetails
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.util.Collections

/**
 * Date：2024/7/5
 * Describe:
 */
class FallingNet : BaseNetRequest() {
    var num = 1
    private val jsFalling = Collections.synchronizedList(arrayListOf<JSONObject>())

    //"isuser", "ishit", "jumpfail","netjust"
    private var nameList =
        mapOf("netjust" to 10, "isuser" to 1000, "ishit" to 1000, "jumpfail" to 10)

    fun eventPost(name: String, map: Map<String, String>? = null, isCancelPost: Boolean = false) {
        val retryNum = nameList[name] ?: 0
        if (retryNum == 0 && isCancelPost) {
            WindHelper.log("cancel post $name --$map")
            return
        }
        WindHelper.log("eventPost post $name --map=$map")

        val js = getCommonBody(mApp).apply {
            put("spur", name)
            map?.forEach { (t, u) ->
                put("$t<slavonic", u)
            }
        }
        if (retryNum > 0) {
            postJs(js, retryNum)
        } else {
            synchronized(jsFalling) {
                jsFalling.add(js)
                if (jsFalling.size > num) {
                    val jsArray = JSONArray()
                    ArrayList(jsFalling).forEach {
                        jsArray.put(it)
                    }
                    postArray(jsArray)
                    jsFalling.clear()
                }

            }
        }
    }

    private val URL =
        if (BuildConfig.DEBUG) "https://test-delphine.designqrgefpobyjpp.com/wish/nosy"
        else "https://delphine.designqrgefpobyjpp.com/gino/bewitch/boreas"

    override fun createUrl(): String {
        return "$URL?bacillus=${Build.MODEL}&oboist=${System.currentTimeMillis()}"
    }

    override fun createInstJson(res: ReferrerDetails) {
        // not use
    }

    private fun postJs(jsonObject: JSONObject, retry: Int) {
        request(jsonObject, failed = {
            if (retry > 0) {
                postJs(jsonObject, retry - 1)
            }
        }, success = {
            WindHelper.log("postJs success-->$it")
        })
    }

    private fun postArray(jsonArray: JSONArray, retry: Int = 2) {
        request(jsonArray, failed = {
            if (retry > 0) {
                postArray(jsonArray, retry - 1)
            }
        }, success = {
            WindHelper.log("postArray success-->$it")
        })
    }

    fun postAdJson(jsonObject: JSONObject) {
        val js = getCommonBody(mApp)
        js.apply {
            put("redhead", jsonObject)
        }
        request(js, {
            WindHelper.log("postAdJson success-->$it")
        }, failed = {

        })
    }
}