package dev.souravdas.hush.compose.main

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.souravdas.hush.BuildConfig
import dev.souravdas.hush.HushApp
import dev.souravdas.hush.R
import dev.souravdas.hush.arch.MainActivityVM
import dev.souravdas.hush.compose.HushChart
import dev.souravdas.hush.models.InstalledPackageInfo
import dev.souravdas.hush.models.SelectedApp
import dev.souravdas.hush.models.UIEvent
import dev.souravdas.hush.nav.TransitionAnimation
import dev.souravdas.hush.others.*
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.MaterialTheme as MD3

/**
 * Created by Sourav
 * On 2/22/2023 11:45 PM
 * For Hush!
 */

@RootNavGraph(true)
@Destination(style = TransitionAnimation::class)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home(viewModel: MainActivityVM, navigator: DestinationsNavigator) {

    Timber.d("Main Screen Recomposed")
    val selectedListState = viewModel.selectedAppsSF.collectAsState(emptyList())
    val selectedList by remember { selectedListState }
    val scope = rememberCoroutineScope()
    val hushStatus =
        viewModel.getHushStatusAsFlow(Constants.DS_HUSH_STATUS).collectAsState(initial = true)
    val _logStats = viewModel.appLogStats.collectAsState()
    val logState by remember {
        mutableStateOf(_logStats)
    }

    val listState = rememberLazyListState()


    val setHushStatus = remember<(Boolean) -> Unit> {
        { status ->
            viewModel.setHushStatus(status)
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
            viewModel.setHushStatus(true)
        }
    }

    val generateDummyDataLambda = remember {
        {
            if (BuildConfig.DEBUG) {
                viewModel.generateDummyLogs()
                Toast.makeText(HushApp.context, "Generated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val modifier = Modifier

    Column {
        Column(
            modifier = modifier
                .padding(top = 8.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Hush",
                    style = MD3.typography.displaySmall,
                    fontSize = 42.sp,
                    color = MD3.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Row() {
                    FilledTonalIconButton(
                        onClick = {
                            viewModel.dispatchUIEvent(UIEvent.InvokeSettingsPageOpen)
                        },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MD3.colorScheme.surfaceVariant,
                            contentColor = MD3.colorScheme.onSurfaceVariant,
                        )
                    ) {
                        Icon(Icons.Rounded.Settings, contentDescription = "Settings")
                    }

                    FilledTonalIconToggleButton(
                        checked = hushStatus.value,
                        onCheckedChange = {
                            setHushStatus(it)
                        },
                        colors = IconButtonDefaults.filledIconToggleButtonColors(
                            containerColor = MD3.colorScheme.surfaceVariant,
                            contentColor = MD3.colorScheme.onSurfaceVariant,
                            checkedContainerColor = MD3.colorScheme.primary,
                            checkedContentColor = MD3.colorScheme.onPrimary
                        )
                    ) {
                        Icon(painterResource(id = R.drawable.ic_power), contentDescription = "IDK")
                    }
                }
            }

            AnimatedVisibility(!hushStatus.value) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MD3.colorScheme.errorContainer, RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {
                    Icon(
                        Icons.Rounded.Info,
                        contentDescription = "Info",
                        tint = MD3.colorScheme.error
                    )
                    Text(
                        text = "Hush service is currently off",
                        style = MD3.typography.labelLarge,
                        color = MD3.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            when (logState.value) {
                is Resource.Error -> TODO("Show ERROR")
                is Resource.Loading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(128.dp)
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is Resource.Success -> {
                    HushChart(
                        dataMap = { (logState.value as Resource.Success<Map<LocalDate, Float>>).data },
                        onRefreshClick = viewModel::getLogStats
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .background(
                    MD3.colorScheme.background, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .padding(horizontal = 16.dp)
        ) {
            Box(modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "Ongoing Hush!",
                    style = MD3.typography.titleMedium,
                    color = MD3.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            LazyColumn(
                state = listState, modifier = modifier.fillMaxSize()
            ) {
                items(
                    selectedList,
                    key = {
                        it.timeUpdated
                    },
                ) { app ->
                    SelectedAppItem(
                        selectedApp = app,
                        onRemoveClick = removeAppLambda,
                        onEditClick = editAppLambda,
                        onConfigDone = addConfigLambda,
                        modifier.animateItemPlacement()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ShowBottomSheet(
    apps: List<InstalledPackageInfo>,
    isBottomSheetOpen: () -> Boolean,
    onDismiss: (SelectedApp?) -> Unit
) {

    val scope = rememberCoroutineScope()

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    if (isBottomSheetOpen.invoke())
        ModalBottomSheet(
            onDismissRequest = { onDismiss.invoke(null) }, sheetState = bottomSheetState
        ) {
            InstalledAppList(apps, { item ->
                scope.launch {
                    bottomSheetState.hide()
                }.invokeOnCompletion {
                    // TODO: insert here
                    onDismiss.invoke(item)
                }
            }, {
                scope.launch {
                    bottomSheetState.expand()
                }
            })
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedAppItem(
    selectedApp: SelectedApp,
    onRemoveClick: (SelectedApp) -> Unit = {},
    onEditClick: (SelectedApp) -> Unit,
    onConfigDone: (AppConfig) -> Unit,
    modifier: Modifier
) {
    var showOptions by remember {
        mutableStateOf(false)
    }
    var showInitConfig by remember {
        mutableStateOf(false)
    }

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

    LaunchedEffect(selectedApp) {
        showInitConfig = !selectedApp.isComplete
    }



    Card(
        onClick = {
            if (!showInitConfig) {
                showOptions = !showOptions
            }
        },
        modifier = modifier
            .fillMaxHeight()
            .padding(bottom = 10.dp),
        colors = CardDefaults.cardColors(MD3.colorScheme.secondaryContainer),
    ) {
        Column() {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Image(
                    painter = rememberDrawablePainter(
                        drawable = AppIconsMap.appIconMap[selectedApp.packageName]
                    ),
                    contentDescription = "appIcon",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(start = 8.dp)
                        .clip(CircleShape)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 8.dp)
                ) {
                    Text(
                        text = selectedApp.appName,
                        style = MD3.typography.headlineMedium,
                        fontSize = 24.sp,
                        color = MD3.colorScheme.onPrimaryContainer
                    )

                    AnimatedVisibility(!showInitConfig) {
                        Row {
                            if (selectedApp.hushType == HushType.DURATION && System.currentTimeMillis() >= selectedApp.timeUpdated + selectedApp.durationInMinutes!! * 60000) {
                                CustomChip(
                                    title = "Expired",
                                    color = MD3.colorScheme.error,
                                    fontColor = MD3.colorScheme.onError
                                )
                            } else {
                                selectedApp.hushType?.let {
                                    CustomChip(
                                        title = it.label,
                                    )
                                }
                            }
                        }
                    }
                    AnimatedVisibility(showInitConfig) {
                        Row {
                            OutLinedButton(
                                containerColor = MD3.colorScheme.errorContainer,
                                contentColor = MD3.colorScheme.error,
                                icon = Icons.Rounded.Close
                            ) {
                                onRemoveClick.invoke(selectedApp)
                            }
                            OutLinedButton(
                                containerColor = ExtraColors.successContainer.getColor(
                                    isSystemInDarkTheme()
                                ), contentColor = ExtraColors.success.getColor(
                                    isSystemInDarkTheme()
                                ), icon = Icons.Rounded.Done
                            ) {
                                onConfigDone.invoke(
                                    AppConfig(
                                        selectedApp,
                                        husType,
                                        startEndTimePair,
                                        selectedDuration,
                                        selectedDays,
                                        logNotificationCb
                                    )
                                )
                            }
                        }
                    }


                }

            }
            val buttonModifier = Modifier.padding(end = 4.dp)

            AnimatedVisibility(showInitConfig) {
                Column(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .wrapContentWidth()
                ) {
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
                            val daySelectorMap = mapOf<Int, String>(
                                0 to "SAT",
                                1 to "SUN",
                                2 to "MON",
                                3 to "TUE",
                                4 to "WED",
                                5 to "THU",
                                6 to "FRI"
                            )

                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 8.dp)
                            ) {

                                daySelectorMap.forEach { item ->
                                    DaySelector(
                                        checked = !selectedDays[item.key].isNullOrEmpty(),
                                        modifier = modifier,
                                        onCheckedChange = {
                                            selectedDays = selectedDays.toMutableList()
                                                .apply {
                                                    set(
                                                        item.key,
                                                        if (it) item.value else null
                                                    )
                                                }
                                        },
                                        title = item.value
                                    )
                                }

                            }

                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                            )
                            {
                                TimeSelect(label = "Start Time",
                                    time = startEndTimePair.startTime,
                                    modifier
                                        .weight(0.5f)
                                        .clip(
                                            RoundedCornerShape(16.dp)
                                        )
                                        .clickable {
                                            showTimePickerStart = true
                                        })
                                TimeSelect(
                                    label = "End Time",
                                    time = startEndTimePair.endTime,
                                    modifier
                                        .weight(0.5f)
                                        .clip(
                                            RoundedCornerShape(16.dp)
                                        )
                                        .clickable {
                                            showTimePickerEnd = true
                                        })
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
                            Checkbox(checked = logNotificationCb, onCheckedChange = {
                                logNotificationCb = !logNotificationCb

                                if (it) Constants.showNIY()
                            })
                        }
                        Text(
                            text = "Log Notifications",
                            color = Color(androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer.toArgb()),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 8.dp)
                        )
                    }

                    if (showTimePickerStart) {
                        ShowTimePicker(time = Pair(
                            startEndTimePair.startTime.split(":")[0].toInt(),
                            startEndTimePair.startTime.split(":")[1].toInt()
                        ), title = "Pick a start time", onTimeSelected = {
                            Timber.d(it)
                            startEndTimePair.startTime = it
                            showTimePickerStart = false
                        }, onDialogDismiss = {
                            showTimePickerStart = false
                        })
                    }
                    if (showTimePickerEnd) {
                        ShowTimePicker(Pair(
                            startEndTimePair.endTime.split(":")[0].toInt(),
                            startEndTimePair.endTime.split(":")[1].toInt()
                        ), "Pick an end time", {
                            Timber.d(it)
                            startEndTimePair.endTime = it
                            showTimePickerEnd = false
                        }, {
                            showTimePickerEnd = false
                        })
                    }
                }
            }

            AnimatedVisibility(showOptions) {
                ShowOptions(
                    buttonModifier,
                    onRemoveClick,
                    onEditClick,
                    selectedApp,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutLinedButton(
    containerColor: Color,
    contentColor: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        OutlinedIconButton(
            onClick = onClick,
            colors = IconButtonDefaults.outlinedIconButtonColors(
                containerColor = containerColor,
                contentColor = contentColor
            ),
            border = BorderStroke(1.dp, contentColor),
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .size(38.dp, 38.dp)
        ) {
            Icon(icon, contentDescription = "Close")
        }
    }
}

@Composable
fun TimeSelect(label: String, time: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_clock),
            contentDescription = "clock",
            tint = MD3.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(32.dp, 32.dp)
        )

        Column(horizontalAlignment = Alignment.Start) {
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = label,
                style = MD3.typography.titleSmall,
                color = MD3.colorScheme.onSecondaryContainer
            )
            Text(
                text = get12HrsFrom24Hrs(time),
                style = MD3.typography.bodyLarge,
                color = MD3.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
fun DaySelector(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier,
    title: String
) {
    OutlinedIconToggleButton(
        modifier = modifier,
        checked = checked,
        colors = IconButtonDefaults.outlinedIconToggleButtonColors(
            containerColor = MD3.colorScheme.secondaryContainer,
            checkedContainerColor = MD3.colorScheme.tertiary,
        ),
        border = BorderStroke(
            1.dp,
            if (checked) MD3.colorScheme.secondary else MD3.colorScheme.primary
        ),
        onCheckedChange = onCheckedChange
    ) {
        ShowDaysText(checked, title = title)
    }
}

@Composable
fun ShowOptions(
    @SuppressLint("ModifierParameter") buttonModifier: Modifier,
    onRemoveClick: (SelectedApp) -> Unit = {},
    onEditClick: (SelectedApp) -> Unit,
    selectedApp: SelectedApp,
) {
    Row(
        modifier = Modifier
            .background(
                color = MD3.colorScheme.secondaryContainer,
                RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
            )
            .fillMaxWidth()
    ) {
        TextButton(modifier = buttonModifier, onClick = { onEditClick.invoke(selectedApp) }) {
            Text("Edit", color = MD3.colorScheme.onSecondaryContainer)
        }

        TextButton(modifier = buttonModifier, onClick = { onRemoveClick.invoke(selectedApp) }) {
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
                MD3.colorScheme.tertiaryContainer, RoundedCornerShape(12.dp)
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
                tint = MD3.colorScheme.onTertiaryContainer
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
            color = MD3.colorScheme.onTertiaryContainer,
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
                tint = MD3.colorScheme.onTertiaryContainer
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomChip(
    title: String,
    color: Color = MD3.colorScheme.tertiary,
    fontColor: Color = MD3.colorScheme.onTertiary
) {
    Box(
        modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = color)
    ) {
        Text(
            text = title,
            style = MD3.typography.labelMedium,
            modifier = Modifier.padding(
                top = 4.dp, bottom = 4.dp, start = 8.dp, end = 8.dp
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
            text = title, color = MD3.colorScheme.onSecondaryContainer, fontSize = 12.sp
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

    FilterChip(selected = selectedChipIndex.value == type, onClick = {
        selectedChipIndex.value = type
        onHushTypeSelected.invoke(type)
    }, colors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MD3.colorScheme.tertiary,
        containerColor = MD3.colorScheme.surfaceVariant,

        ), label = {
        Text(
            text = type.label,
            color = if (selectedChipIndex.value == type) MD3.colorScheme.onTertiary else MD3.colorScheme.onSurfaceVariant
        )
    }, modifier = Modifier.padding(start = 4.dp), leadingIcon = {
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

class Entry(
    val localDate: LocalDate,
    override val x: Float,
    override val y: Float,
) : ChartEntry {
    override fun withY(y: Float) = Entry(localDate, x, y)
}