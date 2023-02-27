package dev.souravdas.hush.arch

import javax.inject.Inject

class SelectAppRepository @Inject constructor(val selectedAppDao: SelectedAppDao) {
    suspend fun addSelectedApp(selectedApp: SelectedApp){
        selectedAppDao.insert(selectedApp)
    }
}