package bg.zahov.app.ui.measures.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.interfaces.MeasurementProvider
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.model.LineChartData
import bg.zahov.app.data.model.Measurement
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MeasurementInfoViewModel(
    private val measurementProvider: MeasurementProvider = Inject.measurementProvider,
    private val serviceError: ServiceErrorHandler = Inject.serviceErrorHandler
) :
    ViewModel() {
    data class MeasurementInfoData(
        val data: LineChartData = LineChartData(),
        val loading: Boolean = true,
        val showDialog: Boolean = false,
        val historyInput: String = "",
    )

    private val _uiState = MutableStateFlow(MeasurementInfoData())
    val uiState: StateFlow<MeasurementInfoData> = _uiState

    init {
        viewModelScope.launch {
            try {
                measurementProvider.getSelectedMeasurement().collect {
                    var measureEntries: List<Entry> = listOf()
                    if (it.measurements.values.isNotEmpty()) {
                        measureEntries = filterEntries(it.measurements.values.first())
                    }
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
            } catch (e: CriticalDataNullException) {
                serviceError.initiateCountdown()
            }
        }
    }

    private fun filterEntries(measurements: List<Measurement>): List<Entry> {
        return measurements
            .groupBy { it.date.dayOfMonth }
            .mapNotNull { (_, dailyMeasurements) -> dailyMeasurements.maxByOrNull { it.date } }
            .map { measurement ->
                Entry().apply {
                    x = measurement.date.dayOfMonth.toFloat()
                    y = measurement.value.toFloat()
                }
            }
            .sortedBy { it.x }
    }

    fun onHistoryInputChange(text: String) {
        _uiState.update { old -> old.copy(historyInput = text) }
    }
}