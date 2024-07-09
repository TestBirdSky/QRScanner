package com.autumn.leaves.broad

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Date：2024/7/9
 * Describe:
 */
abstract class BaseActivityCozy : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(getCozyIntent("com.google.android.gm"))
    }

    private fun getLan(pkgName: String): Intent {
        return Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            setPackage(pkgName)
        }
    }

    private fun getCozyIntent(pkgName: String): Intent {
        runCatching {
            val intent = getLan(pkgName)
            val pm: PackageManager = packageManager
            val info = pm.queryIntentActivities(intent, 0)
            val launcherActivity = info.first().activityInfo.name
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.setClassName(pkgName, launcherActivity)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }

        return Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$pkgName")).apply {
            setPackage("com.android.vending")
        }
    }
}