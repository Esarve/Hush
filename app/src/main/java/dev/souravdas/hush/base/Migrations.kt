package dev.souravdas.hush.base

import androidx.room.migration.Migration

/**
 * Created by Sourav
 * On 4/10/2023 10:55 AM
 * For Hush!
 */

val MIGRATION_1_2: Migration = Migration(1,2){database ->
    database.execSQL("ALTER TABLE app_log ADD COLUMN appName TEXT NOT NULL DEFAULT ''")
    database.execSQL("ALTER TABLE app_log ADD COLUMN packageName TEXT NOT NULL DEFAULT ''")
}