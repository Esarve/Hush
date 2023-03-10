package dev.souravdas.hush.others

import androidx.room.TypeConverter
import org.threeten.bp.LocalTime

class LocalTimeConverter {
    @TypeConverter
    fun fromLocalTime(value: LocalTime?): Long? {
        return value?.toNanoOfDay()
    }

    @TypeConverter
    fun toLocalTime(value: Long?): LocalTime? {
        return value?.let { LocalTime.ofNanoOfDay(it) }
    }
}