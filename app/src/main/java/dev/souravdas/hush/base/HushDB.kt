package dev.souravdas.hush.base

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.souravdas.hush.arch.SelectedAppDao
import dev.souravdas.hush.models.SelectedApp
import dev.souravdas.hush.others.HushDBTypeConverters

@Database(entities = [SelectedApp::class], version = 1)
@TypeConverters(HushDBTypeConverters::class)
abstract class HushDB: RoomDatabase() {
    abstract fun selectAppDao(): SelectedAppDao
}