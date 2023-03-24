package dev.souravdas.hush.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Created by Sourav
 * On 3/18/2023 12:34 PM
 * For Hush!
 */
@Entity(
    tableName = "app_log",
    foreignKeys = [ForeignKey(
        entity = SelectedApp::class,
        parentColumns = ["id"],
        childColumns = ["selected_app_id"]
    )]
)
data class AppLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val selected_app_id: Int,
    val title: String?,
    val body: String?,
    val timeCreated: Long = System.currentTimeMillis()
)
