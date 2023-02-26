package dev.souravdas.hush.base

import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.souravdas.hush.arch.LocalTimeConverter
import dev.souravdas.hush.arch.SelectedAppDao

@TypeConverters(LocalTimeConverter::class)
abstract class HushDB: RoomDatabase() {
    abstract fun selectAppDao(): SelectedAppDao
}