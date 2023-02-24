package dev.souravdas.hush

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import kotlin.math.log

/**
 * Created by Sourav
 * On 2/22/2023 8:28 PM
 * For Hush
 */
class HushService: NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        cancelNotification(sbn.key)
    }
}