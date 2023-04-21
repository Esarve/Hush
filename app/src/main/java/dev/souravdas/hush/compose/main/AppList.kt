package dev.souravdas.hush.compose.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import dev.souravdas.hush.BuildConfig
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {

        if (BuildConfig.FLAVOR == "internal"){
            Row() {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(painterResource(id = R.drawable.ic_import), contentDescription = "import")
                }

                IconButton(onClick = { /*TODO*/ }) {
                    Icon(painterResource(id = R.drawable.ic_save), contentDescription = "export")
                }
            }
        }

        Column() {
            //todo: Contains a bug where soft keyboard does not appear. Link: https://issuetracker.google.com/issues/268380384?pli=1

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