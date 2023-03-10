package dev.souravdas.hush.arch

import dev.souravdas.hush.models.SelectedApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SelectAppRepository @Inject constructor(val selectedAppDao: SelectedAppDao) {
    suspend fun addSelectedApp(selectedApp: SelectedApp){
        selectedAppDao.insert(selectedApp)
    }

    suspend fun getSelectedApps(): List<SelectedApp> = selectedAppDao.getAllSelectedApps()
    fun getSelectedAppsRaw(): List<SelectedApp> = selectedAppDao.getAllSelectedAppsRaw()
    suspend fun removedSelectedApp(selectedApp: SelectedApp) = selectedAppDao.delete(selectedApp);

    fun getSelectedAppsWithFlow(): Flow<List<SelectedApp>> = selectedAppDao.getAllSelectedAppsWithFlow()
    fun getDBUpdatesWithFlow(): Flow<Unit> = selectedAppDao.getAllSelectedAppsWithFlow().map { }
}