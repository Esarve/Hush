package dev.souravdas.hush.arch

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Environment
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import dev.sourav.base.datastore.DataStoreManager
import dev.souravdas.hush.HushApp
import dev.souravdas.hush.base.BaseViewModel
import dev.souravdas.hush.compose.main.AppConfig
import dev.souravdas.hush.models.*
import dev.souravdas.hush.others.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainActivityVM @Inject constructor(
    private val selectAppRepository: SelectAppRepository,
    private val appLogRepository: AppLogRepository,
    private val dataStoreManager: DataStoreManager,
    private val utils: Utils
) : BaseViewModel() {

    private var uiState: HushViewState
        get() = _uiEventFlow.value
        set(newState)  {
            _uiEventFlow.update { newState }
        }

    private val _uiEventFlow = MutableStateFlow(HushViewState())
    val uiEventFlow = _uiEventFlow.asStateFlow()

    private val _appListSF = MutableStateFlow<List<InstalledPackageInfo>>(emptyList())
    val appListSF = _appListSF.asStateFlow()

    private val _selectedAppsSF = MutableStateFlow<List<SelectedApp>>(emptyList())
    val selectedAppsSF = _selectedAppsSF.asStateFlow()

    private val _appLog = MutableStateFlow<List<Combined>>(emptyList())
    val appLog = _appLog.asStateFlow()

    private val _appLogStats = MutableStateFlow<Resource<Map<LocalDate, Float>>>(
        Resource.Loading(
            emptyMap()
        )
    )
    val appLogStats = _appLogStats.asStateFlow()

    init {
        getSelectedApp()
        getInstalledApps()
        getLogStats()
    }

    suspend fun getBoolean(key: String): Boolean = dataStoreManager.getBooleanValue(key)

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
            } else selectAppRepository.addSelectedApp(selectedApp)
        }
    }


    fun getLog() {
        viewModelScope.launch {
            val appLogWithHeader: List<Pair<String?, AppLog>> = arrayListOf()
            appLogRepository.getAllLog().collect {
                val grouped = it.groupBy {
                    it.timeCreated.toLocalDate()
                }
                val listWithHeader: MutableList<Combined> = mutableListOf()
                grouped.entries.toList().forEach { item ->
                    var header: LocalDate? = item.key
                    item.value.forEach { log ->
                        listWithHeader.add(
                            Combined(
                                header, log
                            )
                        )
                        header = null
                    }

                }
                _appLog.value = listWithHeader
            }
        }
    }

    fun exportLog() {
        viewModelScope.launch {
            appLogRepository.getAllLog().collect() {
                val gson = Gson()
                val json = gson.toJson(it)

                val downloadsFolder =
                    HushApp.context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                val outputFile = File(downloadsFolder, "app_logs.json")
                outputFile.writeText(json)
            }
        }
    }

    fun importAppLogFromJson() {
        viewModelScope.launch {
            val downloadsFolder =
                HushApp.context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val inputFile = File(downloadsFolder, "app_logs.json")

            if (!inputFile.exists()) {
                throw IOException("File not found: app_logs.json")
            }

            val json = inputFile.readText()
            val appLogList = Gson().fromJson(json, Array<AppLog>::class.java)

            if (appLogList.isNotEmpty()) {
                appLogList.forEach {
                    appLogRepository.insertLog(it)
                }
            }
        }
    }

    fun getLogStats() {
        viewModelScope.launch {
            appLogRepository.getDataFromLastWeek().collect() { logs ->
                val grouped = logs.sortedBy {
                    it.timeCreated.toEpochSecond()
                }.groupBy {
                    it.timeCreated.toLocalDate()
                }
                val map = grouped.mapValues { it.value.size.toFloat() }

                val fullWeekMap = hashMapOf<LocalDate, Float>()

                if (map.isNotEmpty()) (0..6).forEach {
                    val day = LocalDate.now().minusDays(it.toLong())

                    if (map.containsKey(day)) {
                        fullWeekMap[day] = map[day]!!
                    } else {
                        fullWeekMap[day] = 0f
                    }
                }

                _appLogStats.value = Resource.Success(fullWeekMap.toList().sortedBy { (key,_) -> key  }.reversed().toMap())

            }
        }
    }

    fun changeBooleanDS(key: String, boolean: Boolean) {
        viewModelScope.launch {
            dataStoreManager.writeBooleanData(key, boolean)
        }
    }

    fun getSelectedApp() {
        viewModelScope.launch {
            selectAppRepository.getSelectedAppsWithFlow().map { apps ->
                apps.sortedWith(compareBy<SelectedApp> { it.isComplete }.thenByDescending { it.timeCreated })
            }.collectLatest {
                _selectedAppsSF.value = it
            }
        }
    }

    private fun getInstalledApps() {
        executedSuspendedCodeBlock(APP_LIST) {
            getPackageList()
        }
    }

    suspend fun storeBoolean(key: String, value: Boolean) {
        dataStoreManager.writeBooleanData(key, value)
    }

    fun getPackageList(): List<InstalledPackageInfo> {
        val pm: PackageManager = HushApp.context.packageManager
        val packages: MutableList<ApplicationInfo> = pm.getInstalledApplications(0)
        val packageNames = mutableListOf<InstalledPackageInfo>()

        for (packageInfo in packages) {
            if (packageInfo.enabled && pm.getLaunchIntentForPackage(packageInfo.packageName) != null && packageInfo.packageName != HushApp.context.packageName) {
                packageNames.add(
                    InstalledPackageInfo(
                        packageInfo.loadLabel(pm).toString(),
                        packageInfo.packageName,
                        packageInfo.loadIcon(pm)
                    )
                )

                AppIconsMap.appIconMap[packageInfo.packageName] = packageInfo.loadIcon(pm)
            }
        }
        return packageNames.sortedBy {
            it.appName.lowercase()
        }
    }


    fun getHushStatusAsFlow(key: String): Flow<Boolean> {
        return dataStoreManager.getBooleanValueAsFlow(key)
    }

    fun setHushStatus(value: Boolean) {
        if (isNotificationListenerEnabled(HushApp.context))
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
            appLogRepository.deleteAllBySelectedAppId(selectedApp.packageName)
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
            } else {
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


    fun updateComplete(app: SelectedApp) {
        executedSuspendedCodeBlock {
            val selectedAppFromDB = selectAppRepository.getSelectedApp(app.packageName)
            if (selectedAppFromDB != null) {
                selectedAppFromDB.isComplete = false
                selectAppRepository.update(selectedAppFromDB)
            }
        }
    }

    fun generateDummyLogs() {
        viewModelScope.launch {
            for (i in 1..20) {
                val randomDate = OffsetDateTime.now().minusDays(kotlin.random.Random.nextLong(0, 6))
                val log = AppLog(
                    appName = "App $i",
                    packageName = "com.example.app$i",
                    title = "Title $i",
                    body = "Body $i",
                    timeCreated = randomDate
                )
                appLogRepository.insertLog(log)
            }
        }
    }

    /* <-------- UI STUFFS ----------->*/

    fun dispatchUIEvent(event: UIEvent) {
        uiState = when(event){
            UIEvent.invokeNotificationPermissionGet -> {
                uiState.copy(processNotificationAccessPermissionGet = triggered)
            }

            UIEvent.invokeSettingsPageOpen -> {
                uiState.copy(processSettingsScreenOpen = triggered)
            }

            else -> TODO()
        }
    }

    fun onConsumedSettingsOpen(){
        uiState = uiState.copy(processSettingsScreenOpen = consumed)
    }

    fun onConsumedNotificationPermissionGet(){
        uiState = uiState.copy(processNotificationAccessPermissionGet = consumed)
    }

    fun onConsumedStartHushService(){
        uiState = uiState.copy(startHushService = consumed())
    }

    /* <------ Others --------> */

     fun isNotificationListenerEnabled(context: Context):Boolean {
        val packageName = context.packageName
        val enabledPackages = NotificationManagerCompat.getEnabledListenerPackages(context)
        return enabledPackages.contains(packageName)
    }
}