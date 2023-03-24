package dev.souravdas.hush.services

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Handler
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.annotation.RequiresApi
import dagger.hilt.android.AndroidEntryPoint
import dev.sourav.base.datastore.DataStoreManager
import dev.souravdas.hush.HushApp
import dev.souravdas.hush.arch.SelectAppCache
import dev.souravdas.hush.models.AppLog
import dev.souravdas.hush.models.HushConfig
import dev.souravdas.hush.models.SelectedApp
import dev.souravdas.hush.others.Constants
import dev.souravdas.hush.others.HushType
import dev.souravdas.hush.others.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
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
    private var hushConfig: HushConfig = HushConfig()

    override fun onCreate() {
        super.onCreate()
        scope.launch {
            selectAppCache.getSelectedApps().collect {
                Timber.tag(TAG).i("Received app onCreate list $it")
                selectedApps = it
            }

            isServiceRunning = dataStoreManager.getBooleanValue(Constants.DS_HUSH_STATUS)

            selectAppCache.getConfig().collect{
                hushConfig = it
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
                    Timber.tag(TAG).i("App found on List. Cancelling notification")

                    when (app!!.hushType) {
                        HushType.ALWAYS -> {
                            cancelAndLog(notification, app!!)
                        }
                        HushType.DURATION -> {
                            if (System.currentTimeMillis() <= app!!.timeUpdated + app!!.durationInMinutes!! * 60000) {
                                cancelAndLog(notification, app!!)
                            } else {
                                Timber.tag(TAG).i("Time Expired. Notification will not be canceled")
                            }
                        }
                        HushType.DAYS -> {
                            Timber.tag(TAG).i("Schedule selected. NOT IMPLEMENTED YET")
                            val now = LocalTime.now()
                            app?.let {
                                if (it.muteDays!!.contains(utils.getCurrentDayOfWeek()) && (it.startTime!!.isBefore(now) && it.endTime!!.isAfter(now))){
                                    cancelAndLog(notification, app!!)
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

    private fun cancelAndLog(statusBarNotification: StatusBarNotification, app: SelectedApp) {
        if (VERSION.SDK_INT >= VERSION_CODES.M && hushConfig.isDnd)
            enableDndModeFor4Seconds()
        val tmp = statusBarNotification
        cancelNotification(statusBarNotification.key)
        logNotification(tmp, app)
    }

    private fun logNotification(statusBarNotification: StatusBarNotification, app: SelectedApp) {
        if (app.logNotification){
            scope.launch {
                selectAppCache.logNotification(
                    AppLog(
                        selected_app_id = app.id,
                        title = statusBarNotification.notification.extras.getString(Notification.EXTRA_TITLE),
                        body = statusBarNotification.notification.extras.getString(Notification.EXTRA_TEXT),
                    )
                )
            }
        }
    }

    @RequiresApi(VERSION_CODES.M)
    private fun enableDndModeFor4Seconds() {
        val notificationManager =
            HushApp.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!hushConfig.isDnd && notificationManager.isNotificationPolicyAccessGranted && !isMute) {
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