package bg.zahov.app.ui.measures.info

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.model.Measurement
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.getMeasurementsProvider
import bg.zahov.app.getServiceErrorProvider
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.launch

class MeasurementInfoViewModel(application: Application) : AndroidViewModel(application) {
    private val measurementProvider by lazy {
        application.getMeasurementsProvider()
    }
    private val serviceError by lazy {
        application.getServiceErrorProvider()
    }
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    init {
        viewModelScope.launch {
            _state.postValue(State.Loading(View.VISIBLE))
            var measureEntries = listOf<Entry>()
            var measurementType: MeasurementType?
            try {
                measurementProvider.getSelectedMeasurement().collect {
                    measurementType =
                        if (it.measurements.keys.isNotEmpty()) it.measurements.keys.first() else null
                    if (it.measurements.values.isNotEmpty()) {
                        measureEntries = filterEntries(it.measurements.values.first())
                    }
                    var measurementMax = 0f
                    var measurementMin = 0f
                    if (measureEntries.isNotEmpty()) {
                        measurementMax = measureEntries.maxOf { value -> value.y }
                        measurementMin = measureEntries.minOf { value -> value.y }
                    }
                    val measurementSuffix = when (measurementType) {
                        MeasurementType.Weight -> "kg"
                        MeasurementType.BodyFatPercentage -> "%"
                        MeasurementType.CaloricIntake -> "kcal"
                        else -> "sm"
                    }

                    _state.postValue(
                        State.Data(
                            maxValue = measurementMax,
                            minValue = measurementMin,
                            suffix = measurementSuffix,
                            entries = measureEntries
                        )
                    )
                }
            } catch (e: CriticalDataNullException) {
                serviceError.initiateCountdown()
            }
        }
    }

    private fun filterEntries(measurements: List<Measurement>): List<Entry> {
        val latestEntriesPerDay = mutableMapOf<Int, Measurement>()

        for (measurement in measurements) {
            if (latestEntriesPerDay[measurement.date.dayOfMonth] == null || latestEntriesPerDay[measurement.date.dayOfMonth]!!.date.isBefore(
                    measurement.date
                )
            ) {
                latestEntriesPerDay[measurement.date.dayOfMonth] = measurement
            }
        }
        val entries = latestEntriesPerDay.values.toList()
        val result = entries.map { measurement ->
            Entry().apply {
                x = measurement.date.dayOfMonth.toFloat()
                y = measurement.value.toFloat()
            }
        }.sortedBy { entry -> entry.x }

        return result
    }


    sealed interface State {
        data class Loading(val loadingVisibility: Int) : State
        data class Data(
            val maxValue: Float,
            val minValue: Float,
            val suffix: String,
            val entries: List<Entry>,
        ) : State
    }
}