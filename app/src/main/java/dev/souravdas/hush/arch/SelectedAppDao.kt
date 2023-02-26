package dev.souravdas.hush.arch

import androidx.room.*

@Dao
interface SelectedAppDao {
    @Query("SELECT * FROM selected_app")
    suspend fun getAllSelectedApps(): List<SelectedApp>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(selectedApp: SelectedApp)

    @Delete
    suspend fun delete(selectedApp: SelectedApp)
}