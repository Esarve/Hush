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
import com.ramcosta.composedestinations.annotation.Destination
import dev.souravdas.hush.arch.MainActivityVM
import dev.souravdas.hush.nav.Layer2graph
import dev.souravdas.hush.others.Constants

@Layer2graph
@Destination
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage() {
    val viewModel: MainActivityVM = hiltViewModel()
    var isDnd by remember { mutableStateOf(false) }
    var isRemovedExpired by remember { mutableStateOf(false) }
    var isNotify by remember { mutableStateOf(false) }

    LaunchedEffect(Unit){
        isDnd = viewModel.getBoolean(Constants.DS_DND)
        isRemovedExpired = viewModel.getBoolean(Constants.DS_DELETE_EXPIRE)
        isNotify = viewModel.getBoolean(Constants.DS_NOTIFY_MUTE)
    }

    val onDndCheckChangeLambda = remember<(Boolean) -> Unit> {
        {
            viewModel.changeBooleanDS(Constants.DS_DND, it)
            isDnd = !isDnd
        }
    }

    val onRemovedExpiredChangeLambda = remember<(Boolean) -> Unit> {
        {
            viewModel.changeBooleanDS(Constants.DS_DELETE_EXPIRE, it)
            isRemovedExpired = !isRemovedExpired
        }
    }

    val onNotifyChangeLambda = remember<(Boolean) -> Unit> {
        {
            viewModel.changeBooleanDS(Constants.DS_NOTIFY_MUTE, it)
            isNotify = !isNotify
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
                        }
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it)
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
        }
    }
}

@Composable
fun ToggleRow(label: String, checked:  Boolean, onCheckedChange: (Boolean) -> Unit) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)

    ) {
        Text(text = label, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth(0.8f))
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
