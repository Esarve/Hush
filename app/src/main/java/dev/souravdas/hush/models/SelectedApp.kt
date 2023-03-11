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
    val hushType: HushType,
    val durationInMinutes: Long?,
    val muteDays: String?,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val timeCreated: Long = System.currentTimeMillis(),
    val timeUpdated: Long
): Serializable