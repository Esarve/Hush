package dev.souravdas.hush.activities

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import dev.souravdas.hush.InstalledPackageInfo
import dev.souravdas.hush.R
import dev.souravdas.hush.arch.HushType
import dev.souravdas.hush.arch.MainActivityVM
import dev.souravdas.hush.arch.SelectedApp
import dev.souravdas.hush.arch.SelectedAppForList
import kotlinx.coroutines.launch
import org.threeten.bp.LocalTime
import timber.log.Timber
import java.util.*

/**
 * Created by Sourav
 * On 2/22/2023 11:45 PM
 * For Hush!
 */
class UIKit {
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun InstalledAppList(
        items: List<InstalledPackageInfo>,
        onItemClick: (InstalledPackageInfo) -> Unit = {},
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
                                onItemClick.invoke(item)
                                scope.launch {
                                    //hide shit
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

    @Composable
    fun ApplicationItem(
        app: InstalledPackageInfo, clickListener: () -> Unit = {}, modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier.clickable(onClick = clickListener)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = modifier.fillMaxWidth()
            ) {
                Image(
                    painter = rememberDrawablePainter(
                        drawable = app.icon ?: ContextCompat.getDrawable(
                            LocalContext.current, R.mipmap.ic_launcher_round
                        )
                    ), contentDescription = "appIcon", modifier = Modifier.size(40.dp)
                )

                Text(
                    text = app.appName,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 4.dp)
                )
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
        val selectedApp: MutableState<InstalledPackageInfo> = remember {
            mutableStateOf(
                InstalledPackageInfo()
            )
        }
        val openDialog: MutableState<Boolean> = remember { mutableStateOf(false) }
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
                        selectedApp.value = item
                        openDialog.value = true
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
            val selectedAppForList = viewModel.selectedAppsSF.collectAsState()
            val modifier = Modifier.consumeWindowInsets(it)
            val hushStatus = viewModel.getHushStatusAsFlow().collectAsState(initial = false)

            OpenAppSelectedDialog(openDialog = openDialog, selectedApp, onItemSelected = {
                onItemSelected.invoke(it)
                openDialog.value = false
            }) {
                viewModel.getDaysFromSelected(it)
            }

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

            ShowSelectedApps(modifier) {
                selectedAppForList.value
            }
        }
    }

    @Composable
    fun ShowSelectedApps(
        modifier: Modifier = Modifier,
        itemProvider: () -> (List<SelectedAppForList>) = { emptyList() }
    ) {
        Box(modifier = modifier.padding(8.dp)) {
            LazyColumn() {
                items(count = itemProvider.invoke().size, itemContent = {
                    SelectedAppItem(itemProvider.invoke()[it])
                })
            }
        }
    }

    @Composable
    fun SelectedAppItem(selectedApp: SelectedAppForList) {
        var showExtended by remember {
            mutableStateOf(false)
        }
        Box(
            modifier = Modifier
                .clickable {
                    showExtended = !showExtended
                }
                .padding(bottom = 8.dp)
                .background(colorResource(id = R.color.whiteBG), RoundedCornerShape(12.dp))


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
                        ), contentDescription = "appIcon", modifier = Modifier.size(50.dp)
                    )

                    Column(modifier = Modifier.padding(start = 8.dp, end = 4.dp)) {
                        Text(
                            text = selectedApp.selectedApp.appName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp, bottom = 4.dp)
                                .background(
                                    colorResource(id = R.color.color_lavender),
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Text(
                                text = selectedApp.selectedApp.hushType.toString(),
                                modifier = Modifier.padding(
                                    top = 2.dp, bottom = 2.dp, start = 6.dp, end = 6.dp
                                ),
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
                val buttonModifier = Modifier.padding(end = 4.dp)

                AnimatedVisibility(showExtended) {
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
                            onClick = { /* Do something! */ }) {
                            Text("Notification History")
                        }

                        TextButton(
                            modifier = buttonModifier,
                            onClick = { /* Do something! */ }) {
                            Text("Edit")
                        }

                        TextButton(
                            modifier = buttonModifier,
                            onClick = { /* Do something! */ }) {
                            Text("Remove")
                        }
                    }
                }
            }

        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun OpenAppSelectedDialog(
        openDialog: MutableState<Boolean>,
        selectedApp: MutableState<InstalledPackageInfo>,
        onItemSelected: (SelectedApp) -> Unit = {},
        chooseSelectedDays: (List<Int>) -> String = { "" }
    ) {

        if (openDialog.value) {
            AlertDialog(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentWidth(),
                onDismissRequest = {
                    openDialog.value = false
                },
            ) {
                Surface(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Box(
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight()
                            .padding(16.dp)
                    ) {

                        Column(
                            modifier = Modifier
                                .wrapContentWidth()
                                .wrapContentHeight()
                        ) {

                            val selectedTimeStart = remember { mutableStateOf<LocalTime?>(null) }
                            val selectedTimeEnd = remember { mutableStateOf<LocalTime?>(null) }
                            var selectedDayList: List<Int> = emptyList()
                            var selectedDuration: Long = 0
                            var husType: HushType by remember {
                                mutableStateOf(HushType.ALWAYS)
                            }

                            ApplicationItem(app = selectedApp.value)

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
                                        selectedDayList =
                                            it.mapIndexed { index, isSelected -> if (isSelected) index else null }
                                                .filterNotNull()
                                    }
                                    Row(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)) {
                                        TwoLineButton(
                                            txt1 = "Start Time", txt2 = "-- : --", selectedTimeStart
                                        )
                                        Spacer(modifier = Modifier.weight(0.05f))
                                        TwoLineButton(
                                            txt1 = "End Time", txt2 = "-- : --", selectedTimeEnd
                                        )
                                    }
                                }

                                HushType.DURATION -> DurationSelector {
                                    selectedDuration = it
                                }
                            }

                            TextButton(modifier = Modifier.align(Alignment.End), onClick = {
                                onItemSelected.invoke(
                                    SelectedApp(
                                        appName = selectedApp.value.appName,
                                        packageName = selectedApp.value.packageName,
                                        hushType = husType,
                                        muteDays = chooseSelectedDays.invoke(selectedDayList),
                                        durationInMinutes = selectedDuration,
                                        startTime = selectedTimeStart.value,
                                        endTime = selectedTimeEnd.value
                                    )
                                )
                            }) {
                                Text(text = "Add")
                            }

                        }
                    }
                }
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
    fun ShowDays(onSelectedDays: (List<Boolean>) -> Unit = {}) {

        var selectedDays by remember { mutableStateOf(List(7) { false }) }

        Row {
            FilledTonalIconToggleButton(checked = selectedDays[0], onCheckedChange = {
                selectedDays = selectedDays.toMutableList().apply { set(0, it) }
                onSelectedDays.invoke(selectedDays)
            }) {
                ShowDaysText(selectedDays[0], "SAT")
            }
            FilledTonalIconToggleButton(checked = selectedDays[1], onCheckedChange = {
                selectedDays = selectedDays.toMutableList().apply { set(1, it) }
                onSelectedDays.invoke(selectedDays)
            }) {
                ShowDaysText(selectedDays[1], "SUN")
            }
            FilledTonalIconToggleButton(checked = selectedDays[2], onCheckedChange = {
                selectedDays = selectedDays.toMutableList().apply { set(2, it) }
                onSelectedDays.invoke(selectedDays)
            }) {
                ShowDaysText(selectedDays[2], "MON")
            }
            FilledTonalIconToggleButton(checked = selectedDays[3], onCheckedChange = {
                selectedDays = selectedDays.toMutableList().apply { set(3, it) }
                onSelectedDays.invoke(selectedDays)
            }) {
                ShowDaysText(selectedDays[3], "TUE")
            }
            FilledTonalIconToggleButton(checked = selectedDays[4], onCheckedChange = {
                selectedDays = selectedDays.toMutableList().apply { set(4, it) }
                onSelectedDays.invoke(selectedDays)
            }) {
                ShowDaysText(selectedDays[4], "WED")
            }
            FilledTonalIconToggleButton(checked = selectedDays[5], onCheckedChange = {
                selectedDays = selectedDays.toMutableList().apply { set(5, it) }
                onSelectedDays.invoke(selectedDays)
            }) {
                ShowDaysText(selectedDays[5], "THU")
            }
            FilledTonalIconToggleButton(checked = selectedDays[6], onCheckedChange = {
                selectedDays = selectedDays.toMutableList().apply { set(6, it) }
                onSelectedDays.invoke(selectedDays)
            }) {
                ShowDaysText(selectedDays[6], "FRI")
            }
        }
    }

    @Composable
    fun ShowDaysText(selected: Boolean, title: String) {
        if (selected) {
            Text(text = title, fontWeight = FontWeight.Bold)
        } else {
            Text(text = title)
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

    @Composable
    fun OpenAppSelectedDialog() {
        val openState = remember {
            mutableStateOf(true)
        }

        val selected = remember {
            mutableStateOf(
                InstalledPackageInfo("Test 1", "com.test1")
            )
        }

        OpenAppSelectedDialog(openDialog = openState, selectedApp = selected)
    }

    @Preview
    @Composable
    fun PreviewSelectedAppItem() {
        SelectedAppItem(
            selectedApp = SelectedAppForList(
                SelectedApp(
                    1,
                    "Test APP",
                    "com.hush.hush",
                    HushType.DAYS,
                    0,
                    "FRI,SAT",
                    null,
                    null,
                    13212312313
                ), null

            )
        )
    }

}