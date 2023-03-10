package dev.souravdas.hush.arch

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sourav.base.datastore.DataStoreManager
import dev.souravdas.hush.HushApp
import dev.souravdas.hush.models.InstalledPackageInfo
import dev.souravdas.hush.base.BaseViewModel
import dev.souravdas.hush.others.Constants
import dev.souravdas.hush.models.SelectedApp
import dev.souravdas.hush.models.SelectedAppForList
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainActivityVM @Inject constructor(private val selectAppRepository: SelectAppRepository, private val dataStoreManager: DataStoreManager) : BaseViewModel() {

    private val _appListSF = MutableStateFlow<List<InstalledPackageInfo>>(emptyList())
    val appListSF = _appListSF.asStateFlow()

    private val _selectedAppsSF = MutableStateFlow<List<SelectedAppForList>>(emptyList())
    val selectedAppsSF = _selectedAppsSF.asStateFlow()

    companion object {
        const val APP_LIST = "APP_LIST"
        const val SELECTED_APP = "SELECTED_APP"
    }

    fun getDaysFromSelected(days: List<Int>):String{
        Timber.d("Found Day list $days")
        val daysOfWeek = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN") // days of week abbreviations
        val selectedDays = StringBuilder()

        for (i in days.indices) {
            if (days[i] == 1) {
                if (selectedDays.isNotEmpty()) {
                    selectedDays.append(",")
                }
                selectedDays.append(daysOfWeek[i])
            }
        }
        return selectedDays.toString()
    }

    fun addSelectedApp(selectedApp: SelectedApp) {
        executedSuspendedCodeBlock {
            selectAppRepository.addSelectedApp(selectedApp)
        }
    }

    fun getSelectedApp() {
        viewModelScope.launch {
            selectAppRepository.getSelectedAppsWithFlow().map {
                val installedApps = getPackageList().associateBy ({it.packageName},{it.icon})
                it.map {selectedApp ->
                    SelectedAppForList(selectedApp,installedApps[selectedApp.packageName])
                }
            }.collect{
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
            if (packageInfo.enabled && pm.getLaunchIntentForPackage(packageInfo.packageName) != null)
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

    suspend fun getHusStatus():Boolean{
        return dataStoreManager.getBooleanValue(Constants.DS_HUSH_STATUS)
    }

    fun setHushStatus(value: Boolean){
        viewModelScope.launch {
            dataStoreManager.writeBooleanData(Constants.DS_HUSH_STATUS,value)
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

}