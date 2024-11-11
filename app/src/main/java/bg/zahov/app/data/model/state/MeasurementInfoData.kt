package bg.zahov.app.data.model.state

import bg.zahov.app.data.model.LineChartData

/**
 * Data model representing the information related to a specific measurement,
 * including chart data, loading state, and user input.
 *
 * @property dataType The type of data (e.g., weight, body fat percentage, etc.) represented as a string.
 * @property data The [LineChartData] containing the chart data to be displayed.
 * @property loading A boolean indicating whether the data is currently being loaded.
 * @property showDialog A boolean indicating whether a dialog should be shown (e.g., for input or error).
 * @property historyInput A string representing the user's input related to historical data (e.g., a search term or filter).
 */
data class MeasurementInfoData(
    val dataType: String = "",
    val data: LineChartData = LineChartData(),
    val loading: Boolean = true,
    val showDialog: Boolean = false,
    val historyInput: String = "",
)
