package dev.souravdas.hush.arch

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sourav.base.datastore.DataStoreManager
import dev.souravdas.hush.HushApp
import dev.souravdas.hush.activities.UIKit
import dev.souravdas.hush.base.BaseViewModel
import dev.souravdas.hush.models.InstalledPackageInfo
import dev.souravdas.hush.models.SelectedApp
import dev.souravdas.hush.models.SelectedAppForList
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
    private val dataStoreManager: DataStoreManager,
    private val utils: Utils
) : BaseViewModel() {

    private val _appListSF = MutableStateFlow<List<InstalledPackageInfo>>(emptyList())
    val appListSF = _appListSF.asStateFlow()

    private val _selectedAppsSF = MutableStateFlow<List<SelectedAppForList>>(emptyList())
    val selectedAppsSF = _selectedAppsSF.asStateFlow()

    companion object {
        const val APP_LIST = "APP_LIST"
        const val SELECTED_APP = "SELECTED_APP"
    }

    fun addOrUpdateSelectedApp(selectedApp: SelectedApp) {
        executedSuspendedCodeBlock {
            val selectedAppFromDB =
                selectAppRepository.getSelectedApp(selectedApp.packageName ?: "")
            if (selectedAppFromDB != null) {
                selectAppRepository.delete(selectedAppFromDB)
            }
            selectAppRepository.addSelectedApp(selectedApp)
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
        return packageNames
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
            selectAppRepository.removedSelectedApp(selectedApp)
        }
    }

    fun addConfigInSelectedApp(
        app: SelectedApp,
        type: HushType,
        startEndTime: UIKit.StartEndTime,
        duration: Long,
        daysList: List<String?>,
        logNotification: Boolean
    ) {
        executedSuspendedCodeBlock {
            val selectedApp = SelectedApp(
                appName = app.appName,
                packageName = app.packageName,
                hushType = type,
                durationInMinutes = duration,
                muteDays = utils.getStringFromDaysList(daysList),
                startTime = utils.toLocalTime(startEndTime.startTime),
                endTime = utils.toLocalTime(startEndTime.endTime),
                timeUpdated = System.currentTimeMillis(),
                isComplete = true
            )

            val selectedAppFromDB = selectAppRepository.getSelectedApp(app.packageName ?: "")
            if (selectedAppFromDB != null) {
                selectAppRepository.delete(selectedAppFromDB)
            }
            selectAppRepository.addSelectedApp(selectedApp)
        }
    }

    fun removeIncompleteApp() {
        executedSuspendedCodeBlock {
            selectAppRepository.removedIncompleteApps()
        }
    }

}