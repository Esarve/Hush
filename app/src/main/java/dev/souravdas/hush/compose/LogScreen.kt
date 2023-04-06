package dev.souravdas.hush.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.souravdas.hush.arch.MainActivityVM
import dev.souravdas.hush.others.Utils

/**
 * Created by Sourav
 * On 3/21/2023 6:45 PM
 * For Hush!
 */

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AppLogList(
    app_id: Long?,
    appName: String?,
) {
    val viewModel: MainActivityVM = viewModel()

    viewModel.getLog(app_id!!.toInt())
    val navigator = LocalNavigator.currentOrThrow
    val logs by viewModel.appLog.collectAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                title = { Text(text = appName!!) },
                navigationIcon = {
                    Icon(
                        Icons.Outlined.ArrowBack,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = "BACK",
                        modifier = Modifier.clickable {
                            navigator.popUntilRoot()
                        }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
            ) {
                Icon(Icons.Outlined.Delete, contentDescription = "NUKE")
            }
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
        ) {

            Box(Modifier.padding(16.dp)) {
                Text(
                    text = "Muted Notifications",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            LazyColumn(
                modifier = Modifier
            ) {
                items(logs) { log ->
                    Card(
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(text = "Title: ${log.title ?: ""}")
                            Text(text = "Body: ${log.body ?: ""}")
                            System.currentTimeMillis()
                            Text(text = "Time: " + Utils().getStringDateFromMillis(log.timeCreated))
                        }
                    }
                }
            }

        }
    }


}