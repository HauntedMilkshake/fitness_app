package bg.zahov.app.ui.custom

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import bg.zahov.app.data.model.LineChartData
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.util.MonthValueFormatter
import bg.zahov.app.util.RightAxisValueFormatter
import bg.zahov.fitness.app.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.time.LocalDate

@Composable
fun CommonLineChart(
    modifier: Modifier = Modifier,
    data: LineChartData
) {
    val extractedTextColor = MaterialTheme.colorScheme.onSecondary.toArgb()
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        factory = { context ->
            LineChart(context).apply {
                extraRightOffset = 34f
                setPinchZoom(false)
                setDrawGridBackground(false)
                isDoubleTapToZoomEnabled = false
                legend.isEnabled = false
                axisLeft.isEnabled = false

                description.apply {
                    textColor = extractedTextColor
                    this.text = data.text
                }

                xAxis.apply {
                    axisMinimum = 1f
                    axisMaximum = LocalDate.now().lengthOfMonth().toFloat()
                    position = XAxis.XAxisPosition.BOTTOM
                    textColor = extractedTextColor
                    valueFormatter = MonthValueFormatter()
                }

                axisRight.apply {
                    textColor = extractedTextColor
                    granularity = 1f
                    valueFormatter = RightAxisValueFormatter(
                        when (data.suffix) {
                            MeasurementType.Weight -> context.getString(R.string.weight_unit)
                            MeasurementType.BodyFatPercentage -> context.getString(R.string.body_fat_percentage_unit)
                            MeasurementType.CaloricIntake -> context.getString(R.string.caloric_intake_unit)
                            else -> context.getString(R.string.default_unit)
                        }
                    )
                }

                setDrawBorders(true)
                setBorderColor(extractedTextColor)
                setBorderWidth(1f)
            }
        },
        update = { chart ->
            chart.data =
                LineData(LineDataSet(data.list, chart.context.getString(R.string.results)).apply {
                    valueTextColor = extractedTextColor
                    valueTextSize = 13f
                })

            chart.axisRight.apply {
                axisMaximum = data.maxValue
                axisMinimum = data.minValue
            }
            chart.invalidate()
        }
    )
}
