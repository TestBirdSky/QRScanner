package com.autumn.leaves.activity

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.autumn.leaves.flows.CrispFlows

/**
 * Date：2024/7/5
 * Describe:
 */
abstract class BaseFlowService : JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        val key = CrispFlows.getCueStatus(this)
        if (key != "true") {
            val pb = params?.extras
            val name = pb?.getString(key)
            if (name != null) {
                jum(this@BaseFlowService, name)
            }
        }
        return false
    }

    private fun jum(context: Context, name: String) {
        runCatching {
            val cn = ComponentName(context, name)
            val intent = Intent()
            intent.setClassName(context, cn.className)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }

    override fun onStartCommand(intent: Intent?, i: Int, i2: Int): Int {
        super.onStartCommand(intent, i, i2)
        return Service.START_STICKY
    }
}