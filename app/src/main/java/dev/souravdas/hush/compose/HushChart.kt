package dev.souravdas.hush.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.compose.style.currentChartStyle
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import dev.souravdas.hush.compose.main.Entry
import dev.souravdas.hush.others.rememberChartStyle
import org.threeten.bp.LocalDate
import kotlin.math.ceil

/**
 * Created by Sourav
 * On 4/12/2023 10:02 AM
 * For Hush!
 */

@Composable
fun HushChart(dataMap: Map<LocalDate, Float>){
    
    var index = 0f
    val chartEntryModelProducer = dataMap.map {
        Entry(
            it.key,
            index++,
            it.value
        )
    }.let {
        ChartEntryModelProducer(it)
    }

    Card(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(text = "History", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(8.dp))
            if (dataMap.isNotEmpty()){
                val marker = rememberMarker()
                val axisValueFormatter =
                    AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, chartValues ->
                        (chartValues.chartEntryModel.entries.first().getOrNull(value.toInt()) as? Entry)
                            ?.localDate
                            ?.run {
                                dayOfWeek.toString().substring(0, 3)
                            }
                            .orEmpty()
                    }

                val startAxisValueFormatter =
                    AxisValueFormatter<AxisPosition.Vertical.Start> { value, chartValues ->
                        ceil(value).toInt().toString()
                    }
                
                ProvideChartStyle(rememberChartStyle(listOf(MaterialTheme.colorScheme.primary))) {
                    val defaultColumns = currentChartStyle.columnChart.columns
                    Chart(
                        chart = columnChart(
                            columns = remember(defaultColumns) {
                                defaultColumns.map { defaultColumn ->
                                    LineComponent(
                                        defaultColumn.color,
                                        18f,
                                        defaultColumn.shape
                                    )
                                }
                            },
                            spacing = 32.dp
                        ),
                        chartModelProducer = chartEntryModelProducer,
                        startAxis = startAxis(
                            axis = null,
                            tick = null,
                            maxLabelCount = 3,
                            valueFormatter = startAxisValueFormatter

                        ),
                        bottomAxis = bottomAxis(
                            axis = null,
                            tick = null,
                            valueFormatter = axisValueFormatter,
                            guideline = null
                        ),
                        marker = marker,
                        runInitialAnimation = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(128.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start
                ) {
                    TextButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .padding(top = 8.dp, start = 8.dp)
                    ) {
                        Text(text = "See More", style = MaterialTheme.typography.labelLarge)

                    }
                }

            }else{
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp)
                ) {
                    Text(text = "Not enough data...")
                }
            }
        }

    }
}