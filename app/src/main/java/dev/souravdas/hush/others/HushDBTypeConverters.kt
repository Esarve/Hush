package dev.souravdas.hush.others

import androidx.room.TypeConverter
import org.threeten.bp.LocalTime

class HushDBTypeConverters {
    @TypeConverter
    fun fromLocalTime(value: LocalTime?): Long? {
        return value?.toNanoOfDay()
    }

    @TypeConverter
    fun toLocalTime(value: Long?): LocalTime? {
        return value?.let { LocalTime.ofNanoOfDay(it) }
    }

    @TypeConverter
    fun fromInt(value: Int): Boolean {
        return value == 1
    }

    @TypeConverter
    fun toInt(value: Boolean): Int {
        return if (value) 1 else 0
    }
}