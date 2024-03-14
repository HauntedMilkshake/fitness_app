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
import bg.zahov.app.getWorkoutProvider
import bg.zahov.app.getWorkoutStateManager
import bg.zahov.app.ui.exercise.ExerciseAdapterWrapper
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
    private val _state = MutableLiveData<State>(State.Default)
    var workoutId: String? = ""
    val state: LiveData<State>
        get() = _state
    private val _workoutName = MutableLiveData<String>()
    val workoutName: LiveData<String>
        get() = _workoutName


    private var workoutState: WorkoutState? = null
    private var workout: Workout? = null

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

                        }
                    } catch (e: CriticalDataNullException) {
                        _state.postValue(State.Error(true))
                    }
                } else {
                    _state.postValue(State.Error(true))
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
        workout?.let {
            if (workoutState != null && workoutState == WorkoutState.INACTIVE) {
                viewModelScope.launch {
                    workoutStateProvider.updateTemplate(it)
                }
            } else {
                (_state.value as? State.Data)?.let { state ->
                    _state.value = State.WorkoutActive(
                        "Cannot start a workout at this time",
                        state.lastPerformed,
                        state.exercises
                    )
                }
            }
        }
    }

    fun duplicateWorkout() {

    }

    fun deleteWorkout() {

    }

    sealed interface State {
        object Default : State
        data class Data(val lastPerformed: String, val exercises: List<ExerciseAdapterWrapper>) :
            State

        data class Error(val shutdown: Boolean) : State
        data class Loading(val loadingVisibility: Int) : State

        data class WorkoutActive(
            val message: String,
            val lastPerformed: String,
            val exercises: List<ExerciseAdapterWrapper>,
        ) : State
    }
}

