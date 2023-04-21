package dev.souravdas.hush.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import dev.souravdas.hush.arch.MainActivityVM
import dev.souravdas.hush.models.AppLog
import dev.souravdas.hush.others.AppIconsMap
import dev.souravdas.hush.others.Utils
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

/**
 * Created by Sourav
 * On 3/21/2023 6:45 PM
 * For Hush!
 */

@RootNavGraph
@Destination
@Composable
fun AppLogList() {
    val viewModel: MainActivityVM = hiltViewModel()
    val logsState = viewModel.appLog.collectAsState()
    val logs by remember { logsState }

    SideEffect {
        viewModel.getLog()
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        items(logs) { logWithHeader ->
            logWithHeader.header?.let {
                HeaderItem(value = it.toStringForLogList())
            }
            ItemView(logWithHeader.log)
        }
    }
}

private fun LocalDate.toStringForLogList(): String {
    return when {
        this.dayOfYear == LocalDate.now().dayOfYear -> {
            "TODAY"
        }

        LocalDate.now().dayOfYear - this.dayOfYear <= 6 -> {
            this.dayOfWeek.toString()
        }

        else -> {
            this.format(DateTimeFormatter.ofPattern("dd MMMM"))
        }
    }
}

@Composable
fun HeaderItem(value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = value,
            color = MaterialTheme.colorScheme.tertiary,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(4.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
    }

}

@Composable
fun ItemView(log: AppLog) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val appIconExists by remember {
                    mutableStateOf(AppIconsMap.appIconMap.containsKey(log.packageName))
                }
                Image(
                    painter = rememberDrawablePainter(
                        drawable =
                        if (appIconExists)
                            AppIconsMap.appIconMap[log.packageName]
                        else LocalContext.current.getDrawable(
                            dev.souravdas.hush.R.drawable.esarve
                        )
                    ),
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