package bg.zahov.app.ui.workout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.ClickableSet
import bg.zahov.app.data.model.InteractableExerciseWrapper
import bg.zahov.app.data.model.RestState
import bg.zahov.app.data.model.SetType
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutState
import bg.zahov.app.getAddExerciseToWorkoutProvider
import bg.zahov.app.getRestTimerProvider
import bg.zahov.app.getWorkoutProvider
import bg.zahov.app.getWorkoutStateManager
import bg.zahov.app.util.currDateToString
import bg.zahov.app.util.hashString
import bg.zahov.app.util.parseTimeStringToLong
import bg.zahov.app.util.toExercise
import bg.zahov.app.util.toInteractableExerciseWrapper
import bg.zahov.fitness.app.R
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.Random

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

    private val _exercises = MutableLiveData<List<InteractableExerciseWrapper>>(mutableListOf())
    val exercises: LiveData<List<InteractableExerciseWrapper>>
        get() = _exercises

    private val _name = MutableLiveData("New workout")
    val name: LiveData<String>
        get() = _name

    private val _timer = MutableLiveData<String>()
    val timer: LiveData<String>
        get() = _timer

    private val _restTimerState = MutableLiveData<State>(State.Default(false))
    val restTimer: LiveData<State>
        get() = _restTimerState

    private val _note = MutableLiveData<String>()
    val note: LiveData<String>
        get() = _note

    private var exerciseToReplaceIndex: Int? = null

    init {
        viewModelScope.launch {
            launch {
                workoutStateManager.template.collect {
                    it?.let {
                        _exercises.postValue(it.exercises.map { exercises -> exercises.toInteractableExerciseWrapper() })
                        _name.postValue(it.name)
                    } ?: run {
                        _name.postValue("New Workout")
                        //TODO(Technically this manager does not perceive state for notes)
                        workoutStateManager.updateTemplate(
                            Workout(
                                hashString("New workout"),
                                "New workout",
                                duration = null,
                                date = currDateToString(),
                                isTemplate = false,
                                exercises = listOf()
                            )
                        )
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
                    val updatedExercises = _exercises.value.orEmpty().toMutableList()
                    updatedExercises.addAll(it)
                    _exercises.postValue(updatedExercises)
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

    fun onExerciseReplace(item: InteractableExerciseWrapper) {
        exerciseToReplaceIndex = _exercises.value?.indexOf(item)
    }

    fun onSetCheckClicked(exercise: InteractableExerciseWrapper, set: ClickableSet) {
        val captured = _exercises.value.orEmpty()
        captured.find { it == exercise }?.sets?.find { it == set }?.clicked = !(captured.find { it == exercise }?.sets?.find { it == set }?.clicked ?: true)
        _exercises.value = captured
    }

    fun onNoteToggle(position: Int) {
        val captured = _exercises.value.orEmpty()
        captured[position].isNoteVisible = !captured[position].isNoteVisible
        _exercises.value = captured
    }

    fun removeExercise(item: InteractableExerciseWrapper) {
        val captured = _exercises.value.orEmpty().toMutableList()
        captured.remove(item)
    }

    fun addSet(item: InteractableExerciseWrapper, set: ClickableSet) {
        val captured = _exercises.value.orEmpty().toMutableList()
        captured.find { it == item }?.let {
            val newSets = it.sets.toMutableList()
            newSets.add(set)
            newSets.let { sets ->
                it.sets = sets
            }
        }
        _exercises.value = captured
    }

    fun removeSet(item: InteractableExerciseWrapper, set: ClickableSet) {
        val captured = _exercises.value.orEmpty().toMutableList()
        captured.find { it == item }?.let {
            val newSets = it.sets.toMutableList()
            newSets.remove(set)
            newSets.let { sets ->
                it.sets = sets
            }
        }
        _exercises.value = captured
    }

    fun onInputFieldTextChanged(
        exercise: InteractableExerciseWrapper,
        set: ClickableSet,
        metric: String,
        viewId: Int,
    ) {
        val new = _exercises.value.orEmpty()
        when (viewId) {
            R.id.first_input_field_text -> {
                new.find { it == exercise }?.sets?.find { it == set }?.set?.firstMetric = metric.toDoubleOrNull()
            }

            R.id.second_input_field_text -> {
                new.find { it == exercise }?.sets?.find { it == set }?.set?.secondMetric = metric.toIntOrNull()
            }
        }
        _exercises.value = new
    }

    fun onSetTypeChanged(exercise: InteractableExerciseWrapper, set: ClickableSet, newType: SetType) {
        val captured = _exercises.value.orEmpty()
        captured.find { it == exercise }?.sets?.find { it == set }?.set?.type = newType.key
        _exercises.value = captured
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
            repo.addWorkoutToHistory(
                Workout(
                    id = hashString("${Random().nextInt(Int.MAX_VALUE)}"),
                    name = "${getTimePeriodAsString()} ${_name.value}",
                    date = currDateToString(),
                    exercises = _exercises.value?.map { it.toExercise() } ?: emptyList(),
                    note = _note.value,
                    duration = _timer.value?.parseTimeStringToLong() ?: 0L,
                    isTemplate = false
                )
            )
            addExerciseToWorkoutProvider.resetSelectedExercises()
            workoutStateManager.updateState(WorkoutState.INACTIVE)
        }
    }

    fun onNoteChange(newNote: String) {
        _note.value = newNote
    }

    private fun getTimePeriodAsString() = when (LocalTime.now().hour) {
        in 6..11 -> "Morning"
        in 12..16 -> "Noon"
        in 17..20 -> "Afternoon"
        else -> "Night"
    }

    sealed interface State {
        data class Default(val restState: Boolean) : State
        data class Rest(val time: String) : State
    }
}