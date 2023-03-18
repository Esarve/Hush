package dev.souravdas.hush.arch

import androidx.room.Dao
import androidx.room.Insert
import dev.souravdas.hush.models.AppLog

/**
 * Created by Sourav
 * On 3/18/2023 12:39 PM
 * For Hush!
 */
@Dao
interface AppLogDao {

    @Insert
    suspend fun insertLog(appLog: AppLog)
}