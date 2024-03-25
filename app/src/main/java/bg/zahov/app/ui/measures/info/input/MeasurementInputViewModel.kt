package bg.zahov.app.ui.measures.info.input

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.model.Measurement
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.getUserProvider
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MeasurementInputViewModel(application: Application) : AndroidViewModel(application) {
    private val userProvider by lazy {
        application.getUserProvider()
    }
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state
    lateinit var type: String

    init {
        viewModelScope.launch {
            try {
                userProvider.getSelectedMeasure().collect {
                    Log.d("collecting measurement", "collecting ")
                    _state.postValue(
                        State.Measurement(
                            name = it.type.key,
                            date = LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yy"))
                        )
                    )
                }
            } catch (e: CriticalDataNullException) {
            }
        }
    }

    fun saveInput(input: String) {
        viewModelScope.launch {
            filterInput(input)?.let {
                if (type.isNotEmpty()) {
                    MeasurementType.fromKey(type)?.let { enumValue ->
                        userProvider.updateMeasurement(
                            enumValue,
                            Measurement(LocalDateTime.now(), it)
                        )
                        _state.postValue(State.Navigate(true))
                    }
                } else {
                    postNotifyState()
                }
            } ?: postNotifyState()
        }
    }

    private fun postNotifyState() {
        val previousState = (_state.value as? State.Measurement)
        previousState?.let {
            _state.postValue(
                State.Notify(
                    it.name,
                    it.date,
                    "Incorrect input"
                )
            )
        }
    }

    private fun filterInput(input: String): Double? {
        val numberRegex = Regex("\\d*\\.?\\d+")
        val matchResult = numberRegex.find(input)
        return matchResult?.value?.toDoubleOrNull()?.takeIf { it >= 0.0 }
            ?.let { String.format("%.2f", it).toDouble() }
    }

    sealed interface State {
        data class Measurement(val name: String, val date: String) : State
        data class Notify(val name: String, val date: String, val message: String) : State
        data class Navigate(val action: Boolean) : State
    }
}