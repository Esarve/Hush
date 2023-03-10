package dev.souravdas.hush.arch

import androidx.room.*
import dev.souravdas.hush.models.SelectedApp
import kotlinx.coroutines.flow.Flow

@Dao
interface SelectedAppDao {
    @Query("SELECT * FROM selected_app")
    suspend fun getAllSelectedApps(): List<SelectedApp>

    @Query("SELECT * FROM selected_app")
    fun getAllSelectedAppsRaw(): List<SelectedApp>

    @Query("SELECT * FROM selected_app")
    fun getAllSelectedAppsWithFlow(): Flow<List<SelectedApp>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(selectedApp: SelectedApp)

    @Delete
    suspend fun delete(selectedApp: SelectedApp)
}