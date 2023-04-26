package dev.souravdas.hush.compose

/**
 * Created by Sourav
 * On 3/22/2023 2:03 PM
 * For Hush!
 */
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.souravdas.hush.HushApp
import dev.souravdas.hush.R
import dev.souravdas.hush.arch.MainActivityVM
import dev.souravdas.hush.compose.destinations.AboutScreenDestination
import dev.souravdas.hush.nav.Layer2graph
import dev.souravdas.hush.nav.TransitionAnimation
import dev.souravdas.hush.others.Constants


@Layer2graph
@Destination (style = TransitionAnimation::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun  SettingsPage(navigator: DestinationsNavigator) {
    val viewModel: MainActivityVM = hiltViewModel()
    var isDnd by remember { mutableStateOf(false) }
    var isRemovedExpired by remember { mutableStateOf(false) }
    var isNotify by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
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
            isNotify = it
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                title = { Text(text = "Settings") },
                navigationIcon = {
                    IconButton(onClick = { navigator?.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back Arrow")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "App Settings",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                ToggleRow(
                    drawable = painterResource(id = R.drawable.ic_dnd),
                    label = "Enable DND",
                    subLabel = "Turn on DND when a new notification is muted",
                    checked = isDnd,
                    onCheckedChange = onDndCheckChangeLambda
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.background)
                )
                ToggleRow(
                    drawable = painterResource(id = R.drawable.ic_notification),
                    label = "Notify Muted",
                    subLabel = "Send a notification when a certain amount of notification is being muted in a time range",
                    checked = isNotify,
                    onCheckedChange = onNotifyChangeLambda
                )
            }


            Text(
                text = "Others",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        navigator.navigate(AboutScreenDestination)
                    }
            ) {

                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 8.dp)

                ) {
                    Icon(
                        Icons.Rounded.Info, contentDescription = "Icon", modifier = Modifier
                            .size(32.dp, 32.dp)
                            .padding(4.dp)
                    )
                    Text(
                        text = "About",
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Text(
                text = "Contact Me",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        navigator.navigate(AboutScreenDestination)
                    }
            ) {
                val activity = (LocalContext.current as Activity)
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ContactButton(painter = painterResource(id = R.drawable.ic_telegram), title = "Telegram"){
                        activity.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://t.me/hushappsupport")
                            )
                        )
                    }
                    ContactButton(painter = painterResource(id = R.drawable.ic_main), title = "mail"){
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:")
                            putExtra(
                                Intent.EXTRA_EMAIL,
                                arrayOf("esarve.srv@gmail.com")
                            )
                            putExtra(Intent.EXTRA_SUBJECT, "Hush! feedback")
                        }
                        if (intent.resolveActivity(HushApp.context.packageManager) != null) {
                            activity.startActivity(intent)
                        }
                    }
                    ContactButton(painter = painterResource(id = R.drawable.ic_play), title = "Play Store"){
                        activity.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=dev.souravdas.hush")
                            )
                        )
                    }
                }
            }


        }
    }
}

@Composable
fun ContactButton(painter: Painter, title: String, onClick:() -> Unit = {}){
//    Button(onClick = { /*TODO*/ }, shape = IconButtonDefaults.outlinedShape) {
//        Row() {
//            Icon(
//                painter = painter,
//                contentDescription = title,
//                modifier = Modifier.size(24.dp,24.dp)
//            )
//
//            Text(text = title, style = MaterialTheme.typography.labelMedium)
//        }
//
//
//    }

    ElevatedAssistChip(
        onClick = onClick,
        label = {Text(text = title, style = MaterialTheme.typography.labelMedium)},
        leadingIcon = {
            Icon(
                painter = painter,
                contentDescription = title
            )
        },
        modifier = Modifier.height(32.dp)
    )
}

@Composable
fun ToggleRow(
    drawable: Painter,
    label: String,
    subLabel: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp)

    ) {
        Icon(
            drawable, contentDescription = "Icon", modifier = Modifier
                .size(32.dp, 32.dp)
                .padding(4.dp)
        )
        Column(modifier = Modifier.fillMaxWidth(0.8f)) {
            Text(
                text = label,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = subLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
