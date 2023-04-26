package dev.souravdas.hush.arch

import dev.souravdas.hush.models.AppLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
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

    suspend fun deleteAllBySelectedAppId(packageName:String){
        withContext(Dispatchers.IO){
            appLogDao.deleteAllByPackageName(packageName)
        }
    }

    fun getAllLog(): Flow<List<AppLog>> = appLogDao.getAllLog()

    fun getDataFromLastWeek():Flow<List<AppLog>> {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

        val currentDate = OffsetDateTime.now()
        val lastWeekDate = currentDate.minusDays(7)

        return appLogDao.getAppLogsFromLastWeek(startDate = lastWeekDate.format(formatter))
    }
}