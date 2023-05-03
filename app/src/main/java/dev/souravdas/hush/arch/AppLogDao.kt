package dev.souravdas.hush.arch

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.souravdas.hush.models.AppLog
import kotlinx.coroutines.flow.Flow

/**
 * Created by Sourav
 * On 3/18/2023 12:39 PM
 * For Hush!
 */
@Dao
interface AppLogDao {

    @Insert
    suspend fun insertLog(appLog: AppLog)

    @Query("DELETE FROM app_log WHERE packageName= :packageName")
    suspend fun deleteAllByPackageName(packageName: String)

    @Query("SELECT * FROM app_log ORDER BY timeCreated DESC")
    fun getAllLog(): Flow<List<AppLog>>

    @Query("SELECT * FROM app_log WHERE DATE(timeCreated,'localtime') >= DATE(:startDate, 'localtime')")
    fun getAppLogsFromLastWeek(startDate: String): Flow<List<AppLog>>

    @Query("SELECT * FROM app_log ORDER BY timeCreated ASC limit 1")
    fun getEarliestDate(): AppLog

    @Query("DELETE FROM app_log WHERE DATE(timeCreated, 'localtime') <= DATE(:fromDate,'localtime')")
    fun deleteOldDate(fromDate: String)
}