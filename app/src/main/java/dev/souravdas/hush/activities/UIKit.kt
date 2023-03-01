package dev.souravdas.hush

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.keyframes
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.FabPosition
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import dev.souravdas.hush.arch.MainActivityVM
import dev.souravdas.hush.arch.SelectedApp
import dev.souravdas.hush.arch.SelectedAppForList
import kotlinx.coroutines.CoroutineScope
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
    @Composable
    fun InstalledAppList(
        items: List<InstalledPackageInfo>,
        onItemClick: (InstalledPackageInfo) -> Unit = {},
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier
                .fillMaxHeight(fraction = 0.7f)
                .padding(top = 48.dp)
        ) {
            LazyColumn(
                modifier = modifier
            ) {
                items(count = items.size, itemContent = {
                    ApplicationItem(
                        app = items[it],
                        clickListener = { onItemClick(items[it]) },
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                    )
                })
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

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    @Composable
    fun MainActivityScreen(
        sheetState: BottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed),
        scope: CoroutineScope = rememberCoroutineScope(),
        openDialog: MutableState<Boolean> = remember { mutableStateOf(false) },
        selectedApp: MutableState<InstalledPackageInfo> = remember {
            mutableStateOf(
                InstalledPackageInfo()
            )
        },
        viewModel: MainActivityVM = viewModel(),
        onItemClick: (InstalledPackageInfo) -> Unit = {},
        onItemSelected: (SelectedApp) -> Unit = {}
    ) {
        val scaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = sheetState
        )

        val appList = viewModel.appListSF.collectAsState()
        viewModel.getInstalledApps()

        BottomSheetScaffold(topBar = {
            TopAppBar(title = {
                Text(text = stringResource(id = R.string.app_name))
            })
        },
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        scope.launch {
                            if (sheetState.isCollapsed) {
                                sheetState.expand()
                            } else {
                                sheetState.collapse()
                            }
                        }
                    },
                    containerColor = colorResource(id = R.color.color_coral),
                    contentColor = Color.White,
                    modifier = Modifier.padding(bottom = 96.dp)
                ) {
                    Text(text = "Select an APP")
                }
            },
            scaffoldState = scaffoldState,
            sheetContent = {
                InstalledAppList(
                    items = appList.value,
                    onItemClick = onItemClick,
                )
            },
            sheetPeekHeight = 0.dp,
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            sheetBackgroundColor = colorResource(id = R.color.whiteBG),
            sheetContentColor = colorResource(id = R.color.whiteBG),
            sheetGesturesEnabled = false
        ) {
            OpenAppSelectedDialog(openDialog = openDialog, selectedApp, onItemSelected)

//            ShowSelectedApps()
        }
    }

    private @Composable
    fun ShowSelectedApps(items: List<SelectedAppForList>) {
        Box(modifier = Modifier.padding(8.dp)) {
            LazyColumn() {
                items(count = items.size, itemContent = {
                    SelectedAppList(items[it])
                })
            }
        }
    }

    private @Composable
    fun SelectedAppList(selectedApp: SelectedAppForList) {
        Box(modifier = Modifier.padding(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = rememberDrawablePainter(
                        drawable = selectedApp.icon
                    ), contentDescription = "appIcon", modifier = Modifier.size(40.dp)
                )

                Column() {
                    Row() {
                        Text(
                            text = selectedApp.selectedApp.appName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

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
        onItemSelected: (SelectedApp) -> Unit = {}
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
                            ApplicationItem(app = selectedApp.value)
                            val selectedChipIndex = remember { mutableStateOf(0) }

                            ShowChipRow(selectedChipIndex);

                            val selectedTimeStart = remember { mutableStateOf<LocalTime?>(null) }
                            val selectedTimeEnd = remember { mutableStateOf<LocalTime?>(null) }
                            val selectedDays = remember { mutableStateOf(List(7) { false }) }
                            val selectedDaysList = selectedDays.value
                                .mapIndexed { index, isSelected -> if (isSelected) index else null }
                                .filterNotNull()

                            when(selectedChipIndex.value){
                                0 ->{}
                                1 -> Column(
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .wrapContentHeight(unbounded = true)
                                        .padding(top = 8.dp, bottom = 8.dp)
                                ) {
                                    ShowDays(selectedDays)
                                    Row(modifier = Modifier.padding(top=8.dp, bottom = 8.dp)) {
                                        TwoLineButton(
                                            txt1 = "Start Time", txt2 = "-- : --", selectedTimeStart
                                        )
                                        Spacer(modifier = Modifier.weight(0.05f))
                                        TwoLineButton(
                                            txt1 = "End Time", txt2 = "-- : --", selectedTimeEnd
                                        )
                                    }
                                }

                                2 -> DurationSelector()
                            }

                            TextButton(modifier = Modifier
                                .align(Alignment.End),
                                onClick = {
                                    onItemSelected.invoke(
                                        SelectedApp(
                                            appName = selectedApp.value.appName,
                                            packageName = selectedApp.value.packageName,
                                            startTime = selectedTimeStart.value!!,
                                            endTime = selectedTimeEnd.value!!,
                                            isAlways = 0
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
    fun DurationSelector() {
        var duration by remember { mutableStateOf(0) }
        Row(
            modifier = Modifier
                .background(colorResource(id = R.color.whiteBG), RoundedCornerShape(12.dp))
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
                },
                modifier = Modifier
                    .padding(end = 16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Icon(painterResource(id = R.drawable.twotone_remove_circle_24), contentDescription = "Decrease duration by 10 minutes")
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
                },
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Icon(painterResource(id = R.drawable.twotone_add_circle_24), contentDescription = "Increase duration by 10 minutes")
            }
        }
    }


    @Composable
    fun ShowDays(selectedDays: MutableState<List<Boolean>>) {
        Row {
            FilledTonalIconToggleButton(
                checked = selectedDays.value[0],
                onCheckedChange = { selectedDays.value = selectedDays.value.toMutableList().apply { set(0, it) } }
            ) {
                ShowDaysText(selectedDays.value[0], "SAT")
            }
            FilledTonalIconToggleButton(
                checked = selectedDays.value[1],
                onCheckedChange = { selectedDays.value = selectedDays.value.toMutableList().apply { set(1, it) } }
            ) {
                ShowDaysText(selectedDays.value[1], "SUN")
            }
            FilledTonalIconToggleButton(
                checked = selectedDays.value[2],
                onCheckedChange = { selectedDays.value = selectedDays.value.toMutableList().apply { set(2, it) } }
            ) {
                ShowDaysText(selectedDays.value[2], "MON")
            }
            FilledTonalIconToggleButton(
                checked = selectedDays.value[3],
                onCheckedChange = { selectedDays.value = selectedDays.value.toMutableList().apply { set(3, it) } }
            ) {
                ShowDaysText(selectedDays.value[3], "TUE")
            }
            FilledTonalIconToggleButton(
                checked = selectedDays.value[4],
                onCheckedChange = { selectedDays.value = selectedDays.value.toMutableList().apply { set(4, it) } }
            ) {
                ShowDaysText(selectedDays.value[4], "WED")
            }
            FilledTonalIconToggleButton(
                checked = selectedDays.value[5],
                onCheckedChange = { selectedDays.value = selectedDays.value.toMutableList().apply { set(5, it) } }
            ) {
                ShowDaysText(selectedDays.value[5], "THU")
            }
            FilledTonalIconToggleButton(
                checked = selectedDays.value[6],
                onCheckedChange = { selectedDays.value = selectedDays.value.toMutableList().apply { set(6, it) } }
            ) {
                ShowDaysText(selectedDays.value[6], "FRI")
            }
        }
    }

    @Composable
    fun ShowDaysText(selected:Boolean, title: String) {
        if (selected) {
            Text(text = title, fontWeight = FontWeight.Bold)
        } else {
            Text(text = title)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowChipRow(selectedChipIndex: MutableState<Int>) {
        Row(modifier = Modifier.fillMaxWidth()) {
            val chipModifier = Modifier.padding(start = 4.dp)

            FilterChip(selected = selectedChipIndex.value == 0,
                onClick = { selectedChipIndex.value = 0 },
                label = { Text("Mute Always") },
                modifier = chipModifier,
                leadingIcon = {
                    Box(
                        Modifier.animateContentSize(keyframes {
                            durationMillis = 100
                        })
                    ) {
                        if (selectedChipIndex.value == 0) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    }

                })

            FilterChip(selected = selectedChipIndex.value == 1,
                onClick = { selectedChipIndex.value = 1 },
                label = { Text("Duration") },
                modifier = chipModifier,
                leadingIcon = {
                    Box(
                        Modifier.animateContentSize(keyframes {
                            durationMillis = 100
                        })
                    ) {
                        if (selectedChipIndex.value == 1) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    }

                })

            FilterChip(selected = selectedChipIndex.value == 2,
                onClick = { selectedChipIndex.value = 2 },
                label = { Text("Days") },
                modifier = chipModifier,
                leadingIcon = {
                    Box(
                        Modifier.animateContentSize(keyframes {
                            durationMillis = 100
                        })
                    ) {
                        if (selectedChipIndex.value == 2) {
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
    private fun ShowTimeRangeText(
        selectedTimeStart: MutableState<LocalTime?>,
        selectedTimeEnd: MutableState<LocalTime?>,
    ) {
        if (selectedTimeStart.value != null && selectedTimeEnd.value != null) {
            Timber.d("Text Can be shown")
            Text(
                modifier = Modifier.padding(8.dp),
                fontSize = 12.sp,
                text = "App will be silent from Today ${selectedTimeStart.value} to " + "${
                    if (selectedTimeStart.value!!.isBefore(selectedTimeEnd.value)) "Today" else "Tomorrow"
                } ${selectedTimeEnd.value}",
            )
        } else {
            Text(
                modifier = Modifier.padding(8.dp),
                fontSize = 12.sp,
                text = "App will be silent from --:-- to --:--",
            )
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

    @OptIn(ExperimentalMaterialApi::class)
//    @Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp")
    @Composable
    fun MainActivityScreenPreview() {
        MainActivityScreen(

        )
    }

    @Preview
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

}