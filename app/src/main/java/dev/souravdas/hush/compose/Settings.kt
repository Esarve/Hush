package dev.souravdas.hush.compose

/**
 * Created by Sourav
 * On 3/22/2023 2:03 PM
 * For Hush!
 */
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import dev.souravdas.hush.arch.MainActivityVM
import dev.souravdas.hush.models.HushConfig
import dev.souravdas.hush.others.Constants
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(viewModel: MainActivityVM = hiltViewModel(), navController: NavHostController) {
    val hushConfig by viewModel.hushConfig.collectAsState(initial = HushConfig())

    val isDnd by remember { mutableStateOf(hushConfig.isDnd) }
    val isRemovedExpired by remember { mutableStateOf(hushConfig.isAutoDeleteExpired) }
    val isNotify by remember { mutableStateOf(hushConfig.isNotificationReminder) }

    val onDndCheckChangeLambda = remember<(Boolean) -> Unit> {
        {
            viewModel.changeBooleanDS(Constants.DS_DND, it)
        }
    }

    val onRemovedExpiredChangeLambda = remember<(Boolean) -> Unit> {
        {
            viewModel.changeBooleanDS(Constants.DS_DELETE_EXPIRE, it)
        }
    }

    val onNotifyChangeLambda = remember<(Boolean) -> Unit> {
        {
            viewModel.changeBooleanDS(Constants.DS_NOTIFY_MUTE, it)
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                title = { Text(text = "Settings") },
                navigationIcon = {
                    Icon(
                        Icons.Outlined.ArrowBack,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = "BACK",
                        modifier = Modifier.clickable {
                            navController.popBackStack()
                        }
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
        ) {
            ToggleRow(
                label = "Enable DND while muting notifications",
                checked = isDnd,
                onCheckedChange = onDndCheckChangeLambda
            )
            ToggleRow(
                label = "Automatically remove expired mutes",
                checked = isRemovedExpired,
                onCheckedChange = onRemovedExpiredChangeLambda
            )
            ToggleRow(
                label = "Notify if there's too many notification being muted",
                checked = isNotify,
                onCheckedChange = onNotifyChangeLambda
            )
//            ToggleRow(
//                label = { Text(text = "Automatic Updates") },
//                checked = automaticUpdatesEnabled,
//                onCheckedChange = { automaticUpdatesEnabled = it }
//            )
//            ToggleRow(
//                label = { Text(text = "Language Selection") },
//                checked = languageSelectionEnabled,
//                onCheckedChange = { languageSelectionEnabled = it }
//            )
        }
    }
}

@Composable
fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    var enabled by remember {
        mutableStateOf(true)
    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(text = label, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth(0.8f))
        Checkbox(
            checked = enabled,
            onCheckedChange = {
                onCheckedChange.invoke(it)
                enabled = !enabled
            },
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
