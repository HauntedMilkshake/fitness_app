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

    //first - max, second - min, third - data
    private val _oneRepMax = MutableLiveData<Triple<Float, Float, List<Entry>>>()
    val oneRepMax: LiveData<Triple<Float, Float, List<Entry>>>
        get() = _oneRepMax

    private val _totalVolume = MutableLiveData<Triple<Float, Float, List<Entry>>>()
    val totalVolume: LiveData<Triple<Float, Float, List<Entry>>>
        get() = _totalVolume

    private val _maxReps = MutableLiveData<Triple<Float, Float, List<Entry>>>()
    val maxReps: LiveData<Triple<Float, Float, List<Entry>>>
        get() = _maxReps
    private lateinit var job: Job

    fun initChartData() {
        //most reps despite the weight
        val maxVolumeEntries = mutableListOf<Entry>()
        //most weight for a single repetition
        val oneRepMaxEntries = mutableListOf<Entry>()
        //most weight for any number of repetitions
        val maxWeightEntries = mutableListOf<Entry>()

        job = viewModelScope.launch(NonCancellable) {
            workoutProvider.getExerciseHistory().collect { data ->
                Log.d("exercise history", data.toString())
                data.forEach {
                    it.sets.forEach { set ->
                        when (set.type) {
                            SetType.WARMUP, SetType.FAILURE -> {}
                            SetType.DROP_SET, SetType.DEFAULT -> {
                                val reps = set.secondMetric ?: 0
                                val weight = set.firstMetric ?: 0.0

                                it.date?.let { date ->
                                    maxVolumeEntries.add(
                                        Entry(
                                            date.dayOfMonth.toFloat(),
                                            reps.toFloat()
                                        )
                                    )

                                    maxWeightEntries.add(
                                        Entry(
                                            date.dayOfMonth.toFloat(),
                                            (reps.toDouble() * weight).toFloat()
                                        )
                                    )
                                }
                            }
                        }
                    }

                    it.oneRepMaxes.forEach { pr ->
                        it.date?.let { date ->
                            oneRepMaxEntries.add(
                                Entry(
                                    date.dayOfMonth.toFloat(),
                                    pr.toFloat()
                                )
                            )
                        }
                    }
                }

                val totalVolumeEntries = filterEntries(maxWeightEntries).sortedBy { it.x }
                _totalVolume.postValue(
                    Triple(
                        if (totalVolumeEntries.isNotEmpty()) totalVolumeEntries.maxOf { it.y } else 0f,
                        if (totalVolumeEntries.isNotEmpty()) totalVolumeEntries.minOf { it.y } else 0f,
                        totalVolumeEntries
                    )
                )

                val oneRepMaxes = filterEntries(oneRepMaxEntries).sortedBy { it.x }
                _oneRepMax.postValue(
                    Triple(
                        if (oneRepMaxes.isNotEmpty()) oneRepMaxes.maxOf { it.y } else 0f,
                        if (oneRepMaxes.isNotEmpty()) oneRepMaxes.minOf { it.y } else 0f,
                        oneRepMaxes
                    )
                )

                val maxRepsEntries = filterEntries(maxVolumeEntries).sortedBy { it.x }
                _maxReps.postValue(
                    Triple(
                        if (maxRepsEntries.isNotEmpty()) maxRepsEntries.maxOf { it.y } else 0f,
                        if (maxRepsEntries.isNotEmpty()) maxRepsEntries.minOf { it.y } else 0f,
                        maxRepsEntries
                    )
                )
            }
        }
    }

    private fun filterEntries(entries: List<Entry>): List<Entry> {
        val groupedEntries = HashMap<Float, Entry>()

        for (entry in entries) {
            if (!groupedEntries.containsKey(entry.x)) {
                groupedEntries[entry.x] = entry
            } else {
                val existingEntry = groupedEntries[entry.x]!!
                if (entry.y > existingEntry.y) {
                    groupedEntries[entry.x] = entry
                }
            }
        }
        return groupedEntries.values.toList()
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}