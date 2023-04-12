package dev.souravdas.hush.base

import androidx.room.migration.Migration

/**
 * Created by Sourav
 * On 4/10/2023 10:55 AM
 * For Hush!
 */

val MIGRATION_1_2: Migration = Migration(1,2){database ->
    database.execSQL("DROP TABLE IF EXISTS app_log")

    // Create the new app_log table
    database.execSQL(
        "CREATE TABLE IF NOT EXISTS app_log (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "appName TEXT, " +
                "packageName TEXT, " +
                "title TEXT, " +
                "body TEXT, " +
                "timeCreated TEXT" +
                ")"
    )
}