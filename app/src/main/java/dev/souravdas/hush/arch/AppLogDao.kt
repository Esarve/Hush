package dev.souravdas.hush.arch

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.souravdas.hush.models.AppLog
import dev.souravdas.hush.models.SelectedApp
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
}