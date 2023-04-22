package dev.souravdas.hush.compose

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.souravdas.hush.R
import dev.souravdas.hush.arch.MainActivityVM
import dev.souravdas.hush.compose.destinations.MainScreenDestination
import dev.souravdas.hush.models.UIEvent
import dev.souravdas.hush.nav.Layer2graph
import dev.souravdas.hush.others.ExtraColors

/**
 * Created by Sourav
 * On 4/21/2023 8:44 PM
 * For Hush!
 */

@Layer2graph
@Destination
@Composable
fun PermissionScreen(vm: MainActivityVM, navigator: DestinationsNavigator) {
    val lifecycleState = LocalLifecycleOwner.current.lifecycle.observeAsState()
    val isNotificationAccessPermissionGrant =
        remember { mutableStateOf(vm.isNotificationAccessPermissionProvided()) }
    val isNotificationPermissionGrant =
        remember { mutableStateOf(vm.isNotificationPermissionGranted()) }
    val uiEvent = vm.uiEventFlow.collectAsState()

    val canContinue = remember {
        mutableStateOf(false)
    }

    when (lifecycleState.value) {
        Lifecycle.Event.ON_RESUME -> {
            isNotificationAccessPermissionGrant.value = vm.isNotificationAccessPermissionProvided()
            isNotificationPermissionGrant.value = vm.isNotificationPermissionGranted()
            canContinue.value =
                isNotificationPermissionGrant.value && isNotificationAccessPermissionGrant.value
        }

        else -> {}
    }
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp, vertical = 32.dp)
        ) {
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = "Permissions",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Some permissions are necessary for this app to work properly",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                PermissionItem(
                    title = "Notification Access", isNotificationAccessPermissionGrant.value
                ) {
                    vm.dispatchUIEvent(
                        UIEvent.InvokeNotificationAccessPermissionGet
                    )
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) PermissionItem(
                    title = "Post Notification", isNotificationPermissionGrant.value
                ) {
                    vm.dispatchUIEvent(UIEvent.InvokeNotificationPermissionGet)
                }

                PermissionItem(title = "Ignore Battery optimisation", false, {})

                Card(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(ExtraColors.infoContainer.getColor(isSystemInDarkTheme()),)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)

                    ) {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = "Info",
                            tint = ExtraColors.info.getColor(isSystemInDarkTheme())
                        )
                        Text(
                            text = "No data is collected. Everything will be stored locally on the device",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = ExtraColors.onInfoContainer.getColor(isSystemInDarkTheme()),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            FilledTonalButton(
                enabled = isNotificationAccessPermissionGrant.value && isNotificationPermissionGrant.value,
                onClick = {
                    navigator.popBackStack()
                    navigator.navigate(MainScreenDestination()) {
                        launchSingleTop = true
                    }
                },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(text = "Continue to app", style = MaterialTheme.typography.titleMedium)
                Icon(
                    Icons.Rounded.ArrowForward,
                    contentDescription = "Arrow Right",
                    modifier = Modifier.padding(horizontal = 16.dp),

                    )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionItem(title: String, isGranted: Boolean, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            if (isGranted) ExtraColors.successContainer.getColor(
                isSystemInDarkTheme()
            ) else MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onClick,
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    painterResource(id = if (isGranted) R.drawable.ic_checked else R.drawable.ic_radio_uncheck_24),
                    contentDescription = "uncheck",
                    modifier = Modifier.size(32.dp, 32.dp),
                    tint = if (isGranted) ExtraColors.success.getColor(isSystemInDarkTheme()) else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 8.dp),
                    color = if (isGranted) ExtraColors.onSuccessContainer.getColor(
                        isSystemInDarkTheme()
                    ) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Rounded.KeyboardArrowRight,
                contentDescription = "GO",
                modifier = Modifier
                    .size(32.dp, 32.dp)
                    .padding(horizontal = 8.dp),
                tint = if (isGranted) ExtraColors.success.getColor(isSystemInDarkTheme()) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

}

@Composable
fun Lifecycle.observeAsState(): State<Lifecycle.Event> {
    val state = remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    DisposableEffect(this) {
        val observer = LifecycleEventObserver { _, event ->
            state.value = event
        }
        this@observeAsState.addObserver(observer)
        onDispose {
            this@observeAsState.removeObserver(observer)
        }
    }
    return state
}
