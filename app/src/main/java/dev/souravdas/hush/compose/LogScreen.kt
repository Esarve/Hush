package dev.souravdas.hush.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.souravdas.hush.arch.MainActivityVM
import dev.souravdas.hush.models.AppLog

/**
 * Created by Sourav
 * On 3/21/2023 6:45 PM
 * For Hush!
 */

@Composable
fun AppLogList(
    app_id: Long?,
    viewModel: MainActivityVM = hiltViewModel()
) {
    viewModel.getLog(app_id!!.toInt())

    val logs by viewModel.appLog.collectAsState(emptyList())

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(logs) { log ->
                Card(
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(text = "Title: ${log.title ?: ""}")
                        Text(text = "Body: ${log.body ?: ""}")
                        Text(text = "Time Created: ${log.timeCreated}")
                    }
                }
            }
        }
    }

}