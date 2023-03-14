package dev.souravdas.hush.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.souravdas.hush.others.HushType
import org.threeten.bp.LocalTime
import java.io.Serializable

@Entity("selected_app")
data class SelectedApp (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val appName: String,
    val packageName: String,
    val hushType: HushType? = null,
    val durationInMinutes: Long? = null,
    val muteDays: String? = null,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    val timeCreated: Long = System.currentTimeMillis(),
    val timeUpdated: Long,
    val isComplete: Boolean
): Serializable
