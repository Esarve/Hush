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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
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
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import dev.souravdas.hush.HushApp
import dev.souravdas.hush.R
import dev.souravdas.hush.arch.MainActivityVM
import dev.souravdas.hush.models.InstalledPackageInfo
import dev.souravdas.hush.models.SelectedApp
import dev.souravdas.hush.models.SelectedAppForList
import dev.souravdas.hush.others.Constants
import dev.souravdas.hush.others.HushType
import kotlinx.coroutines.launch
import org.threeten.bp.LocalTime
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Sourav
 * On 2/22/2023 11:45 PM
 * For Hush!
 */
class UIKit()  {
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
                        .background(color = colorResource(id = R.color.color_pale_green))
                ) {
                    Text(
                        text = "Swipe up to Expand the sheet",
                        modifier = Modifier.align(alignment = Alignment.Center),
                        color = Color.White
                    )
                }
                val lazyListState = rememberLazyListState()
                LazyColumn(state = lazyListState) {
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
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        )
                    }
                }
            }

        }
    }

    @OptIn(
        ExperimentalMaterial3Api::class,
        ExperimentalLayoutApi::class, ExperimentalMaterialApi::class
    )
    @Composable
    fun MainActivityScreen(
        viewModel: MainActivityVM = viewModel(),
        onItemSelected: (SelectedApp) -> Unit = {},
    ) {
        val scope = rememberCoroutineScope()
        val scaffoldState = rememberBottomSheetScaffoldState()
        val list = viewModel.appListSF.collectAsState()
        viewModel.getInstalledApps()

        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 56.dp,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.app_name)) }
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
                    contentColor = Color.White,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.twotone_add_24),
                        contentDescription = "Add Icon"
                    )
                }
            },
        ) { it ->
            val modifier = Modifier.consumeWindowInsets(it)
            val hushStatus = viewModel.getHushStatusAsFlow().collectAsState(initial = false)

//            <--- Hush Service Toggle ---->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(
                        color = if (hushStatus.value) colorResource(id = R.color.color_pale_green) else colorResource(
                            R.color.grayBG
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        viewModel.setHushStatus(!hushStatus.value)
                    }
            )
            {
                Text(
                    text = "Start Hush Service",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .align(alignment = Alignment.CenterVertically)
                )

                Switch(
                    checked = hushStatus.value,
                    onCheckedChange = { status ->
                        viewModel.setHushStatus(status)
                    },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            ShowSelectedApps(
                modifier,
                viewModel,
                onRemoveClick = {
                    viewModel.removeApp(it)
                    Toast.makeText(HushApp.context, "Item Removed", Toast.LENGTH_SHORT).show()
                })
        }
    }

    @Composable
    fun ShowSelectedApps(
        modifier: Modifier = Modifier,
        viewModel: MainActivityVM,
        onRemoveClick: (SelectedApp) -> Unit
    ) {
        viewModel.getSelectedApp()
        val itemList = viewModel.selectedAppsSF.collectAsState(initial = emptyList())
        Box(modifier = modifier.padding(8.dp)) {
            LazyColumn {
                item {
                    Box(Modifier.padding(10.dp)) {
                        Text(text = "Ongoing Hush!", fontSize = 16.sp)
                    }
                }
                items(itemList.value) { app ->
                    SelectedAppItem(
                        selectedApp = app,
                        onRemoveClick = onRemoveClick,
                        onConfigDone = { type: HushType, startEndTime: StartEndTime, duration: Long, daysList: List<String?>, logNotification: Boolean ->
                            viewModel.addConfigInSelectedApp(app.selectedApp,type,startEndTime,duration,daysList,logNotification)
                        },
                        onCancelClick = {
                            viewModel.removeApp(app.selectedApp)
                        }
                    )
                }
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
        Box(
            modifier = Modifier
                .clickable {
                    if (!showInitConfig) {
                        showOptions = !showOptions
                    }
                }
                .padding(bottom = 10.dp)
                .background(
                    colorResource(id = R.color.whiteBG), RoundedCornerShape(12.dp)
                )
                .padding(top = 8.dp, bottom = 8.dp)


        ) {
            Column() {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Image(
                        painter = rememberDrawablePainter(
                            drawable = selectedApp.icon
                        ),
                        contentDescription = "appIcon",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp)
                    ) {
                        Text(
                            text = selectedApp.selectedApp.appName,
                            fontSize = 24.sp,
                        )

                        if(selectedApp.selectedApp.hushType != null){
                            if (selectedApp.selectedApp.hushType == HushType.DURATION
                                && System.currentTimeMillis() >= selectedApp.selectedApp.timeUpdated + selectedApp.selectedApp.durationInMinutes!! * 60000
                            ) {
                                CustomChip(title = "Expired", color = Color.Red)
                            } else {
                                CustomChip(
                                    title = selectedApp.selectedApp.hushType.toString(),
                                    color = colorResource(id = R.color.color_lavender)
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
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowInitConfig(
        onConfigDone: (type: HushType, startEndTime: StartEndTime, duration: Long, daysList: List<String?>, logNotification: Boolean) -> Unit,
        onCancelClick: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .padding(start = 8.dp, end = 8.dp)
        ) {

            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
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
                var startEndTimePair by remember {
                    mutableStateOf(StartEndTime("00:00", "23:59"))
                }
                var selectedDayList = emptyList<String?>()
                var selectedDuration: Long = 0
                var husType: HushType by remember {
                    mutableStateOf(HushType.ALWAYS)
                }

                ShowChipRow {
                    husType = it
                };

                when (husType) {
                    HushType.ALWAYS -> {}

                    HushType.DAYS -> Column(
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight(unbounded = true)
                            .padding(top = 8.dp, bottom = 8.dp)
                    ) {
                        ShowDays {
                            selectedDayList = it
                        }
                        Column(Modifier.padding(start = 8.dp, end = 8.dp)) {
                            Text(
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                                text = "Start Time"
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
                                    modifier = Modifier.padding(start = 24.dp)
                                )
                            }
                            Text(
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                                text = "End Time"
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
                                    modifier = Modifier.padding(start = 24.dp)
                                )
                            }
                        }
                    }

                    HushType.DURATION -> DurationSelector {
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
                        Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp)
                    )
                }

                if (showTimePickerStart) {
                    ShowTimePicker(
                        Pair(startEndTimePair.startTime.split(":")[0].toInt(), startEndTimePair.startTime.split(":")[1].toInt()),
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
                        Pair(startEndTimePair.endTime.split(":")[0].toInt(), startEndTimePair.endTime.split(":")[1].toInt()),
                        "Pick an end time",
                        {
                            Timber.d(it)
                            startEndTimePair.endTime = it
                            showTimePickerEnd = false
                        }, {
                            showTimePickerEnd = false
                        })
                }

                AddCancelButtonBar(onAddClick = {onConfigDone.invoke(
                    husType,startEndTimePair,selectedDuration,selectedDayList,logNotificationCb
                )}, onCancelClick = {
                    onCancelClick.invoke()
                })
            }
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

            Text(text = "Cancel", modifier = modifier
                .clickable {
                    onCancelClick.invoke()
                })

            Text(text = "Add", fontWeight = FontWeight.Medium,
                modifier = modifier
                    .clickable {
                        onAddClick.invoke()
                    })
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowTimePicker(
        time: Pair<Int,Int>,
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
                    .background(MaterialTheme.colors.background)
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
                        Text(text = title, fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))
                    }
                    TimeInput(state = state)
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
                    colorResource(R.color.color_light_yellow),
                    RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                )
                .fillMaxWidth()
        ) {
            TextButton(
                modifier = buttonModifier,
                onClick = { Constants.showNIY() }) {
                Text("Notification History")
            }

            TextButton(
                modifier = buttonModifier,
                onClick = { Constants.showNIY() }) {
                Text("Edit")
            }

            TextButton(
                modifier = buttonModifier,
                onClick = { onRemoveClick.invoke(selectedApp.selectedApp) }) {
                Text("Remove")
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
    fun ShowDays(onSelectedDays: (List<String?>) -> Unit = {}) {

        var selectedDays by remember { mutableStateOf(List<String?>(7) { null }) }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp)
        ) {
            FilledTonalIconToggleButton(checked = !selectedDays[0].isNullOrEmpty(), onCheckedChange = {
                selectedDays = selectedDays.toMutableList().apply { set(0, "SAT") }
                onSelectedDays.invoke(selectedDays)
            }) {
                ShowDaysText(!selectedDays[0].isNullOrEmpty(), "SAT")
            }
            FilledTonalIconToggleButton(checked = !selectedDays[1].isNullOrEmpty(), onCheckedChange = {
                selectedDays = selectedDays.toMutableList().apply { set(1, "SUN") }
                onSelectedDays.invoke(selectedDays)
            }) {
                ShowDaysText(!selectedDays[1].isNullOrEmpty(), "SUN")
            }
            FilledTonalIconToggleButton(checked = !selectedDays[2].isNullOrEmpty(), onCheckedChange = {
                selectedDays = selectedDays.toMutableList().apply { set(2, "MON") }
                onSelectedDays.invoke(selectedDays)
            }) {
                ShowDaysText(!selectedDays[2].isNullOrEmpty(), "MON")
            }
            FilledTonalIconToggleButton(checked = !selectedDays[3].isNullOrEmpty(), onCheckedChange = {
                selectedDays = selectedDays.toMutableList().apply { set(3, "TUE") }
                onSelectedDays.invoke(selectedDays)
            }) {
                ShowDaysText(!selectedDays[3].isNullOrEmpty(), "TUE")
            }
            FilledTonalIconToggleButton(checked = !selectedDays[4].isNullOrEmpty(), onCheckedChange = {
                selectedDays = selectedDays.toMutableList().apply { set(4, "WED") }
                onSelectedDays.invoke(selectedDays)
            }) {
                ShowDaysText(!selectedDays[4].isNullOrEmpty(), "WED")
            }
            FilledTonalIconToggleButton(checked = !selectedDays[5].isNullOrEmpty(), onCheckedChange = {
                selectedDays = selectedDays.toMutableList().apply { set(5, "THU") }
                onSelectedDays.invoke(selectedDays)
            }) {
                ShowDaysText(!selectedDays[5].isNullOrEmpty(), "THU")
            }
            FilledTonalIconToggleButton(checked = !selectedDays[6].isNullOrEmpty(), onCheckedChange = {
                selectedDays = selectedDays.toMutableList().apply { set(6, "FRI") }
                onSelectedDays.invoke(selectedDays)
            }) {
                ShowDaysText(!selectedDays[6].isNullOrEmpty(), "FRI")
            }
        }
    }

    @Composable
    fun CustomChip(title: String, color: Color, fontColor: Color = Color.White) {
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
                    top = 2.dp, bottom = 2.dp, start = 6.dp, end = 6.dp
                ),
                color = fontColor,
                fontSize = 12.sp,
            )
        }

    }

    @Composable
    fun ShowDaysText(selected: Boolean, title: String) {
        if (selected) {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        } else {
            Text(text = title, fontSize = 12.sp)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowChipRow(onHushTypeSelected: (HushType) -> Unit = {}) {

        var selectedChipIndex by remember { mutableStateOf(HushType.ALWAYS) }

        Row(modifier = Modifier.fillMaxWidth()) {
            val chipModifier = Modifier.padding(start = 4.dp)

            FilterChip(selected = selectedChipIndex == HushType.ALWAYS, onClick = {
                selectedChipIndex = HushType.ALWAYS
                onHushTypeSelected.invoke(HushType.ALWAYS)
            }, label = { Text("Mute Always") }, modifier = chipModifier, leadingIcon = {
                Box(
                    Modifier.animateContentSize(keyframes {
                        durationMillis = 100
                    })
                ) {
                    if (selectedChipIndex == HushType.ALWAYS) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                }

            })

            FilterChip(selected = selectedChipIndex == HushType.DURATION, onClick = {
                selectedChipIndex = HushType.DURATION
                onHushTypeSelected.invoke(HushType.DURATION)
            }, label = { Text("Duration") }, modifier = chipModifier, leadingIcon = {
                Box(
                    Modifier.animateContentSize(keyframes {
                        durationMillis = 100
                    })
                ) {
                    if (selectedChipIndex == HushType.DURATION) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                }

            })

            FilterChip(selected = selectedChipIndex == HushType.DAYS, onClick = {
                selectedChipIndex = HushType.DAYS
                onHushTypeSelected.invoke(HushType.DAYS)
            }, label = { Text("Days") }, modifier = chipModifier, leadingIcon = {
                Box(
                    Modifier.animateContentSize(keyframes {
                        durationMillis = 100
                    })
                ) {
                    if (selectedChipIndex == HushType.DAYS) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                }

            })
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TwoLineButton(
        txt1: String, txt2: String, selectedTime: MutableState<LocalTime?> = remember {
            mutableStateOf(null)
        }
    ) {

        val sheetState = rememberSheetState()
        val title = remember {
            mutableStateOf(txt1)
        }

        OpenClock(sheetState, title, selectedTime)


        Button(onClick = {
            sheetState.show()
        }) {
            Column {
                Text(
                    text = txt1, textAlign = TextAlign.Center, modifier = Modifier.width(80.dp)
                )
                Text(
                    text = if (selectedTime.value == null) txt2 else selectedTime.value.toString(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(80.dp)
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun OpenClock(
        sheetState: com.maxkeppeker.sheets.core.models.base.SheetState,
        title: MutableState<String>,
        selectedTime: MutableState<LocalTime?>
    ) {
        ClockDialog(
            header = Header.Default(title.value),
            state = sheetState,
            selection = ClockSelection.HoursMinutes { hours, minutes ->
                Timber.d("Time Selected")
                selectedTime.value = LocalTime.of(hours, minutes)
            },
            config = ClockConfig(
                is24HourFormat = false,
            ),
        )
    }
    fun get12HrsFrom24Hrs (inputTime: String):String{
        val timeFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
        val time = timeFormat.parse(inputTime)
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return outputFormat.format(time)

    }
//
//    @Preview
//    @Composable
//    fun PreviewInitConfig() {
//        ShowInitConfig(selectedApp = SelectedAppForList(
//            SelectedApp(
//                1,
//                "Test APP",
//                "com.hush.hush",
//                HushType.DAYS,
//                0,
//                "FRI,SAT",
//                null,
//                null,
//                13212312313,
//                3123123123,
//                true
//            ), icon = null
//
//        ), chooseSelectedDays = { "" })
//    }
//
//    //    @Preview
//    @Composable
//    fun PreviewSelectedAppItem() {
//        SelectedAppItem(
//            selectedApp = SelectedAppForList(
//                SelectedApp(
//                    1,
//                    "Test APP",
//                    "com.hush.hush",
//                    HushType.DAYS,
//                    0,
//                    "FRI,SAT",
//                    null,
//                    null,
//                    13212312313,
//                    3123123123,
//                    true
//                ), icon = null
//
//            )
//        )
//    }

    data class StartEndTime(var startTime: String, var endTime: String)
}