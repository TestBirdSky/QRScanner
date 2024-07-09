package com.autumn.leaves.flows

import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.autumn.leaves.LeaversCache
import com.autumn.leaves.ServiceCrisp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Date：2024/7/5
 * Describe:
 */

class AutumnServiceHelper(private val leaversCache: LeaversCache) {

    init {
        helperStart()
    }

    private fun helperStart() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(389)
            while (LeaversCache.isShowNotification.not()) {
                if (Build.VERSION.SDK_INT < 31) {
                    sss()
                } else {
                    if (leaversCache.num > 0) {
                        sss()
                        delay(3206)
                    }
                }
                delay(4649)
            }
        }
    }

    private fun sss() {
        runCatching {
            ContextCompat.startForegroundService(
                leaversCache.context, Intent(leaversCache.context, ServiceCrisp::class.java)
            )
        }
    }

}