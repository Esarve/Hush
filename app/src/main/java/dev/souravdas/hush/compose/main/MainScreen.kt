package dev.souravdas.hush.compose.main

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import dev.souravdas.hush.R
import dev.souravdas.hush.arch.MainActivityVM
import dev.souravdas.hush.models.SelectedApp
import dev.souravdas.hush.models.SelectedAppForList
import dev.souravdas.hush.models.UIEvent
import dev.souravdas.hush.nav.AboutScreen
import dev.souravdas.hush.nav.AppLogScreen
import dev.souravdas.hush.nav.SettingsScreen
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
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class, ExperimentalLayoutApi::class
)
@Composable
fun MainActivityScreen(
    viewModel: MainActivityVM = viewModel(),
) {
    viewModel.getInstalledApps()
    viewModel.getSelectedApp()
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val list = viewModel.appListSF.collectAsState()
    val navigator = LocalNavigator.currentOrThrow
    val focusManager = LocalFocusManager.current

    var dropDownMenuExpanded by remember {
        mutableStateOf(false)
    }

    BottomSheetScaffold(
        backgroundColor = MD3.colorScheme.background,
        scaffoldState = scaffoldState,
        sheetPeekHeight = 56.dp,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.app_name),
                        style =MD3.typography.displaySmall,
                        color = MD3.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                },
                actions = {
                    TopAppBarActionButton(
                        imageVector = Icons.Outlined.MoreVert,
                        description = "Options"
                    ) {
                        // show the drop down menu
                        dropDownMenuExpanded = true
                    }

                    // drop down menu
                    DropdownMenu(
                        expanded = dropDownMenuExpanded,
                        onDismissRequest = {
                            dropDownMenuExpanded = false
                        },
                        offset = DpOffset(x = 10.dp, y = (-60).dp)
                    ) {

                        DropdownMenuItem(onClick = {
                            navigator.push(SettingsScreen())
                            dropDownMenuExpanded = false
                        }) {
                            Text("Settings")
                        }

                        DropdownMenuItem(onClick = {
                            navigator.push(AboutScreen())
                            dropDownMenuExpanded = false
                        }) {
                            Text("About")
                        }
                    }
                })
        },
        sheetContent = {
            InstalledAppList(items = list.value) { item ->
                scope.launch {
                    focusManager.clearFocus()
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
    ) { mo ->
        val selectedList by viewModel.selectedAppsSF.collectAsState(emptyList())
        Timber.d(selectedList.size.toString())
        val modifier = Modifier
            .consumeWindowInsets(mo)
            .padding(horizontal = 16.dp)
        val hushStatus = viewModel.getHushStatusAsFlow(Constants.DS_HUSH_STATUS).collectAsState(initial = false)
        val notificationPermissionStatus by viewModel.getHushStatusAsFlow(Constants.DS_NOTIFICATION_PERMISSION).collectAsState(initial = true)
        val listState = rememberLazyListState()

        var showNotificationPermissionAlertDialog by remember {
            mutableStateOf(false)
        }
        LaunchedEffect(notificationPermissionStatus){
            showNotificationPermissionAlertDialog = !notificationPermissionStatus
        }

        val setHushStatus = remember<(Boolean) -> Unit> {
            { status ->
                if (notificationPermissionStatus)
                    viewModel.setHushStatus(status)
                else
                    showNotificationPermissionAlertDialog = true
            }
        }

        val editAppLambda = remember<(SelectedApp) -> Unit> {
            { app ->
                viewModel.updateComplete(app)
                scope.launch {
                    listState.scrollToItem(index = 1)
                }
            }
        }
        val removeAppLambda = remember<(SelectedApp) -> Unit> {
            { app ->
                viewModel.removeApp(app)
            }
        }
        val addConfigLambda = remember<(AppConfig) -> Unit> {
            { app ->
                viewModel.addConfigInSelectedApp(app)
            }
        }

        if (showNotificationPermissionAlertDialog)
            ShowAlertDialog {
                showNotificationPermissionAlertDialog = false
                viewModel.dispatchUIEvent(UIEvent.invokeNotificationPermissionGet)
            }

//            <--- Hush Service Toggle ---->
        Row(
            modifier = modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        )
        {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = if (hushStatus.value) MD3.colorScheme.primaryContainer else MD3.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clickable {
                            if (notificationPermissionStatus)
                                viewModel.setHushStatus(!hushStatus.value)
                            else
                                showNotificationPermissionAlertDialog = true
                        }
                ) {
                    Text(
                        text = "Start Hush Service",
                        style = MD3.typography.titleMedium,
                        fontSize = 18.sp,
                        fontWeight = if (hushStatus.value) FontWeight.Bold else FontWeight.Normal,
                        color = if (hushStatus.value) MD3.colorScheme.onPrimaryContainer else MD3.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .align(alignment = Alignment.CenterVertically)
                    )

                    Switch(
                        checked = hushStatus.value,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MD3.colorScheme.primaryContainer
                        ),
                        onCheckedChange = setHushStatus,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

                AnimatedVisibility(visible = !hushStatus.value) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color(0xFFFFE082),
                                RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                            )
                    ) {
                        Text(
                            text = "Turn on hush service to mute notifications",
                            style = MD3.typography.labelMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

        }

        Box(modifier.padding(vertical = 16.dp)) {
            Text(
                text = "Ongoing Hush!",
                style = MD3.typography.titleMedium,
                color = MD3.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        LazyColumn(
            state = listState,
            modifier = modifier
                .fillMaxSize()
        ) {
            items(selectedList, key = {
                it.selectedApp.timeUpdated
            }) { app ->
                SelectedAppItem(
                    selectedApp = app,
                    onRemoveClick = removeAppLambda,
                    onEditClick = editAppLambda,
                    onConfigDone = addConfigLambda,
                    onNotificationLogClick = {
                        navigator.push(AppLogScreen(app_id = app.selectedApp.id.toLong(), appName = app.selectedApp.appName))
                    }
                )
            }
        }
    }
}

@Composable
fun TopAppBarActionButton(
    imageVector: ImageVector,
    description: String,
    onClick: () -> Unit
) {
    IconButton(onClick = {
        onClick()
    }) {
        Icon(imageVector = imageVector, contentDescription = description)
    }
}

@Composable
fun SelectedAppItem(
    selectedApp: SelectedAppForList,
    onRemoveClick: (SelectedApp) -> Unit = {},
    onEditClick: (SelectedApp) -> Unit,
    onConfigDone: (AppConfig) -> Unit,
    onNotificationLogClick: (SelectedApp) -> Unit
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
                        selectedApp.selectedApp.hushType?.let {
                            CustomChip(
                                title = it.label,
                            )
                        }
                    }
                }
            }

        }
        val buttonModifier = Modifier.padding(end = 4.dp)

        AnimatedVisibility(showInitConfig) {
            ShowInitConfig(selectedApp.selectedApp, onConfigDone, onRemoveClick)
        }

        AnimatedVisibility(showOptions) {
            ShowOptions(
                buttonModifier,
                onRemoveClick,
                onEditClick,
                selectedApp,
                onNotificationLogClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowInitConfig(
    app: SelectedApp,
    onConfigDone: (AppConfig) -> Unit,
    onRemoveClick: (SelectedApp) -> Unit
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
            mutableStateOf(true)
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
                val buttonColor = IconButtonDefaults.outlinedIconToggleButtonColors(
                        containerColor = MD3.colorScheme.primaryContainer,
                        checkedContainerColor = MD3.colorScheme.tertiary,
                    )
                val modifier = Modifier
                    .height(36.dp)
                    .width(36.dp)

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp)
                ) {
                    OutlinedIconToggleButton(
                        modifier = modifier,
                        checked = !selectedDays[0].isNullOrEmpty(),
                        colors = buttonColor,
                        onCheckedChange = {
                            selectedDays = selectedDays.toMutableList()
                                .apply { set(0, if (it) "SAT" else null) }
                        }) {
                        ShowDaysText(!selectedDays[0].isNullOrEmpty(), "SAT")
                    }
                    OutlinedIconToggleButton(
                        modifier = modifier,
                        checked = !selectedDays[1].isNullOrEmpty(),
                        colors = buttonColor,
                        onCheckedChange = {
                            selectedDays = selectedDays.toMutableList()
                                .apply { set(1, if (it) "SUN" else null) }
                        }) {
                        ShowDaysText(!selectedDays[1].isNullOrEmpty(), "SUN")
                    }
                    OutlinedIconToggleButton(
                        modifier = modifier,
                        checked = !selectedDays[2].isNullOrEmpty(),
                        colors = buttonColor,
                        onCheckedChange = {
                            selectedDays = selectedDays.toMutableList()
                                .apply { set(2, if (it) "MON" else null) }
                        }) {
                        ShowDaysText(!selectedDays[2].isNullOrEmpty(), "MON")
                    }
                    OutlinedIconToggleButton(
                        modifier = modifier,
                        checked = !selectedDays[3].isNullOrEmpty(),
                        colors = buttonColor,
                        onCheckedChange = {
                            selectedDays = selectedDays.toMutableList()
                                .apply { set(3, if (it) "TUE" else null) }
                        }) {
                        ShowDaysText(!selectedDays[3].isNullOrEmpty(), "TUE")
                    }
                    OutlinedIconToggleButton(
                        modifier = modifier,
                        checked = !selectedDays[4].isNullOrEmpty(),
                        colors = buttonColor,
                        onCheckedChange = {
                            selectedDays = selectedDays.toMutableList()
                                .apply { set(4, if (it) "WED" else null) }
                        }) {
                        ShowDaysText(!selectedDays[4].isNullOrEmpty(), "WED")
                    }
                    OutlinedIconToggleButton(
                        modifier = modifier,
                        checked = !selectedDays[5].isNullOrEmpty(),
                        colors = buttonColor,
                        onCheckedChange = {
                            selectedDays = selectedDays.toMutableList()
                                .apply { set(5, if (it) "THU" else null) }
                        }
                    ) {
                        ShowDaysText(!selectedDays[5].isNullOrEmpty(), "THU")
                    }
                    OutlinedIconToggleButton(
                        modifier = modifier,
                        checked = !selectedDays[6].isNullOrEmpty(),
                        onCheckedChange = {
                            selectedDays = selectedDays.toMutableList()
                                .apply { set(6, if (it) "FRI" else null) }
                        },
                        colors = buttonColor
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
                            tint = MD3.colorScheme.onPrimaryContainer,
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
                            tint = MD3.colorScheme.onPrimaryContainer,
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
            CompositionLocalProvider(androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement provides false) {
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
                AppConfig(
                    app,
                    husType,
                    startEndTimePair,
                    selectedDuration,
                    selectedDays,
                    logNotificationCb
                )
            )
        }, onCancelClick = {
            onRemoveClick.invoke(app)
        })
    }

}

@Composable
fun ShowOptions(
    @SuppressLint("ModifierParameter") buttonModifier: Modifier,
    onRemoveClick: (SelectedApp) -> Unit = {},
    onEditClick: (SelectedApp) -> Unit,
    selectedApp: SelectedAppForList,
    onNotificationLogClick: (SelectedApp) -> Unit = {}
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
            onClick = { onNotificationLogClick.invoke(selectedApp.selectedApp) }) {
            Text("Notification History", color = MD3.colorScheme.onSecondaryContainer)
        }

        TextButton(
            modifier = buttonModifier,
            onClick = { onEditClick.invoke(selectedApp.selectedApp) }) {
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
                MD3.colorScheme.secondaryContainer, RoundedCornerShape(12.dp)
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
                contentDescription = "Decrease duration by 10 minutes",
                tint = MD3.colorScheme.onSecondaryContainer
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
            color = MD3.colorScheme.onSecondaryContainer,
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
                contentDescription = "Increase duration by 10 minutes",
                tint = MD3.colorScheme.onSecondaryContainer
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
            color = MD3.colorScheme.onPrimaryContainer,
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

private fun get12HrsFrom24Hrs(inputTime: String): String {
    val timeFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
    val time = timeFormat.parse(inputTime)
    val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return outputFormat.format(time)

}


data class StartEndTime(var startTime: String, var endTime: String)

@Immutable
data class AppConfig(
    val selectedApp: SelectedApp,
    val type: HushType,
    val startEndTime: StartEndTime,
    val duration: Long,
    val daysList: List<String?>,
    val logNotification: Boolean
)