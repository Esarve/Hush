package dev.souravdas.hush

import android.app.Application

/**
 * Created by Sourav
 * On 2/24/2023 7:57 PM
 * For Hush!
 */
class HushApp: Application() {

    companion object {
        lateinit var context: HushApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}