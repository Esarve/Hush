package dev.souravdas.hush.services

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.annotation.RequiresApi
import dagger.hilt.android.AndroidEntryPoint
import dev.sourav.base.datastore.DataStoreManager
import dev.souravdas.hush.HushApp
import dev.souravdas.hush.arch.SelectAppCache
import dev.souravdas.hush.models.SelectedApp
import dev.souravdas.hush.others.Constants
import dev.souravdas.hush.others.HushType
import dev.souravdas.hush.others.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.threeten.bp.LocalTime
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Sourav
 * On 2/22/2023 8:28 PM
 * For Hush
 */
@AndroidEntryPoint
class HushService : NotificationListenerService() {

    @Inject
    lateinit var utils: Utils

    private var isMute = false
    companion object {
        const val TAG = "HushService"
    }

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private var isServiceRunning = false;

    @Inject
    lateinit var selectAppCache: SelectAppCache

    @Inject
    lateinit var dataStoreManager: DataStoreManager
    private var selectedApps: List<SelectedApp> = emptyList()

    override fun onCreate() {
        super.onCreate()
        scope.launch {
            selectAppCache.getSelectedApps().collect {
                Timber.tag(TAG).i("Received app onCreate list $it")
                selectedApps = it
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onNotificationPosted(notification: StatusBarNotification) {
        scope.launch {
            isServiceRunning = dataStoreManager.getBooleanValue(Constants.DS_HUSH_STATUS)
        }
        Timber.tag(TAG).i("Hush Service is running: $isServiceRunning")
        if (isServiceRunning) {
            Timber.tag(TAG).d("onNotificationPosted fired")
            Timber.tag(TAG).d("Received notification from package: ${notification.packageName}")

            if (selectedApps.isEmpty()) {
                Timber.tag(TAG).i("Selected app list empty")
                scope.launch {
                    selectedApps = selectAppCache.getSelectedApps().firstOrNull() ?: emptyList()
                }
            } else {
                var app: SelectedApp? = null
                if (selectedApps.any {
                        app = it
                        it.packageName == notification.packageName
                    }) {
                    enableDndModeFor4Seconds()
                    Timber.tag(TAG).i("App found on List. Cancelling notification")

                    when (app!!.hushType) {
                        HushType.ALWAYS -> {
                            cancelNotification(notification.key)
                        }
                        HushType.DURATION -> {
                            if (System.currentTimeMillis() <= app!!.timeUpdated + app!!.durationInMinutes!! * 60000) {
                                cancelNotification(notification.key)
                            } else {
                                Timber.tag(TAG).i("Time Expired. Notification will not be canceled")
                            }
                        }
                        HushType.DAYS -> {
                            Timber.tag(TAG).i("Schedule selected. NOT IMPLEMENTED YET")
                            val now = LocalTime.now()
                            app?.let {
                                if (it.muteDays!!.contains(utils.getCurrentDayOfWeek()) && (it.startTime!!.isBefore(now) && it.endTime!!.isAfter(now))){
                                    cancelNotification(notification.key)
                                }
                            }

                        }
                        else -> {}
                    }

                } else {
                    Timber.tag(TAG).d("App not found in list.")
                }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun enableDndModeFor4Seconds() {
        val notificationManager =
            HushApp.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.isNotificationPolicyAccessGranted && !isMute) {
            val oldInterruptionFilter = notificationManager.currentInterruptionFilter
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
            isMute = true
            Handler(Looper.getMainLooper()).postDelayed({
                notificationManager.setInterruptionFilter(oldInterruptionFilter)
                isMute = false
            }, 4000) // 4 seconds
        }
    }
}