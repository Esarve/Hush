package dev.souravdas.hush.others

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dev.souravdas.hush.R

class NotificationHelper(private val context: Context) {
    
    private val notificationManager = ContextCompat.getSystemService(
        context, NotificationManager::class.java) as NotificationManager
    
    companion object {
        private const val CHANNEL_ID = "MyAppChannelId"
        private const val CHANNEL_NAME = "MyAppChannelName"
        private const val NOTIFICATION_ID = 1
    }
    
    fun showNotification(title: String, body: String) {
        val defaultIconRes = R.drawable.app_icon_svg
        val largeIcon = BitmapFactory.decodeResource(context.resources, defaultIconRes)
        
        // Create a NotificationCompat.Builder object
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(defaultIconRes)
            .setLargeIcon(largeIcon)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        
        // Create a NotificationChannel for Android 8.0 (Oreo) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        
        // Show the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}
