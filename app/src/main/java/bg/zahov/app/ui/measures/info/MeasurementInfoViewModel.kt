package bg.zahov.app.ui.measures.info

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.getMeasurementsProvider
import bg.zahov.app.getUserProvider
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.launch
import kotlin.math.max

class MeasurementInfoViewModel(application: Application) : AndroidViewModel(application) {
    private val measurementProvider by lazy {
        application.getMeasurementsProvider()
    }
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    init {
        viewModelScope.launch {
            _state.postValue(State.Loading(View.VISIBLE))
            val measureEntries = mutableListOf<Entry>()
            try {
                var maxValue = 0
                measurementProvider.getSelectedMeasurement().collect {
                    if(it.measurements.values.isNotEmpty()) {
                        it.measurements.values.first().sortedBy { item -> item.date.monthValue }
                            .forEach { measurement ->
                                if(maxValue < measurement.value) maxValue = measurement.value.toInt()
                                measureEntries.add(
                                    Entry(
                                        measurement.date.dayOfMonth.toFloat(),
                                        measurement.value.toFloat()
                                    )
                                )
                            }

                    }
                    _state.postValue(State.Data(maxValue, measureEntries))
                }
            } catch (e: CriticalDataNullException) {
                _state.postValue(State.Error(true))
            }
        }
    }

    sealed interface State {
        data class Loading(val loadingVisibility: Int) : State
        data class Data(val maxValue: Int, val entries: List<Entry>) : State
        data class Error(val shutdown: Boolean) : State
    }
}