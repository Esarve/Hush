package dev.souravdas.hush.arch

import dev.souravdas.hush.models.AppLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Sourav
 * On 3/18/2023 12:39 PM
 * For Hush!
 */
class AppLogRepository @Inject constructor(val appLogDao: AppLogDao) {

    suspend fun insertLog(appLog: AppLog) {
        withContext(Dispatchers.IO){
            appLogDao.insertLog(appLog)
        }
    }

    suspend fun deleteAllBySelectedAppId(selectedAppId:Int){
        withContext(Dispatchers.IO){
            appLogDao.deleteAllByForeignKey(selectedAppId)
        }
    }

    fun getAllBySelectedAppID(selectedAppId: Int): Flow<List<AppLog>> = appLogDao.getAllByForeignKey(selectedAppId)
    fun getAllLog(): Flow<List<AppLog>> = appLogDao.getAllLog()
}