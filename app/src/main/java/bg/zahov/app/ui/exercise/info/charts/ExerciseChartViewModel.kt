package bg.zahov.app.ui.exercise.info.charts

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.SetType
import bg.zahov.app.getWorkoutProvider
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch

class ExerciseChartViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutProvider by lazy {
        application.getWorkoutProvider()
    }
    private val _oneRepMax = MutableLiveData<Pair<Float, List<Entry>>>()
    val oneRepMax: LiveData<Pair<Float, List<Entry>>>
        get() = _oneRepMax

    private val _totalVolume = MutableLiveData<Pair<Float, List<Entry>>>()
    val totalVolume: LiveData<Pair<Float, List<Entry>>>
        get() = _totalVolume

    private val _maxReps = MutableLiveData<Pair<Float, List<Entry>>>()
    val maxReps: LiveData<Pair<Float, List<Entry>>>
        get() = _maxReps
    private lateinit var job: Job

    fun initChartData() {
        var maxVolume = 0.0
        var maxReps = 0
        var oneRepMax = 0.0
        val maxVolumeEntries = mutableListOf<Entry>()
        val oneRepMaxEntries = mutableListOf<Entry>()
        val maxWeightEntries = mutableListOf<Entry>()

        job = viewModelScope.launch(NonCancellable) {
            workoutProvider.getExerciseHistory().collect { data ->
                data.forEach {
                    it.sets.forEach { set ->
                        if (set.type == SetType.FAILURE || set.type == SetType.DEFAULT && maxVolume < (set.secondMetric
                                ?: 0) * (set.firstMetric ?: 0.0)
                        ) {
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

                        if (set.type == SetType.FAILURE || set.type == SetType.DEFAULT && maxReps < (set.secondMetric
                                ?: 0)
                        ) {
                            maxReps = (set.secondMetric ?: 0)
                            it.date?.let { date ->
                                maxWeightEntries.add(
                                    Entry(
                                        maxReps.toFloat(),
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
                                        date.dayOfMonth.toFloat(),
                                        oneRepMax.toFloat()
                                    )
                                )
                            }
                        }
                    }
                }
                Log.d("volumeEntries", maxVolumeEntries.size.toString())
                Log.d("onerepmax", oneRepMax.toString())
                Log.d("ORP", oneRepMaxEntries.toString())
                _totalVolume.postValue(Pair(maxVolume.toFloat(), maxVolumeEntries))
                _oneRepMax.postValue(Pair(oneRepMax.toFloat(), oneRepMaxEntries))
                _maxReps.postValue(Pair(maxReps.toFloat(), maxWeightEntries))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}