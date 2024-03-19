package bg.zahov.app.ui.history.info

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutState
import bg.zahov.app.getWorkoutProvider
import bg.zahov.app.getWorkoutStateManager
import bg.zahov.app.util.getOneRepMaxes
import bg.zahov.app.util.timeToString
import bg.zahov.app.util.toFormattedString
import kotlinx.coroutines.launch

class HistoryInfoViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutStateProvider by lazy {
        application.getWorkoutStateManager()
    }
    private val workoutProvider by lazy {
        application.getWorkoutProvider()
    }
    private lateinit var workout: Workout
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    fun delete() {
        viewModelScope.launch {
            workoutProvider.deleteWorkout(workout)
        }
    }

    fun saveAsTemplate() {
        viewModelScope.launch {
            try {
                workoutProvider.getTemplateWorkouts().collect {
                    if (!it.contains(workout)) {
                        workoutProvider.addTemplateWorkout(workout)
                    } else {
                        _state.postValue(
                            State.Notify(
                                (_state.value as? State.Data)?.data,
                                "Such a workout already exists!"
                            )
                        )
                    }
                }
            } catch (e: CriticalDataNullException) {
                _state.postValue(State.CriticalError(shutdown = true))
            }
        }
    }

    fun performAgain() {
        viewModelScope.launch {
            workoutStateProvider.state.collect {
                when (it) {
                    WorkoutState.INACTIVE -> workoutStateProvider.updateTemplate(workout)
                    else -> _state.postValue(
                        State.Notify(
                            (_state.value as? State.Data)?.data,
                            "Unable to start workout whenever one is active!"
                        )
                    )
                }
            }
        }
    }

    fun queryWorkout(id: String) {
        Log.d("GIVEN ID", id)
        viewModelScope.launch {
            if (id.isNotEmpty()) {
                try {
                    workout = workoutProvider.getPastWorkoutById(id)
                    _state.postValue(State.Data(createAdapterData(workout)))
                } catch (e: Exception) {
                    _state.postValue(State.CriticalError(shutdown = true))
                }
            } else {
                _state.postValue(State.CriticalError(shutdown = true))
            }
        }
    }

    private fun createAdapterData(workout: Workout) = HistoryInfoData(
        adapterData = workout.exercises.map { exercise ->
            HistoryInfo(
                exercise.name,
                exercise.sets,
                exercise.getOneRepMaxes()
            )
        },
        workoutName = workout.name,
        workoutDate = workout.date.toFormattedString(),
        duration = (workout.duration ?: 0L).timeToString(),
        volume = (workout.volume ?: 0).toString(),
        prs = workout.personalRecords.toString()
    )

    sealed interface State {
        data class Data(val data: HistoryInfoData) : State
        data class CriticalError(val shutdown: Boolean) : State
        data class Notify(val data: HistoryInfoData? = null, val message: String) : State
    }
}

data class HistoryInfoData(
    val adapterData: List<HistoryInfo>,
    val workoutName: String,
    val workoutDate: String,
    val duration: String,
    val volume: String,
    val prs: String,
)