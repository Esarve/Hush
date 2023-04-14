package dev.souravdas.hush.base

import androidx.room.migration.Migration

/**
 * Created by Sourav
 * On 4/10/2023 10:55 AM
 * For Hush!
 */

val MIGRATION_1_2: Migration = Migration(1, 2) { database ->
    database.execSQL("DROP TABLE IF EXISTS app_log")

    // Create the new app_log table
    database.execSQL(
        "CREATE TABLE IF NOT EXISTS `app_log` (`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `timeCreated` TEXT NOT NULL, `packageName` TEXT NOT NULL, `title` TEXT, `body` TEXT, `appName` TEXT NOT NULL)"
    )
}