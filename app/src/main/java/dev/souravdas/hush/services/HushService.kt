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
import dev.souravdas.hush.arch.AppLogRepository
import dev.souravdas.hush.arch.SelectAppRepository
import dev.souravdas.hush.models.AppLog
import dev.souravdas.hush.models.HushConfig
import dev.souravdas.hush.models.SelectedApp
import dev.souravdas.hush.others.Constants
import dev.souravdas.hush.others.HushType
import dev.souravdas.hush.others.NotifyUtils
import dev.souravdas.hush.others.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.threeten.bp.Duration
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Sourav
 * On 2/22/2023 8:28 PM
 * For Hush
 */
@AndroidEntryPoint
class HushService: NotificationListenerService() {
    private var NOTIFY_TIME_IN_MINS = 10
    private var NOTIFY_AMOUNT_MUTED = 5

    @Inject
    lateinit var utils: Utils
    @Inject
    lateinit var notifyUtils: NotifyUtils
    @Inject
    lateinit var appLogRepository: AppLogRepository
    @Inject
    lateinit var selectAppRepository: SelectAppRepository
    @Inject
    lateinit var dsm: DataStoreManager

    private var cancelMap: HashMap<String, Pair<Long, LocalTime>> = hashMapOf()
    private var isMute = false

    companion object {
        const val TAG = "HushService"
    }

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private var isServiceRunning = false;
    private var selectedApps: List<SelectedApp> = emptyList()
    private var hushConfig: HushConfig = HushConfig()

    override fun onCreate() {
        super.onCreate()
        scope.launch {
            selectAppRepository.getSelectedAppsWithFlow().collect{
                selectedApps = it
            }

            isServiceRunning = dsm.getBooleanValue(Constants.DS_HUSH_STATUS)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onNotificationPosted(notification: StatusBarNotification) {
        scope.launch {
            isServiceRunning = dsm.getBooleanValue(Constants.DS_HUSH_STATUS)
            hushConfig.isNotificationReminder = dsm.getBooleanValue(Constants.DS_NOTIFY_MUTE)
        }
        Timber.tag(TAG).i("Hush Service is running: $isServiceRunning")
        if (isServiceRunning) {
            Timber.tag(TAG).d("onNotificationPosted fired")
            Timber.tag(TAG).d("Received notification from package: ${notification.packageName}")

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
                            if (it.muteDays!!.contains(utils.getCurrentDayOfWeek()) && (it.startTime!!.isBefore(
                                    now
                                ) && it.endTime!!.isAfter(now))
                            ) {
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

    private fun cancelAndLog(statusBarNotification: StatusBarNotification, app: SelectedApp) {
        if (VERSION.SDK_INT >= VERSION_CODES.M && hushConfig.isDnd)
            enableDndModeFor4Seconds()
        val tmp = statusBarNotification
        cancelNotification(statusBarNotification.key)
        if (cancelMap.contains(tmp.packageName) && hushConfig.isNotificationReminder) {
            val lastNotificationTime = cancelMap[tmp.packageName]!!.second
            var notificationCount = cancelMap[tmp.packageName]!!.first
            val diff = Duration.between(LocalTime.now(), lastNotificationTime)
            if (diff.toMinutes() < NOTIFY_TIME_IN_MINS) {
                if (notificationCount >= NOTIFY_AMOUNT_MUTED) {
                    notifyUtils.pushNotification(
                        "Notify Alert from ${tmp.packageName}",
                        "Hush muted $NOTIFY_AMOUNT_MUTED notifications in $NOTIFY_TIME_IN_MINS minutes. Wanna check out whats going on?"
                    )
                    cancelMap[tmp.packageName] = Pair(1, LocalTime.now())
                } else {
                    Timber.tag(TAG).i("$notificationCount notifications muted WITHIN $NOTIFY_TIME_IN_MINS minutes")
                    cancelMap[tmp.packageName] = Pair(++notificationCount, LocalTime.now())
                }
            } else {
                cancelMap[tmp.packageName] = Pair(1, LocalTime.now())
                Timber.tag(TAG).i("notification muted in less than $NOTIFY_TIME_IN_MINS minutes")
            }
        } else {
            cancelMap[tmp.packageName] = Pair(1, LocalTime.now())
        }
        logNotification(tmp, app)
    }

    private fun logNotification(statusBarNotification: StatusBarNotification, app: SelectedApp) {
        if (app.logNotification) {
            scope.launch {
                appLogRepository.insertLog(
                    AppLog(
                        appName = app.appName,
                        packageName = app.packageName,
                        title = statusBarNotification.notification.extras.getString(Notification.EXTRA_TITLE),
                        body = statusBarNotification.notification.extras.getString(Notification.EXTRA_TEXT),
                    )
                )
                val lastDataTime = appLogRepository.getEarliestDate()
                val firstLogDiff = Duration.between( lastDataTime,OffsetDateTime.now(),)
                if (firstLogDiff.toDays() >= 30){
                    appLogRepository.deleteOldData(lastDataTime)
                }
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