package dev.souravdas.hush.models

import android.service.notification.StatusBarNotification
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.souravdas.hush.others.HushType
import org.threeten.bp.LocalTime
import java.io.Serializable

@Entity("selected_app")
data class SelectedApp (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var appName: String,
    var packageName: String,
    var hushType: HushType? = null,
    var durationInMinutes: Long? = null,
    var muteDays: String? = null,
    var startTime: LocalTime? = null,
    var endTime: LocalTime? = null,
    val timeCreated: Long = System.currentTimeMillis(),
    var timeUpdated: Long,
    var logNotification: Boolean,
    var isComplete: Boolean
): Serializable
