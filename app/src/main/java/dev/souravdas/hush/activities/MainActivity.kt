package dev.souravdas.hush.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import dagger.hilt.android.AndroidEntryPoint
import dev.sourav.emptycompose.ui.theme.HushTheme
import dev.souravdas.hush.arch.MainActivityVM
import dev.souravdas.hush.nav.MainScreen
import dev.souravdas.hush.others.Utils
import dev.souravdas.hush.services.KeepAliveService
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var utils: Utils
    private val viewModel: MainActivityVM by viewModels()
    private var doubleBackToExitPressedOnce = false

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            viewModel.getSelectedApp()

            HushTheme() {
                Navigator(MainScreen(onNotificationPermissionGet = {
                    openNotificationAccessSettingsIfNeeded(this)
                }, checkNotificationPermission = {
                    isNotificationListenerEnabled(this)
                })){
                    SlideTransition(navigator = it)
                }
            }
        }

        checkService();
    }

    private fun checkService() {
        lifecycleScope.launch {
            viewModel.getHushStatusAsFlow().collect() { value ->
                if (value) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(
                            Intent(
                                this@MainActivity,
                                KeepAliveService::class.java
                            )
                        )
                    } else {
                        startService(Intent(this@MainActivity, KeepAliveService::class.java))
                    }
                } else {
                    stopService(Intent(this@MainActivity, KeepAliveService::class.java))
                }
            }
        }
    }

    private fun openNotificationAccessSettingsIfNeeded(activity: Activity) {
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

