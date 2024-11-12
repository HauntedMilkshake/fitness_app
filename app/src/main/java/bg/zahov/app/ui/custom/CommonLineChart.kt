package bg.zahov.app.ui.custom

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
    val text = stringResource(R.string.measure)
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
                    textColor = Color.WHITE
                    this.text = text
                }

                xAxis.apply {
                    axisMinimum = 1f
                    axisMaximum = LocalDate.now().lengthOfMonth().toFloat()
                    position = XAxis.XAxisPosition.BOTTOM
                    textColor = Color.WHITE
                    valueFormatter = MonthValueFormatter()
                }

                axisRight.apply {
                    textColor = Color.WHITE
                    granularity = 1f
                    valueFormatter = RightAxisValueFormatter(
                        when (data.suffix) {
                            MeasurementType.Weight -> "kg"
                            MeasurementType.BodyFatPercentage -> "%"
                            MeasurementType.CaloricIntake -> "kcal"
                            else -> "sm"
                        }
                    )
                }

                setDrawBorders(true)
                setBorderColor(Color.WHITE)
                setBorderWidth(1f)
            }
        },
        update = { chart ->
            chart.data = LineData(LineDataSet(data.list, "results").apply {
                valueTextColor = Color.WHITE
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
