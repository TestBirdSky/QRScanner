package com.autumn.leaves.broad

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Date：2024/7/9
 * Describe:
 */
class LeavesBroadReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return
        if (intent.hasExtra("data")) {
            val intent1 = intent.getParcelableExtra("data") as Intent?
            if (intent1 != null) {
                try {
                    context?.startActivity(intent1)
                    context?.unregisterReceiver(this)
                } catch (_: Exception) {

                }
            }
        }
    }
}