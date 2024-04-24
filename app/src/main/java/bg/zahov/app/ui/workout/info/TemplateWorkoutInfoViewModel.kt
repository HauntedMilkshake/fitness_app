package bg.zahov.app.ui.workout.info

import android.app.Application
import android.view.View
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
import bg.zahov.app.ui.exercise.ExerciseAdapterWrapper
import bg.zahov.app.util.generateRandomId
import bg.zahov.app.util.toExerciseAdapterWrapper
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class TemplateWorkoutInfoViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutProvider by lazy {
        application.getWorkoutProvider()
    }

    private val workoutStateProvider by lazy {
        application.getWorkoutStateManager()
    }

    private val serviceError by lazy {
        application.getServiceErrorProvider()
    }
    private val _state = MutableLiveData<State>(State.Default)
    var workoutId: String? = ""
    val state: LiveData<State>
        get() = _state
    private val _workoutName = MutableLiveData<String>()
    val workoutName: LiveData<String>
        get() = _workoutName


    private var workoutState: WorkoutState? = null
    private var workout: Workout? = null
    private var allWorkout: List<Workout> = listOf()

    fun fetchWorkout() {
        viewModelScope.launch {
            launch {
                _state.postValue(State.Loading(View.VISIBLE))
                if (!workoutId.isNullOrEmpty()) {
                    try {
                        workoutProvider.getTemplateWorkouts().collect { workouts ->
                            workouts.find { it.id == workoutId }?.let {
                                _state.postValue(
                                    State.Data(
                                        getDaysDifferenceInString(it.date),
                                        it.exercises.map { exercise -> exercise.toExerciseAdapterWrapper() })
                                )
                                _workoutName.postValue(it.name)
                                workout = it
                            }
                            allWorkout = workouts
                        }
                    } catch (e: CriticalDataNullException) {
                        serviceError.initiateCountdown()
                    }
                } else {
                    serviceError.initiateCountdown()
                }
            }
            launch {
                workoutStateProvider.state.collect {
                    workoutState = it
                }
            }
        }
    }

    private fun getDaysDifferenceInString(lastPerformedDate: LocalDateTime): String {
        return when (val difference =
            ChronoUnit.DAYS.between(lastPerformedDate.toLocalDate(), LocalDate.now())) {
            0L, 1L -> "Last performed yesterday"
            else -> "Last performed $difference days ago"
        }
    }

    fun startWorkout() {
        val oldState = _state.value as State.Data
        workout?.let {
            when (workoutState) {
                WorkoutState.MINIMIZED, WorkoutState.ACTIVE -> {
                    _state.value = State.Data(
                        oldState.lastPerformed,
                        oldState.exercises,
                        "Cannot start a workout when one is active!"
                    )
                }

                WorkoutState.INACTIVE -> {
                    viewModelScope.launch {
                        workoutStateProvider.startWorkout(it)
                    }
                }

                null -> {} //noop
            }
        }
    }

    fun duplicateWorkout() {
        viewModelScope.launch {
            workout?.let { workout ->
                val count = allWorkout.count { it.id == workout.id }
                workoutProvider.addTemplateWorkout(
                    Workout(
                        id = generateRandomId(),
                        name = "${workout.name} duplicate $count",
                        duration = 0L,
                        volume = 0.0,
                        date = LocalDateTime.now(),
                        isTemplate = true,
                        exercises = workout.exercises,
                        note = workout.note,
                        personalRecords = 0
                    )
                )
            }
        }
    }

    fun deleteWorkout() {
        viewModelScope.launch {
            workout?.let {
                workoutProvider.deleteTemplateWorkout(it)
            }
        }
    }

    sealed interface State {
        object Default : State
        data class Data(
            val lastPerformed: String,
            val exercises: List<ExerciseAdapterWrapper>,
            val message: String? = null,
        ) :
            State

        data class Loading(val loadingVisibility: Int) : State

    }
}

