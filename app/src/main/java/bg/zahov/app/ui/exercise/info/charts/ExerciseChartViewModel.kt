package bg.zahov.app.ui.exercise.info.charts

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.getWorkoutProvider
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch

class ExerciseChartViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutProvider by lazy {
        application.getWorkoutProvider()
    }
    private val _oneRepMax = MutableLiveData<List<Entry>>()
    val oneRepMax: LiveData<List<Entry>>
        get() = _oneRepMax

    private val _totalVolume = MutableLiveData<List<Entry>>()
    val totalVolume: LiveData<List<Entry>>
        get() = _totalVolume

    private val _bestSet = MutableLiveData<List<Entry>>()
    val bestSet: LiveData<List<Entry>>
        get() = _bestSet
    private lateinit var job: Job
    init {
        var maxVolume = 0.0
        var maxWeight = 0.0
        var oneRepMax = 0.0
        val maxVolumeEntries = mutableListOf<Entry>()
        val oneRepMaxEntries = mutableListOf<Entry>()
        val maxWeightEntries = mutableListOf<Entry>()

        job = viewModelScope.launch(NonCancellable) {
            workoutProvider.getExerciseHistory().collect { data ->
                data.forEach {
                    it.sets.forEach { set ->
                        if (maxVolume < (set.secondMetric ?: 0) * (set.firstMetric ?: 0.0)) {
                            maxVolume =
                                (set.secondMetric ?: 0).toDouble() * (set.firstMetric ?: 0.0)
                            it.date?.let { date ->
                                maxVolumeEntries.add(
                                    Entry(
                                        maxVolume.toFloat(),
                                        date.dayOfMonth.toFloat()
                                    )
                                )
                            }
                        }

                        if (maxWeight < (set.firstMetric ?: 0.0)) {
                            maxWeight = (set.firstMetric ?: 0.0)
                            it.date?.let { date ->
                                maxWeightEntries.add(
                                    Entry(
                                        maxWeight.toFloat(),
                                        date.dayOfMonth.toFloat()
                                    )
                                )
                            }
                        }
                    }
                    it.oneRepMaxes.forEach { pr ->
                        if (oneRepMax < pr.toDouble()) {
                            oneRepMax = pr.toDouble()
                            it.date?.let { date ->
                                oneRepMaxEntries.add(
                                    Entry(
                                        oneRepMax.toFloat(),
                                        date.dayOfMonth.toFloat()
                                    )
                                )
                            }
                        }
                    }
                }
                Log.d("totalVolume", maxVolumeEntries.toString())
                _totalVolume.postValue(maxVolumeEntries)
                Log.d("ORP", oneRepMaxEntries.toString())
                _oneRepMax.postValue(oneRepMaxEntries)
                Log.d("bestWeight", maxWeightEntries.toString())
                _bestSet.postValue(maxWeightEntries)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}