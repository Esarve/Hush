package dev.souravdas.hush

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.souravdas.hush.activities.UIKit
import dev.souravdas.hush.arch.MainActivityVM
import dev.souravdas.hush.ui.theme.HushTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val viewModel: MainActivityVM by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            viewModel.getSelectedApp()

            HushTheme {
                UIKit().MainActivityScreen(
                    onItemSelected = {
                        viewModel.addSelectedApp(it)
                        viewModel.getSelectedApp()
                        Toast.makeText(applicationContext, "APP ADDED", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    private fun updateStatusBarColor() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.statusBarColor = ContextCompat.getColor(this, R.color.whiteBG)
        // make icons white
    }

    @Suppress("Deprecation")
    private fun getPackageList(): List<InstalledPackageInfo> {
        val pm: PackageManager = this.packageManager
        val packages: MutableList<ApplicationInfo> = pm.getInstalledApplications(0)
        val packageNames = mutableListOf<InstalledPackageInfo>()

        for (packageInfo in packages) {
            if (packageInfo.enabled && pm.getLaunchIntentForPackage(packageInfo.packageName) != null)
                packageNames.add(
                    InstalledPackageInfo(
                        packageInfo.loadLabel(pm).toString(),
                        packageInfo.packageName,
                        packageInfo.loadIcon(pm)
                    )
                )
        }
        return packageNames

    }

    private fun openNotificationAccessSettingsIfNeeded(activity: Activity) {
        if (isNotificationListenerEnabled(activity)) {
            // Permission is already granted, no need to prompt the user
            return
        }

        // Permission is not granted, prompt the user to grant it
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        activity.startActivity(intent)
    }

    private fun isNotificationListenerEnabled(context: Context): Boolean {
        val packageName = context.packageName
        val enabledPackages = NotificationManagerCompat.getEnabledListenerPackages(context)
        return enabledPackages.contains(packageName)
    }
}

