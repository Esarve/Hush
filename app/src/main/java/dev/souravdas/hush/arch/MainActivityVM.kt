package dev.souravdas.hush.arch

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.souravdas.hush.HushApp
import dev.souravdas.hush.InstalledPackageInfo
import dev.souravdas.hush.activities.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainActivityVM @Inject constructor(val selectAppRepository: SelectAppRepository) : BaseViewModel() {

    val appListSF = MutableStateFlow<List<InstalledPackageInfo>>(emptyList())
    val selectedAppsSF = MutableStateFlow<List<SelectedAppForList>>(emptyList())

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
            coroutineScope {
                val selectedApps = async { selectAppRepository.getSelectedApps() }.await()

                val installedApps = async { getPackageList().associateBy ({it.packageName},{it.icon}) }.await()

                val selectedAppsForList = mutableListOf<SelectedAppForList>()

                selectedApps.forEach {
                    selectedAppsForList.add(SelectedAppForList(it, installedApps[it.packageName]!!))
                }

                selectedAppsSF.value = selectedAppsForList
            }
        }
    }

    fun getInstalledApps() {
        executedSuspendedCodeBlock(APP_LIST) {
            return@executedSuspendedCodeBlock getPackageList()
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
            APP_LIST -> appListSF.value = resultResponse as List<InstalledPackageInfo>
        }
    }

}