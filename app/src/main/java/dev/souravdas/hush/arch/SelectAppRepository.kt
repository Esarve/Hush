package dev.souravdas.hush.arch

import dev.souravdas.hush.models.SelectedApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SelectAppRepository @Inject constructor(val selectedAppDao: SelectedAppDao) {
    suspend fun addSelectedApp(selectedApp: SelectedApp){
        withContext(Dispatchers.IO){
            selectedAppDao.insert(selectedApp)
        }
    }

    suspend fun getSelectedApp(packageName: String): SelectedApp? = withContext(Dispatchers.IO){
        selectedAppDao.getSelectedApp(packageName)
    }
    suspend fun getSelectedApps(): List<SelectedApp> = selectedAppDao.getAllSelectedApps()
    fun getSelectedAppsRaw(): List<SelectedApp> = selectedAppDao.getAllSelectedAppsRaw()

    suspend fun update(selectedApp: SelectedApp) {
        withContext(Dispatchers.IO){
            selectedApp.timeUpdated = System.currentTimeMillis()
            selectedAppDao.update(selectedApp)
        }
    }
    suspend fun delete(selectedApp: SelectedApp) = selectedAppDao.delete(selectedApp)
    suspend fun removedSelectedApp(selectedApp: SelectedApp) = withContext(Dispatchers.IO){
        selectedAppDao.delete(selectedApp);
    }

    suspend fun removedIncompleteApps(){
        withContext(Dispatchers.IO){
            selectedAppDao.removeIncompleteApps(false)
        }
    }

    fun getSelectedAppsWithFlow(): Flow<List<SelectedApp>> = selectedAppDao.getAllSelectedAppsWithFlow()
    fun getDBUpdatesWithFlow(): Flow<Unit> = selectedAppDao.getAllSelectedAppsWithFlow().map { }
}