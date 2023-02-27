package dev.souravdas.hush.arch

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.souravdas.hush.HushApp
import dev.souravdas.hush.InstalledPackageInfo
import dev.souravdas.hush.activities.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalTime
import javax.inject.Inject

@HiltViewModel
class MainActivityVM @Inject constructor(val selectAppRepository: SelectAppRepository) : BaseViewModel() {

    val appListSF = MutableStateFlow<List<InstalledPackageInfo>>(emptyList())

    companion object {
        const val APP_LIST = "APP_LIST"
        const val SELECTED_APP = "SELECTED_APP"
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