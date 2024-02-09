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
import kotlinx.coroutines.flow.collect
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
            workoutState.getState().collect {
                when(it) {
                    WorkoutState.MINIMIZED -> _state.postValue(State.Active(true, "Cannot create templates during workout"))
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
            Log.d("WORKOUT","Starting workout fragment")
            workoutState.setWorkoutState(WorkoutState.ACTIVE)
        }
    }

    fun startWorkoutFromTemplate(workout: Workout) {
        viewModelScope.launch {
            workoutState.setTemplate(workout)
            workoutState.setWorkoutState(WorkoutState.ACTIVE)
        }
    }

    sealed interface State {
        data class Error(val error: String?, val shutdown: Boolean) : State

        data class Active(val isWorkoutActive: Boolean, val message: String? = null): State
    }
}
