package dev.souravdas.hush.arch

import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.threeten.bp.LocalTime

@Entity("selected_app")
data class SelectedApp (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val appName: String,
    val packageName: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val isAlways: Int,
    val timeCreated: Long = System.currentTimeMillis(),
)
