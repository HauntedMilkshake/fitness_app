package bg.zahov.app.ui.measures.info

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.getUserProvider
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.launch

class MeasurementInfoViewModel(application: Application) : AndroidViewModel(application) {
    private val userProvider by lazy {
        application.getUserProvider()
    }
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    init {
        viewModelScope.launch {
            _state.postValue(State.Loading(View.VISIBLE))
            val measureEntries = mutableListOf<Entry>()
            try {
                userProvider.getSelectedMeasure().collect {
                    it.measurements.sortedBy { item -> item.date.monthValue }.forEach { measurement ->
                        measureEntries.add(
                            Entry(
                                measurement.value.toFloat(),
                                measurement.date.dayOfMonth.toFloat()
                            )
                        )
                    }
                    _state.postValue(State.Data(measureEntries))
                }
            } catch (e: CriticalDataNullException) {
                _state.postValue(State.Error(true))
            }
        }
    }

    sealed interface State {
        data class Loading(val loadingVisibility: Int) : State
        data class Data(val entries: List<Entry>) : State
        data class Error(val shutdown: Boolean) : State
    }
}