package dev.souravdas.hush.compose.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import dev.souravdas.hush.R
import dev.souravdas.hush.models.InstalledPackageInfo
import dev.souravdas.hush.models.SelectedApp
import kotlinx.coroutines.launch

/**
 * Created by Sourav
 * On 3/18/2023 12:24 PM
 * For Hush!
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun InstalledAppList(
    items: List<InstalledPackageInfo>,
    onItemClick: (SelectedApp) -> Unit = {},
) {
    var searchText by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    val filteredItems = if (searchText.isBlank()) {
        items
    } else {
        items.filter { it.packageName.contains(searchText, ignoreCase = true) }
    }

    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxHeight(fraction = 0.7f)
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column() {
            TextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                },
                placeholder = { Text(text = "Search")},
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                shape = RoundedCornerShape(32.dp),
                colors = TextFieldDefaults.textFieldColors(
                    disabledTextColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(16.dp)
            )
            val lazyListState = rememberLazyListState()
            LazyColumn(
                state = lazyListState,
            ) {
                itemsIndexed(items = filteredItems) { index, item ->

                    ListItem(
                        modifier = Modifier.clickable {
                            scope.launch {
                                onItemClick.invoke(
                                    SelectedApp(
                                        appName = item.appName,
                                        packageName = item.packageName,
                                        timeUpdated = System.currentTimeMillis(),
                                        logNotification = false,
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
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    )
                }
            }
        }

    }
}