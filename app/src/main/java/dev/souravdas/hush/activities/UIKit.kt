package dev.souravdas.hush

import android.widget.Toast
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.FabPosition
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
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
                items(
                    count = items.size,
                    itemContent = {
                        ApplicationItem(
                            app = items[it],
                            clickListener = { onItemClick(items[it]) },
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                        )
                    }
                )
            }
        }
    }

    @Composable
    fun ApplicationItem(
        app: InstalledPackageInfo,
        clickListener: () -> Unit = {},
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier.clickable(onClick = clickListener)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.fillMaxWidth()
            ) {
                Image(
                    painter = rememberDrawablePainter(
                        drawable = app.icon ?: ContextCompat.getDrawable(
                            LocalContext.current,
                            R.mipmap.ic_launcher_round
                        )
                    ),
                    contentDescription = "appIcon",
                    modifier = Modifier
                        .size(40.dp)
                )

                Text(
                    text = app.appName,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(start = 4.dp)
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

        BottomSheetScaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.app_name))
                    }
                )
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
        Box(modifier = Modifier.padding(8.dp)){
            LazyColumn() {
                items(
                    count = items.size,
                    itemContent = {
                        SelectedAppList(items[it])
                    }
                )
            }
        }
    }

    private @Composable
    fun SelectedAppList(selectedApp: SelectedAppForList) {
        Box(modifier = Modifier.padding(8.dp)){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = rememberDrawablePainter(
                        drawable = selectedApp.icon
                    ),
                    contentDescription = "appIcon",
                    modifier = Modifier
                        .size(40.dp)
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
        val contextForToast = LocalContext.current.applicationContext

        val checked = remember {
            mutableStateOf(false)
        }
        if (openDialog.value) {
            AlertDialog(
                modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth(),
                onDismissRequest = {
                    openDialog.value = false
                }
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

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {

                                Text(
                                    text = "Always mute this app",
                                    modifier = Modifier.clickable(true) {
                                        !checked.value //does not work lol
                                    })
                                Checkbox(
                                    checked = checked.value,
                                    onCheckedChange = { checked_ ->
                                        checked.value = checked_
                                    },
                                )
                            }

                            val selectedTimeStart = remember { mutableStateOf<LocalTime?>(null) }
                            val selectedTimeEnd = remember { mutableStateOf<LocalTime?>(null) }

                            if (!checked.value) {
                                Column(
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .wrapContentHeight(unbounded = true)
                                ) {

                                    Row(modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
                                        TwoLineButton(
                                            txt1 = "Start Time",
                                            txt2 = "-- : --",
                                            selectedTimeStart
                                        )
                                        Spacer(modifier = Modifier.weight(0.05f))
                                        TwoLineButton(
                                            txt1 = "End Time",
                                            txt2 = "-- : --",
                                            selectedTimeEnd
                                        )
                                    }

                                    ShowTimeRangeText(
                                        selectedTimeStart,
                                        selectedTimeEnd,
                                    ) //Doesn't work
                                }
                            }

                            Button(
                                enabled = (selectedTimeStart.value != null && selectedTimeEnd.value != null),
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

                                }, modifier = Modifier
                                    .padding(top = 16.dp, end = 8.dp)
                                    .align(Alignment.End)
                            ) {
                                Text(
                                    text = "Add",
                                    fontSize = 16.sp,
                                )
                            }
                        }
                    }
                }
            }
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
                text = "App will be silent from Today ${selectedTimeStart.value} to " +
                        "${if (selectedTimeStart.value!!.isBefore(selectedTimeEnd.value)) "Today" else "Tomorrow"} ${selectedTimeEnd.value}",
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
                    text = txt1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(80.dp)
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
    @Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp")
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