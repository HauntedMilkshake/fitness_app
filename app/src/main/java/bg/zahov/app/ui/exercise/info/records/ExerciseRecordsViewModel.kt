package bg.zahov.app.ui.exercise.info.records

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.getWorkoutProvider
import kotlinx.coroutines.launch

class ExerciseRecordsViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutProvider by lazy {
        application.getWorkoutProvider()
    }
    private val _state = MutableLiveData<Data>()
    val state: LiveData<Data>
        get() = _state

    init {
        var maxVolume = 0.0
        var maxWeight = 0.0
        var oneRepMax = 0.0
        viewModelScope.launch {
            workoutProvider.getExerciseHistory().collect { data ->
                data.forEach {
                    it.sets.forEach { set ->
                        if (maxVolume < (set.secondMetric ?: 0) * (set.firstMetric ?: 0.0)) {
                            maxVolume =
                                (set.secondMetric ?: 0).toDouble() * (set.firstMetric ?: 0.0)
                        }

                        if (maxWeight < (set.firstMetric ?: 0.0)) {
                            maxWeight = (set.firstMetric ?: 0.0)
                        }
                    }
                    it.oneRepMaxes.forEach { pr ->
                        if (oneRepMax < pr.toDouble()) {
                            oneRepMax = pr.toDouble()
                        }
                    }

                }
                _state.postValue(
                    Data(
                        oneRepMax.toString(),
                        maxVolume.toString(),
                        maxWeight.toString()
                    )
                )
            }
        }
    }

    data class Data(val oneRepMax: String, val maxVolume: String, val maxWeight: String)
}