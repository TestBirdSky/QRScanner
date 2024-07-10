package com.autumn.leaves.flows

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.autumn.leaves.WindHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Date：2024/7/1
 * Describe:
 */
class AcornBroadcast(private val event: () -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            event.invoke()
        }

    }
}