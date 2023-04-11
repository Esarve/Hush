package dev.souravdas.hush.base

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.souravdas.hush.arch.AppLogDao
import dev.souravdas.hush.arch.SelectedAppDao
import dev.souravdas.hush.models.AppLog
import dev.souravdas.hush.models.SelectedApp
import dev.souravdas.hush.others.HushDBTypeConverters

@Database(entities = [SelectedApp::class, AppLog::class], version = 2)
@TypeConverters(HushDBTypeConverters::class)
abstract class HushDB: RoomDatabase() {
    abstract fun selectAppDao(): SelectedAppDao
    abstract fun appLogDao(): AppLogDao
}