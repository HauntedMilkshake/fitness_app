package bg.zahov.app.data.model

import com.github.mikephil.charting.data.Entry

/**
 * Data model representing the configuration and data points for a line chart.
 *
 * @property textId The id used for extracting the string used for title of the chart.
 * @property maxValue The maximum value displayed on the chart, represented as a float.
 * @property minValue The minimum value displayed on the chart, represented as a float.
 * @property suffix An optional [MeasurementType] indicating the unit or type of measurement
 *                  (e.g., weight, body fat percentage) associated with the chart data.
 * @property list A list of [Entry] objects representing the data points on the chart.
 */
data class LineChartData(
    val textId: Int? = null,
    val maxValue: Float = 0f,
    val minValue: Float = 0f,
    val suffix: MeasurementType? = null,
    val list: List<Entry> = listOf(),
)
