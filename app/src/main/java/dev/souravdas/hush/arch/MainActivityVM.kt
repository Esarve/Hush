package dev.souravdas.hush.arch

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sourav.base.datastore.DataStoreManager
import dev.souravdas.hush.HushApp
import dev.souravdas.hush.base.BaseViewModel
import dev.souravdas.hush.compose.main.AppConfig
import dev.souravdas.hush.compose.main.StartEndTime
import dev.souravdas.hush.models.*
import dev.souravdas.hush.others.Constants
import dev.souravdas.hush.others.HushType
import dev.souravdas.hush.others.Utils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainActivityVM @Inject constructor(
    private val selectAppRepository: SelectAppRepository,
    private val appLogRepository: AppLogRepository,
    private val dataStoreManager: DataStoreManager,
    private val utils: Utils
) : BaseViewModel() {

    private val _appListSF = MutableStateFlow<List<InstalledPackageInfo>>(emptyList())
    val appListSF = _appListSF.asStateFlow()

    private val _selectedAppsSF = MutableStateFlow<List<SelectedAppForList>>(emptyList())
    val selectedAppsSF = _selectedAppsSF.asStateFlow()

    private val _appLog = MutableStateFlow<List<AppLog>>(emptyList())
    val appLog = _appLog.asStateFlow()

    private val _hushConfig = MutableSharedFlow<HushConfig>()
    val hushConfig = _hushConfig.asSharedFlow()

    companion object {
        const val APP_LIST = "APP_LIST"
        const val LOG = "LOG"
    }

    fun addOrUpdateSelectedApp(selectedApp: SelectedApp) {
        executedSuspendedCodeBlock {
            val selectedAppFromDB =
                selectAppRepository.getSelectedApp(selectedApp.packageName ?: "")
            if (selectedAppFromDB != null) {
                selectedAppFromDB.isComplete = false
                selectAppRepository.update(selectedAppFromDB)
            }else
                selectAppRepository.addSelectedApp(selectedApp)
        }
    }

    fun getLog(id: Int){
        viewModelScope.launch {
            appLogRepository.getAllBySelectedAppID(id).collect{
                _appLog.value = it
            }
        }
    }

    fun getSelectedApp() {
        viewModelScope.launch {
            selectAppRepository.getSelectedAppsWithFlow().map { apps ->
                val installedApps = getPackageList().associateBy({ it.packageName }, { it.icon })
                apps.sortedWith(
                    compareBy<SelectedApp> { it.isComplete }
                        .thenByDescending { it.timeCreated }
                ).map {
                    SelectedAppForList(
                        it,
                        installedApps[it.packageName]
                    )
                }
            }.collectLatest {
                _selectedAppsSF.value = it
            }
        }
    }

    fun getInstalledApps() {
        executedSuspendedCodeBlock(APP_LIST) {
            getPackageList()
        }
    }

    private fun getPackageList(): List<InstalledPackageInfo> {
        val pm: PackageManager = HushApp.context.packageManager
        val packages: MutableList<ApplicationInfo> = pm.getInstalledApplications(0)
        val packageNames = mutableListOf<InstalledPackageInfo>()

        for (packageInfo in packages) {
            if (packageInfo.enabled && pm.getLaunchIntentForPackage(packageInfo.packageName) != null && packageInfo.packageName != HushApp.context.packageName)
                packageNames.add(
                    InstalledPackageInfo(
                        packageInfo.loadLabel(pm).toString(),
                        packageInfo.packageName,
                        packageInfo.loadIcon(pm)
                    )
                )
        }
        return packageNames.sortedBy {
            it.appName
        }
    }


    fun getHushStatusAsFlow(): Flow<Boolean> {
        return dataStoreManager.getBooleanValueAsFlow(Constants.DS_HUSH_STATUS)
    }

    suspend fun getHusStatus(): Boolean {
        return dataStoreManager.getBooleanValue(Constants.DS_HUSH_STATUS)
    }

    fun setHushStatus(value: Boolean) {
        viewModelScope.launch {
            dataStoreManager.writeBooleanData(Constants.DS_HUSH_STATUS, value)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onSuspendResponse(operationTag: String, resultResponse: Any) {
        when (operationTag) {
            APP_LIST -> {
                _appListSF.value = resultResponse as List<InstalledPackageInfo>
                Timber.d("App list collected")
            }
        }
    }

    fun removeApp(selectedApp: SelectedApp) {
        executedSuspendedCodeBlock {
            appLogRepository.deleteAllBySelectedAppId(selectedApp.id)
            selectAppRepository.removedSelectedApp(selectedApp)
        }
    }

    fun addConfigInSelectedApp(appConfig: AppConfig) {
        executedSuspendedCodeBlock {
            val app = appConfig.selectedApp
            val selectedAppFromDB = selectAppRepository.getSelectedApp(app.packageName)
            if (selectedAppFromDB != null) {
                with(selectedAppFromDB) {
                    appName = app.appName
                    packageName = app.packageName
                    hushType = appConfig.type
                    durationInMinutes = appConfig.duration
                    muteDays = utils.getStringFromDaysList(appConfig.daysList)
                    startTime = utils.toLocalTime(appConfig.startEndTime.startTime)
                    endTime = utils.toLocalTime(appConfig.startEndTime.endTime)
                    timeUpdated = System.currentTimeMillis()
                    logNotification = appConfig.logNotification
                    isComplete = true
                }
                selectAppRepository.update(selectedAppFromDB)
            }else{
                val selectedApp = SelectedApp(
                    appName = app.appName,
                    packageName = app.packageName,
                    hushType = appConfig.type,
                    durationInMinutes = appConfig.duration,
                    muteDays = utils.getStringFromDaysList(appConfig.daysList),
                    startTime = utils.toLocalTime(appConfig.startEndTime.startTime),
                    endTime = utils.toLocalTime(appConfig.startEndTime.endTime),
                    timeUpdated = System.currentTimeMillis(),
                    logNotification = appConfig.logNotification,
                    isComplete = true
                )
                selectAppRepository.addSelectedApp(selectedApp)
            }
        }
    }

    fun removeIncompleteApp() {
        executedSuspendedCodeBlock {
            selectAppRepository.removedIncompleteApps()
        }
    }

    fun getHushConfig(){
        viewModelScope.launch {
            _hushConfig.tryEmit(
                HushConfig(
                    dataStoreManager.getBooleanValue(Constants.DS_DND),
                    dataStoreManager.getBooleanValue(Constants.DS_DELETE_EXPIRE),
                    dataStoreManager.getBooleanValue(Constants.DS_NOTIFY_MUTE)
                )
            )
        }
    }

    fun updateComplete(app: SelectedApp){
        executedSuspendedCodeBlock {
            val selectedAppFromDB = selectAppRepository.getSelectedApp(app.packageName)
            if (selectedAppFromDB != null){
                selectedAppFromDB.isComplete = false
                selectAppRepository.update(selectedAppFromDB)
            }
        }
    }

}