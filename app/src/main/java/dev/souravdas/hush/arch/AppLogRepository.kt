package dev.souravdas.hush.arch

import dev.souravdas.hush.models.AppLog
import dev.souravdas.hush.models.SelectedApp
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by Sourav
 * On 3/18/2023 12:39 PM
 * For Hush!
 */
class AppLogRepository @Inject constructor(val appLogDao: AppLogDao) {

    suspend fun insertLog(appLog: AppLog) {
        appLogDao.insertLog(appLog)
    }

    suspend fun deleteAllBySelectedAppId(selectedAppId:Int){
        appLogDao.deleteAllByForeignKey(selectedAppId)
    }

    fun getAllBySelectedAppID(selectedAppId: Int): Flow<List<AppLog>> = appLogDao.getAllByForeignKey(selectedAppId)
}