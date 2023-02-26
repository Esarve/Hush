package dev.souravdas.hush.arch

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalTime

@Entity("selected_app")
data class SelectedApp (
    @PrimaryKey
    val id: Int = 0,
    val packageName: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val isAlways: Int
)
