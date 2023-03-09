package dev.souravdas.hush.activities

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import dev.souravdas.hush.R

/**
 * Created by Sourav
 * On 3/8/2023 9:02 PM
 * For Hush!
 */
class KeepAliveService : Service() {
    private var isRunning = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // This method is called when the service is started
        if (!isRunning) {
            isRunning = true
            // Start your long-running operation here
        }

        // Create a notification object
        val notification =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("My Foreground Service")
                .setContentText("Running...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()
        } else {
            Notification.Builder(this)
                .setContentTitle("My Foreground Service")
                .setContentText("Running...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()
        }

        // Start the service in the foreground with the notification
        startForeground(FOREGROUND_SERVICE_ID, notification)

        // Return START_STICKY to indicate that the service should be restarted if it is killed
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        // Clean up any resources you need here
    }

    override fun onBind(intent: Intent?): IBinder? {
        // You don't need to implement this method unless you're using bound services
        return null
    }

    companion object {
        private const val CHANNEL_ID = "my_channel"
        private const val FOREGROUND_SERVICE_ID = 101
    }

}