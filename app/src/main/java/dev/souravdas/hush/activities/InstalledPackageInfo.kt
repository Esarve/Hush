package dev.souravdas.hush

import android.graphics.drawable.Drawable
import java.io.Serializable

/**
 * Created by Sourav
 * On 2/22/2023 8:47 PM
 * For Hush
 */
class InstalledPackageInfo: Serializable {
    var appName: String = ""
    var packageName: String = ""
    var icon: Drawable? = null

    constructor() // Empty constructor

    constructor(appName: String, packageName: String) {
        this.appName = appName
        this.packageName = packageName
    }

    constructor(appName: String, packageName: String, icon: Drawable) {
        this.appName = appName
        this.packageName = packageName
        this.icon = icon
    }
}
