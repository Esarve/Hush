package dev.souravdas.hush.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import dev.souravdas.hush.arch.MainActivityVM
import dev.souravdas.hush.models.AppLog
import dev.souravdas.hush.others.AppIconsMap
import dev.souravdas.hush.others.Utils

/**
 * Created by Sourav
 * On 3/21/2023 6:45 PM
 * For Hush!
 */

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

@Composable
fun AppLogList() {
    val viewModel: MainActivityVM = viewModel()
    val navigator = LocalNavigator.currentOrThrow

    val logsState = viewModel.appLog.collectAsState(emptyList())
    val logs by rememberSaveable { logsState }

    SideEffect {
        viewModel.getLog()
    }


    LazyColumn(

    ) {
        items(logs) { log ->
            ItemView(log)
        }
    }


}

@Composable
fun ItemView(log: AppLog) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberDrawablePainter(drawable = AppIconsMap.appIconMap[log.packageName]),
                    contentDescription = "APP", modifier = Modifier.size(24.dp, 24.dp)
                )
                Text(text = log.appName, modifier = Modifier.padding(start = 8.dp))
                Spacer(modifier = Modifier.weight(0.6f))
                Text(
                    text = Utils().getTimeAgo(log.timeCreated),
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            Text(
                text = log.title ?: "",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 6.dp)
            )
            Text(
                text = log.body ?: "",
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}