package dev.souravdas.hush.models

import android.graphics.drawable.Drawable

/**
 * Created by Sourav
 * On 4/9/2023 2:02 PM
 * For Hush!
 */
data class AppLogWithIcon(
    val icon: Drawable,
    val appLog: AppLog
)
