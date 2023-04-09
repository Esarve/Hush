package dev.souravdas.hush.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import cafe.adriel.voyager.navigator.tab.TabNavigator
import dagger.hilt.android.AndroidEntryPoint
import dev.sourav.emptycompose.ui.theme.HushTheme
import dev.souravdas.hush.arch.MainActivityVM
import dev.souravdas.hush.models.UIEvent
import dev.souravdas.hush.nav.HomeTab
import dev.souravdas.hush.others.Constants
import dev.souravdas.hush.others.Utils
import dev.souravdas.hush.services.KeepAliveService
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var utils: Utils
    private val viewModel: MainActivityVM by viewModels()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            viewModel.getSelectedApp()

            HushTheme() {
                TabNavigator(HomeTab)
            }
        }

        checkService();
        initUIListeners()
    }

    private fun initUIListeners() {
        viewModel.uiEventMLD.observe(this){event ->
            event.getContentIfNotHandled()?.let {
                when(it){
                    UIEvent.invokeNotificationPermissionCheck -> {

                    }
                    UIEvent.invokeNotificationPermissionGet -> {
                        openNotificationAccessSettingsIfNeeded(this)
                    }
                    UIEvent.showSelectAppSheet -> TODO()
                }
            }
        }
    }

    private fun checkService() {
        lifecycleScope.launch {
            viewModel.getHushStatusAsFlow(Constants.DS_HUSH_STATUS).collect() { value ->
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

    private fun isNotificationListenerEnabled(context: Context) {
        val packageName = context.packageName
        val enabledPackages = NotificationManagerCompat.getEnabledListenerPackages(context)
        lifecycleScope.launch {
            viewModel.storeBoolean(Constants.DS_NOTIFICATION_PERMISSION, enabledPackages.contains(packageName))
        }
    }

    override fun onResume() {
        isNotificationListenerEnabled(this)
        super.onResume()
    }

}

