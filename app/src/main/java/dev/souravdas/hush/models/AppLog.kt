package dev.souravdas.hush.models

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime
import java.io.Serializable

/**
 * Created by Sourav
 * On 3/18/2023 12:34 PM
 * For Hush!
 */
@Entity(tableName = "app_log")
@Immutable
data class AppLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val appName: String,
    val packageName: String,
    val title: String?,
    val body: String?,
    val timeCreated: OffsetDateTime = OffsetDateTime.now()
): Serializable
