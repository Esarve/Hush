package dev.souravdas.hush.arch

class SelectAppRepository(private val selectedAppDao: SelectedAppDao) {
    suspend fun addSelectedApp(selectedApp: SelectedApp){
        selectedAppDao.insert(selectedApp)
    }
}