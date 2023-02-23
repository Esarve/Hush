package dev.souravdas.hush

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter

/**
 * Created by Sourav
 * On 2/22/2023 11:45 PM
 * For Hush!
 */
class UIKit {
    @Composable
    fun InstalledAppList(items: List<InstalledPackageInfo>, modifier: Modifier = Modifier) {
        modifier
            .fillMaxSize()
            .padding(top = 16.dp)

        LazyColumn(
            modifier = modifier
        ) {
            items(items){item ->
                ApplicationItem(app = item, modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp))
            }
        }
    }

    @Composable
    fun ApplicationItem(app: InstalledPackageInfo, modifier: Modifier = Modifier) {
        Box(
            modifier = modifier
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.fillMaxWidth()
            ) {
                Image(
                    painter = rememberDrawablePainter(drawable = app.icon),
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainActivityScreen(items: List<InstalledPackageInfo>) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "My App Title")
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { /* Handle FAB click */ }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) {
            InstalledAppList(items = items, modifier = Modifier.padding(it))
        }
    }



}