package dev.souravdas.hush.base

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.souravdas.hush.arch.LocalTimeConverter
import dev.souravdas.hush.arch.SelectedApp
import dev.souravdas.hush.arch.SelectedAppDao

@Database(entities = [SelectedApp::class], version = 1)
@TypeConverters(LocalTimeConverter::class)
abstract class HushDB: RoomDatabase() {
    abstract fun selectAppDao(): SelectedAppDao
}