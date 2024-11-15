package bg.zahov.app.ui.custom

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import bg.zahov.app.data.model.Theme
import bg.zahov.app.ui.home.ChartData
import bg.zahov.fitness.app.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun CommonBarChart(
    modifier: Modifier = Modifier,
    chartData: ChartData,
    chartFormatter: ValueFormatter
) {
    val color = MaterialTheme.colorScheme.secondary.toArgb()
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        factory = { context ->
            BarChart(context).apply {
                setupBarChart(context, color)

                xAxis.valueFormatter = chartFormatter
                xAxis.axisMaximum = chartData.xMax
                xAxis.axisMinimum = chartData.xMin
                axisRight.axisMaximum = chartData.yMax
                axisRight.axisMinimum = chartData.yMin

                data = BarData(BarDataSet(chartData.chartData, "").apply {
                    setDrawValues(false)
                }).apply {
                    barWidth = 0.5f
                }
            }
        },
        update = { chart ->
            chart.xAxis.apply {
                valueFormatter = chartFormatter
                axisMaximum = chartData.xMax
                axisMinimum = chartData.xMin
            }

            chart.axisRight.apply {
                axisMaximum = chartData.yMax
                axisMinimum = chartData.yMin
            }

            val dataSet = BarDataSet(chartData.chartData, "").apply {
                setDrawValues(false)
            }
            chart.data = BarData(dataSet).apply {
                barWidth = 0.5f
            }

            chart.invalidate()
        }
    )
}

fun BarChart.setupBarChart(context: Context, color: Int) {
    setFitBars(true)
    legend.isEnabled = false
    isDoubleTapToZoomEnabled = false
    axisLeft.isEnabled = false
    isDragEnabled = false
    isHighlightFullBarEnabled = false

    description.apply {
        setPosition(250f, 60f)
        text = context.getString(R.string.weekly_workouts)
        textColor = color
    }

    xAxis.apply {
        position = XAxis.XAxisPosition.BOTTOM
        granularity = 1f
        axisMinimum = 0f
        textColor = color
        setCenterAxisLabels(true)
        isGranularityEnabled = true
    }

    axisRight.apply {
        textColor = color
        granularity = 1f
        setDrawGridLines(false)
    }
}