package bg.zahov.app.ui.exercise.info.records

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.getWorkoutProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch

class ExerciseRecordsViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutProvider by lazy {
        application.getWorkoutProvider()
    }
    private val _state = MutableLiveData<Data>()
    val state: LiveData<Data>
        get() = _state
    private lateinit var job: Job

    fun initData() {
        var maxVolume = 0.0
        var maxWeight = 0.0
        var oneRepMax = 0.0
        job = viewModelScope.launch(NonCancellable) {
            workoutProvider.getExerciseHistory().collect { data ->
                data.forEach {
                    it.sets.forEach { set ->
                        val reps = set.secondMetric ?: 0
                        val weight = set.firstMetric ?: 0.0
                        //the most ammount of weight with the sets
                        if (maxVolume < reps * weight) {
                            maxVolume = reps * weight
                        }
                        //simply the most amount of weight lifted ever a set without considering the sets
                        if (maxWeight < weight) {
                            maxWeight = weight
                        }
                    }
                    //estimates
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

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    data class Data(val oneRepMax: String, val maxVolume: String, val maxWeight: String)
}