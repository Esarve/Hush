package dev.souravdas.hush.others

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import dev.souravdas.hush.HushApp

/**
 * Created by Sourav
 * On 4/23/2023 12:50 PM
 * For Hush!
 */
class NotifyUtils(private val notificationHelper: NotificationHelper) {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun isNotificationPermissionGranted(): Boolean {
        return  ContextCompat.checkSelfPermission(
            HushApp.context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun pushNotification(title: String, sub: String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (isNotificationPermissionGranted()){
                notificationHelper.showNotification(title,sub)
            }
        }else{
            notificationHelper.showNotification(title,sub)
        }
    }
}