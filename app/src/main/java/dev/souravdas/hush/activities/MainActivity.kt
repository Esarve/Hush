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
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import dagger.hilt.android.AndroidEntryPoint
import dev.sourav.emptycompose.ui.theme.HushTheme
import dev.souravdas.hush.arch.MainActivityVM
import dev.souravdas.hush.compose.FloatingNav
import dev.souravdas.hush.compose.main.ShowBottomSheet
import dev.souravdas.hush.models.SelectedApp
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            viewModel.getSelectedApp()
            val scope = rememberCoroutineScope()

            val showBottomSheet = remember {
                mutableStateOf(false)
            }


            val installedApps = remember {
                viewModel.getPackageList()
            }


            val addSelectedApp = remember<(SelectedApp) -> Unit> {
                {
                    viewModel.addOrUpdateSelectedApp(selectedApp = it)
                    viewModel.getSelectedApp()
                }
            }

            val isBottomSheetOpenLambda = remember {
                {
                    showBottomSheet.value
                }
            }

            ShowBottomSheet(installedApps, isBottomSheetOpenLambda) { app ->
                showBottomSheet.value = false
                app?.let {
                    addSelectedApp.invoke(it)
                }
            }

            HushTheme() {
                TabNavigator(HomeTab){
                    Scaffold(
                        bottomBar = {
                            FloatingNav {
                                scope.launch {
                                    showBottomSheet.value = true
                                }
                            }
                        },
                        modifier = Modifier.background(MaterialTheme.colorScheme.background),
                        content = {CurrentTab()}
                    )
                }
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

