package dev.souravdas.hush.activities

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import dev.souravdas.hush.HushApp
import dev.souravdas.hush.R
import dev.souravdas.hush.arch.MainActivityVM
import dev.souravdas.hush.models.InstalledPackageInfo
import dev.souravdas.hush.models.SelectedApp
import dev.souravdas.hush.models.SelectedAppForList
import dev.souravdas.hush.others.Constants
import dev.souravdas.hush.others.HushType
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.MaterialTheme as MD3

/**
 * Created by Sourav
 * On 2/22/2023 11:45 PM
 * For Hush!
 */
class UIKit() {
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun InstalledAppList(
        items: List<InstalledPackageInfo>,
        onItemClick: (SelectedApp) -> Unit = {},
    ) {
        val scope = rememberCoroutineScope()
        val modifier = Modifier.padding(4.dp)
        Box(
            modifier = Modifier
                .fillMaxHeight(fraction = 0.7f)
        ) {
            Column() {
                Box(
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth()
                        .background(color = MD3.colorScheme.primary)
                ) {
                    Text(
                        text = "Swipe up to select an app",
                        modifier = Modifier.align(alignment = Alignment.Center),
                        color = MD3.colorScheme.onPrimary
                    )
                }
                val lazyListState = rememberLazyListState()
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.background(color = MD3.colorScheme.background)
                ) {
                    itemsIndexed(items = items) { index, item ->

                        ListItem(
                            modifier = Modifier.clickable {
                                scope.launch {
                                    onItemClick.invoke(
                                        SelectedApp(
                                            appName = item.appName,
                                            packageName = item.packageName,
                                            timeUpdated = System.currentTimeMillis(),
                                            isComplete = false
                                        )
                                    )
                                }
                            },
                            icon = {
                                Image(
                                    painter = rememberDrawablePainter(
                                        drawable = item.icon ?: ContextCompat.getDrawable(
                                            LocalContext.current, R.mipmap.ic_launcher_round
                                        )
                                    ),
                                    contentDescription = "appIcon",
                                    modifier = Modifier.size(40.dp)
                                )
                            },
                            text = {
                                Text(
                                    text = item.appName,
                                    textAlign = TextAlign.Center,
                                    color = MD3.colorScheme.onBackground,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        )
                    }
                }
            }

        }
    }

    @Composable
    fun ShowAlertDialog(
        onConfirmClick: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = {},
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp)
                )
            },
            title = {
                Text(text = "Notification Permission Necessary")
            },
            text = {
                Text(text = stringResource(id = R.string.notification_alert_dialog_body))
            },
            confirmButton = {
                Button(onClick = {
                    onConfirmClick.invoke()
                    Toast.makeText(
                        HushApp.context,
                        "Please Select Hush! from the list",
                        Toast.LENGTH_LONG
                    ).show()
                }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            }
        )
    }

    @OptIn(
        ExperimentalMaterial3Api::class,
        ExperimentalMaterialApi::class, ExperimentalLayoutApi::class
    )
    @Composable
    fun MainActivityScreen(
        viewModel: MainActivityVM = viewModel(),
        checkNotificationPermission: () -> Boolean,
        onNotificationPermissionGet: () -> Unit
    ) {
        val scope = rememberCoroutineScope()
        val scaffoldState = rememberBottomSheetScaffoldState()
        val list = viewModel.appListSF.collectAsState()
        viewModel.getInstalledApps()

        BottomSheetScaffold(
            backgroundColor = MD3.colorScheme.background,
            scaffoldState = scaffoldState,
            sheetPeekHeight = 56.dp,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(id = R.string.app_name),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Medium,
                            color = MD3.colorScheme.onBackground
                        )
                    }
                )
            },
            sheetContent = {
                InstalledAppList(items = list.value) { item ->
                    scope.launch {
                        scaffoldState.bottomSheetState.collapse()
                    }.invokeOnCompletion {
                        // TODO: insert here
                        viewModel.addOrUpdateSelectedApp(item)
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                androidx.compose.material.FloatingActionButton(
                    onClick = {
                        scope.launch {
                            if (scaffoldState.bottomSheetState.isExpanded) {
                                scaffoldState.bottomSheetState.collapse()
                            } else {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }
                    },
                    backgroundColor = MD3.colorScheme.secondary,
                    contentColor = MD3.colorScheme.onSecondary,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.twotone_add_24),
                        tint = MD3.colorScheme.onSecondary,
                        contentDescription = "Add Icon"
                    )
                }
            },
            modifier = Modifier.background(MD3.colorScheme.background)
        ) { mo ->//HERE
            val modifier = Modifier.consumeWindowInsets(mo)
            val hushStatus = viewModel.getHushStatusAsFlow().collectAsState(initial = false)
            var showNotificationPermissionAlertDialog by remember {
                mutableStateOf(!checkNotificationPermission.invoke())
            }

            if (showNotificationPermissionAlertDialog)
                ShowAlertDialog {
                    showNotificationPermissionAlertDialog = false
                    onNotificationPermissionGet.invoke()
                }

//            <--- Hush Service Toggle ---->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(
                        color = if (hushStatus.value) MD3.colorScheme.primary else MD3.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        if (checkNotificationPermission.invoke())
                            viewModel.setHushStatus(!hushStatus.value)
                        else
                            showNotificationPermissionAlertDialog = true
                    }
            )
            {
                Text(
                    text = "Start Hush Service",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (hushStatus.value) MD3.colorScheme.onPrimary else MD3.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .align(alignment = Alignment.CenterVertically)
                )

                Switch(
                    checked = hushStatus.value,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MD3.colorScheme.primaryContainer
                    ),
                    onCheckedChange = { status ->
                        if (checkNotificationPermission.invoke())
                            viewModel.setHushStatus(status)
                        else
                            showNotificationPermissionAlertDialog = true
                    },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            ShowSelectedApps(
                modifier = modifier,
                viewModel,
                onRemoveClick = {
                    viewModel.removeApp(it)
                    Toast.makeText(HushApp.context, "Item Removed", Toast.LENGTH_SHORT).show()
                })
        }
    }

    @Composable
    fun ShowSelectedApps(
        modifier: Modifier,
        viewModel: MainActivityVM,
        onRemoveClick: (SelectedApp) -> Unit
    ) {
        viewModel.getSelectedApp()
        val itemList = viewModel.selectedAppsSF.collectAsState(initial = emptyList())
        LazyColumn(
            modifier = modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            item {
                Box(Modifier.padding(10.dp)) {
                    Text(
                        text = "Ongoing Hush!",
                        fontSize = 16.sp,
                        color = MD3.colorScheme.onBackground
                    )
                }
            }
            items(itemList.value, key = {
                it.selectedApp.id
            }) { app ->
                SelectedAppItem(
                    selectedApp = app,
                    onRemoveClick = onRemoveClick,
                    onConfigDone = { type: HushType, startEndTime: StartEndTime, duration: Long, daysList: List<String?>, logNotification: Boolean ->
                        viewModel.addConfigInSelectedApp(
                            app.selectedApp,
                            type,
                            startEndTime,
                            duration,
                            daysList,
                            logNotification
                        )
                    },
                    onCancelClick = {
                        viewModel.removeApp(app.selectedApp)
                    }
                )
            }
        }
    }

    @Composable
    fun SelectedAppItem(
        selectedApp: SelectedAppForList,
        onRemoveClick: (SelectedApp) -> Unit = {},
        onConfigDone: (type: HushType, startEndTime: StartEndTime, duration: Long, daysList: List<String?>, logNotification: Boolean) -> Unit,
        onCancelClick: () -> Unit
    ) {
        var showOptions by remember {
            mutableStateOf(false)
        }
        var showInitConfig by remember {
            mutableStateOf(false)
        }

        showInitConfig = !selectedApp.selectedApp.isComplete

        Column(modifier = Modifier
            .fillMaxHeight()
            .clickable {
                if (!showInitConfig) {
                    showOptions = !showOptions
                }
            }
            .padding(bottom = 10.dp)
            .background(
                Color(androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer.toArgb()),
                RoundedCornerShape(12.dp)
            )) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Image(
                    painter = rememberDrawablePainter(
                        drawable = selectedApp.icon
                    ),
                    contentDescription = "appIcon",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp)
                ) {
                    Text(
                        text = selectedApp.selectedApp.appName,
                        fontSize = 24.sp,
                        color = MD3.colorScheme.onPrimaryContainer
                    )

                    if (selectedApp.selectedApp.hushType != null) {
                        if (selectedApp.selectedApp.hushType == HushType.DURATION
                            && System.currentTimeMillis() >= selectedApp.selectedApp.timeUpdated + selectedApp.selectedApp.durationInMinutes!! * 60000
                        ) {
                            CustomChip(
                                title = "Expired",
                                color = Color.Red,
                                fontColor = Color.White
                            )
                        } else {
                            CustomChip(
                                title = selectedApp.selectedApp.hushType.label,
                            )
                        }
                    }
                }

            }
            val buttonModifier = Modifier.padding(end = 4.dp)

            AnimatedVisibility(showInitConfig) {
                ShowInitConfig(onConfigDone, onCancelClick)
            }

            AnimatedVisibility(showOptions) {
                ShowOptions(buttonModifier, onRemoveClick, selectedApp)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowInitConfig(
        onConfigDone: (type: HushType, startEndTime: StartEndTime, duration: Long, daysList: List<String?>, logNotification: Boolean) -> Unit,
        onCancelClick: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .wrapContentWidth()
        ) {
            var showTimePickerStart by remember {
                mutableStateOf(false)
            }

            var showTimePickerEnd by remember {
                mutableStateOf(false)
            }

            var logNotificationCb by remember {
                mutableStateOf(false) //This will be true in the fututre
            }
            val startEndTimePair by remember {
                mutableStateOf(StartEndTime("00:00", "23:59"))
            }
            var selectedDays by remember { mutableStateOf(List<String?>(7) { null }) }

            var selectedDuration: Long = 0
            var husType: HushType by remember {
                mutableStateOf(HushType.ALWAYS)
            }

            ShowChipRow {
                husType = it
            };

            AnimatedVisibility(visible = husType == HushType.ALWAYS) {
                // default
            }

            AnimatedVisibility(visible = husType == HushType.DAYS) {
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight(unbounded = true)
                        .padding(top = 8.dp, bottom = 8.dp)
                ) {

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp)
                    ) {
                        FilledTonalIconToggleButton(
                            checked = !selectedDays[0].isNullOrEmpty(),
                            colors = IconButtonDefaults.filledIconToggleButtonColors(
                                containerColor = MD3.colorScheme.surfaceVariant,
                                checkedContainerColor = MD3.colorScheme.tertiary
                            ),
                            onCheckedChange = {
                                selectedDays = selectedDays.toMutableList()
                                    .apply { set(0, if (it) "SAT" else null) }
                            }) {
                            ShowDaysText(!selectedDays[0].isNullOrEmpty(), "SAT")
                        }
                        FilledTonalIconToggleButton(
                            checked = !selectedDays[1].isNullOrEmpty(),
                            colors = IconButtonDefaults.filledIconToggleButtonColors(
                                containerColor = MD3.colorScheme.surfaceVariant,
                                checkedContainerColor = MD3.colorScheme.tertiary
                            ),
                            onCheckedChange = {
                                selectedDays = selectedDays.toMutableList()
                                    .apply { set(1, if (it) "SUN" else null) }
                            }) {
                            ShowDaysText(!selectedDays[1].isNullOrEmpty(), "SUN")
                        }
                        FilledTonalIconToggleButton(
                            checked = !selectedDays[2].isNullOrEmpty(),
                            colors = IconButtonDefaults.filledIconToggleButtonColors(
                                containerColor = MD3.colorScheme.surfaceVariant,
                                checkedContainerColor = MD3.colorScheme.tertiary
                            ),
                            onCheckedChange = {
                                selectedDays = selectedDays.toMutableList()
                                    .apply { set(2, if (it) "MON" else null) }
                            }) {
                            ShowDaysText(!selectedDays[2].isNullOrEmpty(), "MON")
                        }
                        FilledTonalIconToggleButton(
                            checked = !selectedDays[3].isNullOrEmpty(),
                            colors = IconButtonDefaults.filledIconToggleButtonColors(
                                containerColor = MD3.colorScheme.surfaceVariant,
                                checkedContainerColor = MD3.colorScheme.tertiary
                            ),
                            onCheckedChange = {
                                selectedDays = selectedDays.toMutableList()
                                    .apply { set(3, if (it) "TUE" else null) }
                            }) {
                            ShowDaysText(!selectedDays[3].isNullOrEmpty(), "TUE")
                        }
                        FilledTonalIconToggleButton(
                            checked = !selectedDays[4].isNullOrEmpty(),
                            colors = IconButtonDefaults.filledIconToggleButtonColors(
                                containerColor = MD3.colorScheme.surfaceVariant,
                                checkedContainerColor = MD3.colorScheme.tertiary
                            ),
                            onCheckedChange = {
                                selectedDays = selectedDays.toMutableList()
                                    .apply { set(4, if (it) "WED" else null) }
                            }) {
                            ShowDaysText(!selectedDays[4].isNullOrEmpty(), "WED")
                        }
                        FilledTonalIconToggleButton(
                            checked = !selectedDays[5].isNullOrEmpty(),
                            colors = IconButtonDefaults.filledIconToggleButtonColors(
                                containerColor = MD3.colorScheme.surfaceVariant,
                                checkedContainerColor = MD3.colorScheme.tertiary
                            ),
                            onCheckedChange = {
                                selectedDays = selectedDays.toMutableList()
                                    .apply { set(5, if (it) "THU" else null) }
                            }
                        ) {
                            ShowDaysText(!selectedDays[5].isNullOrEmpty(), "THU")
                        }
                        FilledTonalIconToggleButton(
                            checked = !selectedDays[6].isNullOrEmpty(),
                            onCheckedChange = {
                                selectedDays = selectedDays.toMutableList()
                                    .apply { set(6, if (it) "FRI" else null) }
                            },
                            colors = IconButtonDefaults.filledIconToggleButtonColors(
                                containerColor = MD3.colorScheme.surfaceVariant,
                                checkedContainerColor = MD3.colorScheme.tertiary
                            )
                        ) {
                            ShowDaysText(!selectedDays[6].isNullOrEmpty(), "FRI")
                        }
                    }

                    Column(Modifier.padding(start = 8.dp, end = 8.dp)) {
                        Text(
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                            text = "Start Time",
                            color = MD3.colorScheme.onPrimaryContainer
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showTimePickerStart = true
                                }
                        ) {
                            Icon(
                                Icons.Outlined.Edit,
                                contentDescription = "Edit",
                                modifier = Modifier
                                    .size(20.dp)
                            )
                            Text(
                                text = get12HrsFrom24Hrs(startEndTimePair.startTime),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Medium,
                                color = MD3.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(start = 24.dp)
                            )
                        }
                        Text(
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                            text = "End Time",
                            color = MD3.colorScheme.onPrimaryContainer
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showTimePickerEnd = true
                                }
                        ) {
                            Icon(
                                Icons.Outlined.Edit,
                                contentDescription = "Edit",
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = get12HrsFrom24Hrs(startEndTimePair.endTime),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Medium,
                                color = MD3.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(start = 24.dp)
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = husType == HushType.DURATION) {
                DurationSelector {
                    selectedDuration = it
                }
            }

            Row(modifier = Modifier.padding(8.dp)) {
                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    Checkbox(
                        checked = logNotificationCb,
                        onCheckedChange = {
                            logNotificationCb = !logNotificationCb

                            if (it) Constants.showNIY()
                        }
                    )
                }
                Text(
                    text = "Log Notifications",
                    color = Color(androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer.toArgb()),
                    modifier =
                    Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp)
                )
            }

            if (showTimePickerStart) {
                ShowTimePicker(
                    Pair(
                        startEndTimePair.startTime.split(":")[0].toInt(),
                        startEndTimePair.startTime.split(":")[1].toInt()
                    ),
                    "Pick a start time",
                    {
                        Timber.d(it)
                        startEndTimePair.startTime = it
                        showTimePickerStart = false
                    }, {
                        showTimePickerStart = false
                    })
            }
            if (showTimePickerEnd) {
                ShowTimePicker(
                    Pair(
                        startEndTimePair.endTime.split(":")[0].toInt(),
                        startEndTimePair.endTime.split(":")[1].toInt()
                    ),
                    "Pick an end time",
                    {
                        Timber.d(it)
                        startEndTimePair.endTime = it
                        showTimePickerEnd = false
                    }, {
                        showTimePickerEnd = false
                    })
            }

            AddCancelButtonBar(onAddClick = {
                onConfigDone.invoke(
                    husType, startEndTimePair, selectedDuration, selectedDays, logNotificationCb
                )
            }, onCancelClick = {
                onCancelClick.invoke()
            })
        }

    }

    @Composable
    fun AddCancelButtonBar(
        onAddClick: () -> Unit,
        onCancelClick: () -> Unit = {}
    ) {
        Row() {
            val modifier =
                Modifier.padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Cancel",
                color = MD3.colorScheme.onPrimaryContainer,
                modifier = modifier
                    .clickable {
                        onCancelClick.invoke()
                    })

            Text(text = "Add",
                fontWeight = FontWeight.Medium,
                color = MD3.colorScheme.onPrimaryContainer,
                modifier = modifier
                    .clickable {
                        onAddClick.invoke()
                    })
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowTimePicker(
        time: Pair<Int, Int>,
        title: String,
        onTimeSelected: (String) -> Unit,
        onDialogDismiss: () -> Unit
    ) {
        val state = rememberTimePickerState(time.first, time.second)

        Dialog(
            onDismissRequest = { onDialogDismiss.invoke() }, properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MD3.colorScheme.background)
                    .padding(16.dp)
            ) {
                Column(
                    Modifier.background(MD3.colorScheme.background),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = title,
                            fontSize = 24.sp,
                            color = MD3.colorScheme.onBackground,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    TimePicker(state = state)
                    AddCancelButtonBar(onAddClick = {
                        onTimeSelected.invoke(state.hour.toString() + ":" + state.minute)
                    }, onCancelClick = {
                        onDialogDismiss.invoke()
                    })
                }
            }
        }
    }

    @Composable
    fun ShowOptions(
        @SuppressLint("ModifierParameter") buttonModifier: Modifier,
        onRemoveClick: (SelectedApp) -> Unit = {},
        selectedApp: SelectedAppForList
    ) {
        Row(
            modifier = Modifier
                .background(
                    color = MD3.colorScheme.secondaryContainer,
                    RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                )
                .fillMaxWidth()
        ) {
            TextButton(
                modifier = buttonModifier,
                onClick = { Constants.showNIY() }) {
                Text("Notification History", color = MD3.colorScheme.onSecondaryContainer)
            }

            TextButton(
                modifier = buttonModifier,
                onClick = { Constants.showNIY() }) {
                Text("Edit", color = MD3.colorScheme.onSecondaryContainer)
            }

            TextButton(
                modifier = buttonModifier,
                onClick = { onRemoveClick.invoke(selectedApp.selectedApp) }) {
                Text("Remove", color = MD3.colorScheme.onSecondaryContainer)
            }
        }
    }

    @Composable
    fun DurationSelector(getDuration: (Long) -> Unit = {}) {
        var duration by remember { mutableStateOf(0) }
        Row(
            modifier = Modifier
                .background(
                    colorResource(id = R.color.whiteBG), RoundedCornerShape(12.dp)
                )
                .height(48.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (duration > 0) {
                        duration -= 10
                        if (duration >= 60) {
                            duration -= 20
                        }
                    }
                    getDuration.invoke(duration.toLong())
                }, modifier = Modifier
                    .padding(end = 16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    painterResource(id = R.drawable.twotone_remove_circle_24),
                    contentDescription = "Decrease duration by 10 minutes"
                )
            }

            val hours = duration / 60
            val minutes = duration % 60
            Text(
                text = if (hours > 0) {
                    "${hours}h ${minutes}min"
                } else {
                    "${duration} min"
                },
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .align(Alignment.CenterVertically)
            )
            IconButton(
                onClick = {
                    duration += 10
                    if (duration >= 60) {
                        duration += 30
                    }
                    getDuration.invoke(duration.toLong())
                }, modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    painterResource(id = R.drawable.twotone_add_circle_24),
                    contentDescription = "Increase duration by 10 minutes"
                )
            }
        }
    }


    @Composable
    fun CustomChip(
        title: String,
        color: Color = MD3.colorScheme.tertiary,
        fontColor: Color = MD3.colorScheme.onTertiary
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp, bottom = 4.dp)
                .background(
                    color = color,
                    RoundedCornerShape(10.dp)
                )
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(
                    top = 4.dp, bottom = 4.dp, start = 6.dp, end = 6.dp
                ),
                color = fontColor,
                fontSize = 12.sp,
            )
        }

    }

    @Composable
    fun ShowDaysText(selected: Boolean, title: String) {
        if (selected) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MD3.colorScheme.onTertiary
            )
        } else {
            Text(
                text = title,
                color = MD3.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowChipRow(onHushTypeSelected: (HushType) -> Unit = {}) {

        val selectedChipIndex = remember { mutableStateOf(HushType.ALWAYS) }

        Row(modifier = Modifier.fillMaxWidth()) {
            ShowTypeSelectorChip(
                type = HushType.ALWAYS,
                selectedChipIndex = selectedChipIndex,
                onHushTypeSelected = onHushTypeSelected
            )
            ShowTypeSelectorChip(
                type = HushType.DURATION,
                selectedChipIndex = selectedChipIndex,
                onHushTypeSelected = onHushTypeSelected
            )
            ShowTypeSelectorChip(
                type = HushType.DAYS,
                selectedChipIndex = selectedChipIndex,
                onHushTypeSelected = onHushTypeSelected
            )

        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowTypeSelectorChip(
        type: HushType,
        selectedChipIndex: MutableState<HushType>,
        onHushTypeSelected: (HushType) -> Unit
    ) {

        FilterChip(
            selected = selectedChipIndex.value == type,
            onClick = {
                selectedChipIndex.value = type
                onHushTypeSelected.invoke(type)
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MD3.colorScheme.tertiary,
                containerColor = MD3.colorScheme.surfaceVariant,

                ),
            label = {
                Text(
                    text = type.label,
                    color = if (selectedChipIndex.value == type) MD3.colorScheme.onTertiary else MD3.colorScheme.onSurfaceVariant
                )
            },
            modifier = Modifier.padding(start = 4.dp), leadingIcon = {
                Box(
                    Modifier.animateContentSize(keyframes {
                        durationMillis = 100
                    })
                ) {
                    if (selectedChipIndex.value == type) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            tint = MD3.colorScheme.onTertiary,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                }

            })
    }


    fun get12HrsFrom24Hrs(inputTime: String): String {
        val timeFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
        val time = timeFormat.parse(inputTime)
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return outputFormat.format(time)

    }


    data class StartEndTime(var startTime: String, var endTime: String)
}