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
  "cool": "30-60-60",
  "autumn_time": "",
  "qeunteus": "FD9B4CB9EA7F0E1C5E23800526F8C26F",
  "leaves": "bytedance-adjust-not%20set"
} 
""".trimIndent()

val TEST_C get() = Base64.encodeToString(T.toByteArray(), Base64.DEFAULT)