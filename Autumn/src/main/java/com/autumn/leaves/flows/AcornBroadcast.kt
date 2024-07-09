package com.autumn.leaves.flows

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Date：2024/7/1
 * Describe:
 */
class AcornBroadcast : BroadcastReceiver() {
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            scope.launch {
                CrispFlows.globalFlow.emit("broadcast")
            }
        }

    }
}