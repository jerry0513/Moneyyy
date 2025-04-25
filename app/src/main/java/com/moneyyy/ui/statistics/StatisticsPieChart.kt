package com.moneyyy.ui.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moneyyy.R
import com.moneyyy.data.model.ExpenseCategory
import com.moneyyy.ui.theme.MoneyyyTheme

private val ChartYellow = Color(0xFFFFC107)
private val ChartOrange = Color(0xFFFF9800)
private val ChartTeal = Color(0xFF4DB6AC)
private val ChartBlue = Color(0xFF64B5F6)
private val ChartPurple = Color(0xFFBA68C8)
private val DefaultChartColors = listOf(ChartYellow, ChartOrange, ChartTeal, ChartBlue, ChartPurple)

@Composable
fun StatisticsPieChart(
    data: List<StatisticsCategoryItem>,
    modifier: Modifier = Modifier,
    chartSize: Dp = 100.dp,
    colors: List<Color> = DefaultChartColors
) {
    require(data.size <= colors.size) {
        "StatisticsPieChart only supports up to ${colors.size} items. Consider increasing color pool or trimming data."
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            val colorScheme = MaterialTheme.colorScheme
            val emptyDataColor = remember(colorScheme) { colorScheme.onSurface.copy(alpha = 0.12f) }

            Canvas(modifier = Modifier.size(chartSize)) {
                var startAngle = -90f

                if (data.isNotEmpty()) {
                    data.forEachIndexed { index, value ->
                        val sweepAngle = (value.percentageValue / 100) * 360
                        drawArc(
                            color = colors[index],
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true
                        )
                        startAngle += sweepAngle
                    }
                } else {
                    drawCircle(emptyDataColor)
                }
            }
        }
        Spacer(modifier = Modifier.width(24.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (data.isNotEmpty()) {
                data.forEachIndexed { index, value ->
                    StatisticsPieChartLegend(
                        text = stringResource(value.category?.nameResId ?: R.string.other),
                        percentageValue = value.percentageValue,
                        legendColor = colors[index]
                    )
                }
            } else {
                repeat(5) { index ->
                    StatisticsPieChartLegend(
                        text = "-",
                        percentageValue = 0f,
                        legendColor = colors[index]
                    )
                }
            }
        }
    }
}

@Composable
private fun StatisticsPieChartLegend(
    text: String,
    percentageValue: Float,
    legendColor: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(modifier = Modifier.size(8.dp)) {
            drawCircle(
                color = legendColor,
                style = Stroke(width = 4f)
            )
        }
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = stringResource(R.string.format_percentage, percentageValue),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Preview
@Composable
private fun StatisticsPieChartPreview() {
    MoneyyyTheme {
        Surface {
            StatisticsPieChart(
                data = listOf(
                    StatisticsCategoryItem(
                        category = ExpenseCategory.TRANSPORTATION,
                        amount = 25,
                        percentageValue = 25f
                    ),
                    StatisticsCategoryItem(
                        category = ExpenseCategory.ENTERTAINMENT,
                        amount = 35,
                        percentageValue = 35f
                    ),
                    StatisticsCategoryItem(
                        category = ExpenseCategory.CAR,
                        amount = 40,
                        percentageValue = 40f
                    )
                )
            )
        }
    }
}

@Preview
@Composable
fun StatisticsPieChartFullPreview() {
    MoneyyyTheme {
        Surface {
            StatisticsPieChart(
                data = listOf(
                    StatisticsCategoryItem(
                        category = ExpenseCategory.TRANSPORTATION,
                        amount = 30,
                        percentageValue = 30f
                    ),
                    StatisticsCategoryItem(
                        category = ExpenseCategory.ENTERTAINMENT,
                        amount = 25,
                        percentageValue = 25f
                    ),
                    StatisticsCategoryItem(
                        category = ExpenseCategory.CAR,
                        amount = 20,
                        percentageValue = 20f
                    ),
                    StatisticsCategoryItem(
                        category = ExpenseCategory.TELEPHONE,
                        amount = 15,
                        percentageValue = 15f
                    ),
                    StatisticsCategoryItem(
                        category = null,
                        amount = 10,
                        percentageValue = 10f
                    )
                )
            )
        }
    }
}

@Preview
@Composable
private fun StatisticsPieChartEmptyPreview() {
    MoneyyyTheme {
        Surface {
            StatisticsPieChart(
                data = listOf()
            )
        }
    }
}