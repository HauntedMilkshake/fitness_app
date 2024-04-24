package bg.zahov.app.ui.history.info

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutState
import bg.zahov.app.getServiceErrorProvider
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
    private val serviceError by lazy {
        application.getServiceErrorProvider()
    }
    private var workout: Workout? = null
    private val _state = MutableLiveData<State>()
    private var templates: MutableList<Workout> = mutableListOf()
    private var currentWorkoutState: WorkoutState? = null
    val state: LiveData<State>
        get() = _state

    init {
        viewModelScope.launch {
            launch {
                try {
                    workoutProvider.getTemplateWorkouts().collect {
                        templates = it.toMutableList()
                    }
                } catch (e: CriticalDataNullException) {
                    serviceError.initiateCountdown()
                }
            }
            launch {
                workoutStateProvider.state.collect {
                    currentWorkoutState = it
                }
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            workout?.let {
                workoutProvider.deleteWorkout(it)
            }
        }
    }

    fun saveAsTemplate() {
        if (templates.any { it.id == workout?.id }) {
            _state.value =
                State.Notify((_state.value as? State.Data)?.data, "Such a workout already exists!")
            return
        }

        viewModelScope.launch {
            workout?.let {
                it.isTemplate = true
                it.duration = 0L
                it.volume = 0.0
                it.personalRecords = 0

                workoutProvider.addTemplateWorkout(it)
                templates.add(it)
            }

        }
    }

    fun performAgain() {
        viewModelScope.launch {
            when (currentWorkoutState) {
                WorkoutState.MINIMIZED, WorkoutState.ACTIVE -> {
                    val previousStateData = _state.value as? State.Data
                    _state.postValue(
                        State.Notify(
                            previousStateData?.data,
                            "Cannot start a workout while one is active"
                        )
                    )
                }

                WorkoutState.INACTIVE -> workoutStateProvider.startWorkout(workout)
                null -> {}//noop
            }
        }
    }

    fun queryWorkout(id: String) {
        viewModelScope.launch {
            if (id.isNotEmpty()) {
                try {
                    workout = workoutProvider.getPastWorkoutById(id)
                    workout?.let { _state.postValue(State.Data(createAdapterData(it))) }
                } catch (e: Exception) {
//                    serviceError.initiateCountdown()
                }
            } else {
                serviceError.initiateCountdown()
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
