package dev.souravdas.hush.arch

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.souravdas.hush.HushApp
import dev.souravdas.hush.InstalledPackageInfo
import dev.souravdas.hush.activities.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainActivityVM @Inject constructor(val selectAppRepository: SelectAppRepository) : BaseViewModel() {

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
            val selectedApps = selectAppRepository.getSelectedApps()
            val installedApps = getPackageList().associateBy ({it.packageName},{it.icon})
            val selectedAppsForList = mutableListOf<SelectedAppForList>()

            for (app:SelectedApp in selectedApps){
                selectedAppsForList.add(SelectedAppForList(app,installedApps[app.packageName]))
            }

            _selectedAppsSF.value = selectedAppsForList
            Timber.i("Get App list called")
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

    @Suppress("UNCHECKED_CAST")
    override fun onSuspendResponse(operationTag: String, resultResponse: Any) {
        when (operationTag) {
            APP_LIST -> {
                _appListSF.value = resultResponse as List<InstalledPackageInfo>
                Timber.d("App list collected")
            }
        }
    }

}