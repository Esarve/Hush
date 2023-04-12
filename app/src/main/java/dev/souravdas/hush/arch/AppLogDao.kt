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

    @Query("DELETE FROM app_log WHERE selected_app_id= :selectedAppID")
    suspend fun deleteAllByForeignKey(selectedAppID: Int)

    @Query("SELECT * FROM app_log WHERE selected_app_id= :selectedAppID")
    fun getAllByForeignKey(selectedAppID: Int): Flow<List<AppLog>>

    @Query("SELECT * FROM app_log ORDER BY timeCreated DESC")
    fun getAllLog(): Flow<List<AppLog>>

    @Query("SELECT * FROM app_log WHERE timeCreated >= :lastWeek ORDER BY timeCreated DESC")
    fun getAppLogsFromLastWeek(lastWeek: Long): Flow<List<AppLog>>
}