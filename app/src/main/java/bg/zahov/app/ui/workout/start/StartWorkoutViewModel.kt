package bg.zahov.app.ui.workout.start

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
import bg.zahov.app.util.currDateToString
import bg.zahov.app.util.hashString
import kotlinx.coroutines.launch

class StartWorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val repo by lazy {
        application.getWorkoutProvider()
    }

    private val workoutState by lazy {
        application.getWorkoutStateManager()
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _templates = MutableLiveData<List<Workout>>(listOf())
    val templates: LiveData<List<Workout>>
        get() = _templates

    init {
        getWorkouts()
        viewModelScope.launch {
            workoutState.state.collect {
                when(it) {
                    WorkoutState.MINIMIZED -> _state.postValue(State.Active(true))
                    WorkoutState.INACTIVE -> _state.postValue(State.Active(false))
                    else -> {}
                }
            }
        }
    }

    private fun getWorkouts() {
        viewModelScope.launch {
            try {
                repo.getTemplateWorkouts().collect {
                    _templates.postValue(it)
                }
            } catch (e: CriticalDataNullException) {
                _state.postValue(State.Error(e.message, false))
            }
        }
    }

    fun startEmptyWorkout() {
        viewModelScope.launch {
            workoutState.updateState(WorkoutState.ACTIVE)
        }
    }

    fun startWorkoutFromTemplate(workout: Workout) {
        viewModelScope.launch {
            workoutState.updateTemplate(workout)
            workoutState.updateState(WorkoutState.ACTIVE)
        }
    }

    fun deleteTemplateWorkout(workout: Workout) {
        viewModelScope.launch {
            repo.deleteTemplateWorkout(workout)
        }
    }

    fun addDuplicateTemplateWorkout(workout: Workout) {
        viewModelScope.launch {
            repo.addTemplateWorkout(
                Workout(
                    id = hashString(workout.name + "copy"),
                    name = "${workout.id} + copy",
                    duration = null,
                    date = currDateToString(),
                    isTemplate = true,
                    exercises = listOf(),
                    ids = workout.ids
                )
            )
        }
    }
    sealed interface State {
        data class Error(val error: String?, val shutdown: Boolean) : State

        data class Active(val isWorkoutActive: Boolean, val message: String? = null): State
    }
}
