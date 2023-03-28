package dev.souravdas.hush.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import dev.souravdas.hush.R


/**
 * Created by Sourav
 * On 3/8/2023 9:02 PM
 * For Hush!
 */
class KeepAliveService : Service() {
    private var isRunning = false

    private val binder = MyBinder()

    inner class MyBinder : Binder() {
        fun getService(): KeepAliveService {
            return this@KeepAliveService
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!isRunning) {
            isRunning = true
        }

        val notification =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.keep_alive_service),
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(chan)

            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.hush_service))
                .setContentText(getString(R.string.hush_service_running))
                .setSmallIcon(R.drawable.app_icon_svg)
                .build()
        } else {
            Notification.Builder(this)
                .setContentTitle(getString(R.string.hush_service))
                .setContentText(getString(R.string.hush_service_running))
                .setSmallIcon(R.drawable.app_icon_svg)
                .build()
        }

        startForeground(FOREGROUND_SERVICE_ID, notification)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun stopService() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    companion object {
        private const val CHANNEL_ID = "my_channel"
        private const val FOREGROUND_SERVICE_ID = 101
    }

}