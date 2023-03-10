package dev.souravdas.hush.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import dagger.hilt.android.AndroidEntryPoint
import dev.sourav.base.datastore.DataStoreManager
import dev.souravdas.hush.arch.SelectAppCache
import dev.souravdas.hush.models.SelectedApp
import dev.souravdas.hush.others.Constants
import dev.souravdas.hush.others.HushType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Sourav
 * On 2/22/2023 8:28 PM
 * For Hush
 */
@AndroidEntryPoint
class HushService : NotificationListenerService() {

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

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        scope.launch {
            isServiceRunning = dataStoreManager.getBooleanValue(Constants.DS_HUSH_STATUS)
        }
        Timber.tag(TAG).i("Hush Service is running: $isServiceRunning")
        if (isServiceRunning) {
            Timber.tag(TAG).d("onNotificationPosted fired")
            Timber.tag(TAG).d("Received notification from package: ${sbn.packageName}")

            if (selectedApps.isEmpty()) {
                Timber.tag(TAG).i("Selected app list empty")
                scope.launch {
                    selectedApps = selectAppCache.getSelectedApps().firstOrNull() ?: emptyList()
                }
            } else {
                var app: SelectedApp? = null
                if (selectedApps.any {
                        app = it
                        it.packageName == sbn.packageName
                    }) {

                    Timber.tag(TAG).i("App found on List. Cancelling notification")

                    when(app!!.hushType){
                        HushType.ALWAYS -> {
                            cancelNotification(sbn.key)
                        }
                        HushType.DURATION -> {
                            if (System.currentTimeMillis() <= app!!.timeUpdated + app!!.durationInMinutes!!* 60000){
                                cancelNotification(sbn.key)
                            }
                        }
                        HushType.DAYS -> {
                            Timber.tag(TAG).i("Schedule selected. NOT IMPLEMENTED YET")
                            cancelNotification(sbn.key)
                        }
                    }

                } else {
                    Timber.tag(TAG).d("App not found in list.")
                }
            }

        }
    }
}