package dev.souravdas.hush

import android.os.Build
import android.widget.CalendarView
import android.widget.Toast
import androidx.compose.material3.Checkbox
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalTime

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
                items(items) { item ->
                    ApplicationItem(
                        app = item,
                        clickListener = { onItemClick(item) },
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                    )
                }
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
        items: List<InstalledPackageInfo>,
        sheetState: BottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed),
        scope: CoroutineScope = rememberCoroutineScope(),
        openDialog: MutableState<Boolean> = remember { mutableStateOf(false) },
        selectedApp: MutableState<InstalledPackageInfo> = remember {
            mutableStateOf(
                InstalledPackageInfo()
            )
        },
        onItemClick: (InstalledPackageInfo) -> Unit = {}
    ) {
        val scaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = sheetState
        )

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
                    items = items,
                    onItemClick = onItemClick,
                )
            },
            sheetPeekHeight = 0.dp,
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            sheetBackgroundColor = colorResource(id = R.color.whiteBG),
            sheetContentColor = colorResource(id = R.color.whiteBG),
            sheetGesturesEnabled = false
        ) {
            OpenAppSelectedDialog(openDialog = openDialog, selectedApp)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun OpenAppSelectedDialog(
        openDialog: MutableState<Boolean>,
        selectedApp: MutableState<InstalledPackageInfo>
    ) {
        val contextForToast = LocalContext.current.applicationContext

        var checked by remember {
            mutableStateOf(false)
        }
        if (openDialog.value) {
            AlertDialog(
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
                    Box(modifier = Modifier.padding(16.dp)) {

                        Column() {
                            ApplicationItem(app = selectedApp.value)

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {

                                Text(
                                    text = "Always mute this app",
                                    modifier = Modifier.clickable(true) {
                                        !checked //does not work lol
                                    })
                                Checkbox(
                                    checked = checked,
                                    onCheckedChange = { checked_ ->
                                        checked = checked_
                                        Toast.makeText(
                                            contextForToast,
                                            "checked_ = $checked_",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                )
                            }

                            if (!checked) {
                                Row(modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
                                    TwoLineButton(txt1 = "Start Time", txt2 = "-- : --")
                                    Spacer(modifier = Modifier.weight(0.05f))
                                    TwoLineButton(txt1 = "End Time", txt2 = "-- : --")
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    @Composable
    fun TwoLineButton(txt1: String, txt2: String) {

        val sheetState = rememberSheetState()
        val title = remember {
            mutableStateOf(txt1)
        }

        OpenClock(sheetState, title)

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
                    text = txt2,
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
        title: MutableState<String>
    ) {
        val selectedTime = remember { mutableStateOf<LocalTime?>(null) }
        ClockDialog(
            header = Header.Default(title.value),
            state = sheetState,
            selection = ClockSelection.HoursMinutesSeconds { hours, minutes, seconds ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    selectedTime.value = LocalTime.of(hours, minutes, seconds)
                }
            },
            config = ClockConfig(
                is24HourFormat = false
            ),
        )
    }

    @OptIn(ExperimentalMaterialApi::class)
//    @Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp")
    @Composable
    fun MainActivityScreenPreview() {
        MainActivityScreen(
            items = mutableListOf(
                InstalledPackageInfo("Test 1", "com.test1"),
                InstalledPackageInfo("Test2", "com.test2")
            )
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