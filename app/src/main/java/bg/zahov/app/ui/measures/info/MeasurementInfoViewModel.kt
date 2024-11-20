package bg.zahov.app.ui.measures.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.interfaces.MeasurementProvider
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.model.LineChartData
import bg.zahov.app.data.model.Measurement
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.data.model.state.MeasurementInfoData
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * ViewModel that handles the logic and state for displaying and updating measurement data in the app.
 * This includes managing chart data, user input, and state transitions (e.g., loading, error handling).
 *
 * @property measurementProvider The provider responsible for retrieving and updating measurement data.
 * @property serviceError The service responsible for handling errors, including critical data errors.
 */
class MeasurementInfoViewModel(
    private val measurementProvider: MeasurementProvider = Inject.measurementProvider,
    private val serviceError: ServiceErrorHandler = Inject.serviceErrorHandler
) : ViewModel() {

    private val _uiState = MutableStateFlow(MeasurementInfoData())
    /** State flow that holds the UI state for the measurement info screen. */
    val uiState: StateFlow<MeasurementInfoData> = _uiState

    private val _dialogState = MutableStateFlow("")
    /** State flow that holds the UI state for the dialog screen. */
    val dialogState: StateFlow<String> = _dialogState

    init {
        viewModelScope.launch {
            // Collect selected measurement data and update the UI state with chart data.
            measurementProvider.getSelectedMeasurement().collect {
                val measureEntries: List<Entry> =
                    if (it.measurements.values.firstOrNull() != null) {
                        filterEntries(it.measurements.values.first())
                    } else emptyList()
                _uiState.update { old ->
                    old.copy(
                        data = LineChartData(
                            maxValue = measureEntries.maxOfOrNull { value -> value.y } ?: 0f,
                            minValue = measureEntries.minOfOrNull { value -> value.y } ?: 0f,
                            suffix = if (it.measurements.keys.isNotEmpty()) it.measurements.keys.first() else null,
                            list = measureEntries
                        ),
                        loading = false
                    )
                }
            }
        }
    }

    /**
     * Filters a list of measurements, grouping by the day of the month, and returning the most recent
     * measurement per day.
     *
     * @param measurements The list of measurements to filter.
     * @return A list of [Entry] objects representing the filtered data for the line chart.
     */
    private fun filterEntries(measurements: List<Measurement>): List<Entry> {
        return measurements
            .groupBy { it.date.dayOfMonth }
            .mapNotNull { (_, dailyMeasurements) ->
                dailyMeasurements.maxByOrNull { it.date }?.let { measurement ->
                    Entry(measurement.date.dayOfMonth.toFloat(), measurement.value.toFloat())
                }
            }
            .sortedBy { it.x }
    }


    /**
     * Updates the history input state based on user input.
     *
     * @param text The new text input entered by the user.
     */
    fun onHistoryInputChange(text: String) {
        if (text.isEmpty() || text.matches(Regex("^\\d+\$"))) {
            _dialogState.update { text }
        }
    }

    /**
     * Toggles the visibility of the dialog (e.g., for showing an input dialog).
     */
    fun changeShowDialog() {
        _uiState.update { old ->
            old.copy(showDialog = old.showDialog.not())
        }
    }

    /**
     * Updates the title of the measurement data (e.g., the type of measurement, such as "Weight").
     *
     * @param title The new title for the measurement data.
     */
    fun updateTitle(title: String) {
        _uiState.update { old -> old.copy(dataType = title) }
    }

    /**
     * Saves the user input as a measurement. This involves validating and filtering the input before
     * updating the measurement data provider with the new value.
     */
    fun saveInput() {
//        viewModelScope.launch {
//            filterInput(_dialogState.value)?.let {
//                MeasurementType.fromKey(_uiState.value.dataType)?.let { enumValue ->
//                    measurementProvider.updateMeasurement(
//                        enumValue,
//                        Measurement(LocalDateTime.now(), it)
//                    )
//                    changeShowDialog()
//                }
//            }
//        }
    }

    /**
     * Validates and filters the input string, ensuring it represents a valid, non-negative double value.
     *
     * @param input The string input to validate.
     * @return The filtered value as a [Double] if valid, or null if invalid.
     */
    private fun filterInput(input: String): Double? {
        val numberRegex = Regex("\\d*\\.?\\d+")
        val matchResult = numberRegex.find(input)
        return matchResult?.value?.toDoubleOrNull()?.takeIf { it >= 0.0 }
            ?.let { String.format("%.2f", it).toDouble() }
    }
}
