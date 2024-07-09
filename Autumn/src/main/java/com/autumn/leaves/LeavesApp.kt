package com.autumn.leaves

import android.app.Application
import android.util.Base64

/**
 * Date：2024/6/28
 * Describe:
 *
 */

lateinit var mApp: Application

//todo del

const val IS_TEST = true

// todo del
private val T = """
{
  "cool": "30-30-1",
  "autumn_time": "",
  "leaves": "bytedance-adjust-not%20set"
} 
""".trimIndent()

val TEST_C get() = Base64.encodeToString(T.toByteArray(), Base64.DEFAULT)