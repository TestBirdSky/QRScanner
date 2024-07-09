package com.autumn.leaves

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat

/**
 * Date：2024/7/5
 * Describe:
 */

class ServiceCrisp : Service() {
    private var isInit = false
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel("Notification", "Notification Channel", NotificationManager.IMPORTANCE_DEFAULT)
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
        isInit = true
        startForeground(1233, getCreateNotification())
        LeaversCache.isShowNotification = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (isInit) {
            startForeground(1233, getCreateNotification())
        }
        return START_STICKY
    }

    private var mNotification: Notification? = null

    private fun getCreateNotification(): Notification {
        return mNotification ?: NotificationCompat.Builder(this, "Notification")
            .setAutoCancel(false).setContentText("").setSmallIcon(R.drawable.rain_pic)
            .setOngoing(true).setOnlyAlertOnce(true).setContentTitle("")
            .setCustomContentView(RemoteViews(this.packageName, R.layout.rain_layout)).build()
            .apply {
                mNotification = this
            }
    }
}