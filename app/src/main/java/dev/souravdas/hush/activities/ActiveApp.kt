package dev.souravdas.hush.activities

import android.graphics.drawable.Drawable
import dev.souravdas.hush.InstalledPackageInfo
import java.time.LocalTime

/**
 * Created by Sourav
 * On 2/25/2023 7:47 PM
 * For Hush!
 */
class ActiveApp(
    installedPackageInfo: InstalledPackageInfo,
    startTime: LocalTime,
    endTime: LocalTime,
    isAlways: Boolean = false
) {
    var appName: String?
    var appPackage: String?
    var appIcon: Drawable?
    var startTime: LocalTime?
    var endTime: LocalTime?
    val isAlways: Boolean

    init {
        appName = installedPackageInfo.appName
        appPackage = installedPackageInfo.packageName
        appIcon = installedPackageInfo.icon
        this.startTime = startTime
        this.endTime = endTime
        this.isAlways = isAlways
    }
}