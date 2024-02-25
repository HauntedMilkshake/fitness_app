package bg.zahov.app.ui.workout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.ExerciseWithNoteVisibility
import bg.zahov.app.data.model.RestState
import bg.zahov.app.data.model.Sets
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutState
import bg.zahov.app.getAddExerciseToWorkoutProvider
import bg.zahov.app.getRestTimerProvider
import bg.zahov.app.getWorkoutProvider
import bg.zahov.app.getWorkoutStateManager
import bg.zahov.app.util.currDateToString
import bg.zahov.app.util.hashString
import kotlinx.coroutines.launch

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutStateManager by lazy {
        application.getWorkoutStateManager()
    }

    private val repo by lazy {
        application.getWorkoutProvider()
    }

    private val addExerciseToWorkoutProvider by lazy {
        application.getAddExerciseToWorkoutProvider()
    }

    private val restTimerProvider by lazy {
        application.getRestTimerProvider()
    }

    private val _exercises = MutableLiveData<OnGoin>

    private val _workout = MutableLiveData<Workout>()
    val workout: LiveData<Workout>
        get() = _workout

    private val _timer = MutableLiveData<String>()
    val timer: LiveData<String>
        get() = _timer

    private val _restTimerState = MutableLiveData<State>(State.Default(false))
    val restTimer: LiveData<State>
        get() = _restTimerState

    init {
        viewModelScope.launch {
            launch {
                workoutStateManager.template.collect {
                    it?.let { _workout.postValue(it) } ?: run {
                        val workout = Workout(
                            hashString("New workout"),
                            "New workout",
                            duration = null,
                            date = currDateToString(),
                            isTemplate = false,
                            exercises = listOf()
                        )
                        _workout.postValue(workout)
                        workoutStateManager.updateTemplate(workout)
                    }
                }
            }
            launch {
                workoutStateManager.timer.collect {
                    _timer.postValue(
                        String.format(
                            "%02d:%02d:%02d",
                            (it / (1000 * 60 * 60)) % 24,
                            (it / (1000 * 60)) % 60,
                            (it / 1000) % 60
                        )
                    )
                }
            }
            launch {
                addExerciseToWorkoutProvider.selectedExercises.collect {
                    val new = _workout.value
                    val exercises = new?.exercises.orEmpty().toMutableList()
                    exercises.addAll(it.map { selectable -> selectable.exercise })
                    new?.exercises = exercises

                    new?.let { workout ->
                        _workout.postValue(workout)
                    }
                }
            }
            launch {
                restTimerProvider.restTimer.collect {
                    it.elapsedTime?.let { time ->
                        _restTimerState.postValue(State.Rest(time))
                    }
                }
            }
            launch {
                restTimerProvider.restState.collect {
                    _restTimerState.postValue(
                        when (it) {
                            RestState.Active -> State.Default(true)
                            else -> State.Default(false)
                        }
                    )
                }
            }
        }
    }

    fun removeExercise(item: ExerciseWithNoteVisibility) {
        val captured = _workout.value
        captured?.exercises.orEmpty().toMutableList().remove(item.exercise)
        captured?.let {
            _workout.value = it
        }
    }

    fun addSet(item: ExerciseWithNoteVisibility, set: Sets) {
        val captured = _workout.value
        captured?.exercises?.find { it == item.exercise }?.let {
            val newSets = it.sets.toMutableList()
            newSets.add(set)
            newSets.let { sets ->
                it.sets = sets
            }
        }
        captured?.let { _workout.value = it }
    }

    fun removeSet(item: ExerciseWithNoteVisibility, set: Sets) {
        val captured = _workout.value
        captured?.exercises?.find { it == item.exercise }?.let {
            val newSets = it.sets.toMutableList()
            newSets.remove(set)
            newSets.let { sets ->
                it.sets = sets
            }
        }
        captured?.let { _workout.value = it }
    }

    fun minimize() {
        viewModelScope.launch {
            workoutStateManager.updateState(WorkoutState.MINIMIZED)
        }
    }

    fun cancel() {
        viewModelScope.launch {
            workoutStateManager.updateState(WorkoutState.INACTIVE)
        }
    }

    fun finishWorkout() {
        viewModelScope.launch {
            _workout.value?.let {
                repo.addWorkoutToHistory(it)
            }
            workoutStateManager.updateState(WorkoutState.INACTIVE)
        }
    }

    sealed interface State {
        data class Default(val restState: Boolean) : State
        data class Rest(val time: String) : State
    }
}