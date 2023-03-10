package dev.souravdas.hush

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.sourav.emptycompose.ui.theme.HushTheme
import dev.souravdas.hush.activities.KeepAliveService
import dev.souravdas.hush.activities.UIKit
import dev.souravdas.hush.arch.MainActivityVM
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val viewModel: MainActivityVM by viewModels()
    private lateinit var serviceConnection: ServiceConnection
    private var service: KeepAliveService? = null

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

        val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                val serviceBinder = binder as KeepAliveService.MyBinder
                service = serviceBinder.getService()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                service = null
            }
        }

        bindService(Intent(this, KeepAliveService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)

        checkService();
        openNotificationAccessSettingsIfNeeded(this)
    }

    private fun checkService() {
        lifecycleScope.launch {
            viewModel.getHushStatusAsFlow().collect() {value ->
                if (value){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(Intent(this@MainActivity, KeepAliveService::class.java))
                    }else{
                        startService(Intent(this@MainActivity, KeepAliveService::class.java))
                    }
                }else{
                    stopService()
                }
            }
        }
    }

    fun stopService() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            service?.stopService()
        else{
            service?.stopForeground(true)
            service?.stopSelf()
        }
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

