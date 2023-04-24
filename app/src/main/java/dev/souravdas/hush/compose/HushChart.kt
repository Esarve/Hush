package dev.souravdas.hush.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
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
fun HushChart(dataMap: () -> Map<LocalDate, Float> , onRefreshClick : () -> Unit) {

    var index = 0f
    val chartEntryModelProducer = dataMap.invoke().map {
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
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {


                Text(
                    text = "History",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(8.dp)
                )

                IconButton(onClick = {}) {
                    val rotationState = remember { mutableStateOf(0f) }

                    val rotationAngle = animateFloatAsState(
                        targetValue = rotationState.value,
                        animationSpec = tween(durationMillis = 700)
                    )

                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "Rotated Icon",
                        modifier = Modifier
                            .graphicsLayer {
                                rotationZ = rotationAngle.value
                            }
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                onRefreshClick.invoke()
                                rotationState.value += 360f // Rotate by 45 degrees on each click
                            }
                    )
                }
            }

            if (dataMap.invoke().isNotEmpty()) {
                val marker = rememberMarker()
                val axisValueFormatter =
                    AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, chartValues ->
                        (chartValues.chartEntryModel.entries.first()
                            .getOrNull(value.toInt()) as? Entry)
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
                            .padding(bottom = 8.dp)
                    )
                }

                /*Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start
                ) {
                    TextButton(
                        onClick = { *//*TODO*//* },
                        modifier = Modifier
                            .padding(top = 8.dp, start = 8.dp)
                    ) {
                        Text(text = "See More", style = MaterialTheme.typography.labelLarge)

                    }
                }
*/
            } else {
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