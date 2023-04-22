package dev.souravdas.hush.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import dagger.hilt.android.AndroidEntryPoint
import de.palm.composestateevents.EventEffect
import dev.sourav.emptycompose.ui.theme.HushTheme
import dev.souravdas.hush.arch.MainActivityVM
import dev.souravdas.hush.compose.NavGraphs
import dev.souravdas.hush.others.Utils
import dev.souravdas.hush.services.KeepAliveService
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var utils: Utils
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
        } else {
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MainActivityVM = hiltViewModel()
            val uiState = viewModel.uiEventFlow.collectAsState()

            HushTheme {
                DestinationsNavHost(
                    navGraph = NavGraphs.layerGraph,
                    dependenciesContainerBuilder = {
                        dependency(NavGraphs.layerGraph) {
                            viewModel
                        }
                    })
            }

            EventEffect(
                event = uiState.value.processNotificationAccessPermissionGet,
                onConsumed = viewModel::onConsumedNotificationPermissionGet
            ) {
                openNotificationAccessSettingsIfNeeded()
            }

            EventEffect(event = uiState.value.processNotificationPermissionGet, onConsumed =viewModel::onConsumeNotificationPermissionGet){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    requestNotificationPermission(69)
                }
            }

            EventEffect(
                event = uiState.value.startHushService,
                onConsumed = viewModel::onConsumedStartHushService
            ) { isStart ->
                handleHushService(isStart)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun requestNotificationPermission(requestCode: Int) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, this.packageName)
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun handleHushService(value: Boolean) {
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

    private fun isNotificationListenerEnabled(context: Context): Boolean {
        val packageName = context.packageName
        val enabledPackages = NotificationManagerCompat.getEnabledListenerPackages(context)
        return enabledPackages.contains(packageName)
    }

    private fun openNotificationAccessSettingsIfNeeded() {
        // Permission is not granted, prompt the user to grant it
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        this.startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
    }

}

