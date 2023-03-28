package dev.souravdas.hush.broadcastreceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.sourav.base.datastore.DataStoreManager
import dev.souravdas.hush.HushApp
import dev.souravdas.hush.others.Constants
import dev.souravdas.hush.services.KeepAliveService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Sourav
 * On 3/15/2023 7:31 PM
 * For Hush!
 */

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject
    lateinit var dataStoreManager: DataStoreManager

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||intent.action == Intent.ACTION_REBOOT) {
            Toast.makeText(context, "Boot Completer Received", Toast.LENGTH_SHORT).show()
            scope.launch {
                if (dataStoreManager.getBooleanValue(Constants.DS_HUSH_STATUS)){
                    val serviceIntent = Intent(context, KeepAliveService::class.java)
                    ContextCompat.startForegroundService(context, serviceIntent)
                }
            }

        }
    }
}
