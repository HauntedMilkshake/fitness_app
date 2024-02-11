package bg.zahov.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutState
import kotlinx.coroutines.launch

class WorkoutManagerViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutStateManager by lazy {
        application.getWorkoutStateManager()
    }

    private val _state = MutableLiveData<State>(State.Active(WorkoutState.INACTIVE))
    val state: LiveData<State>
        get() = _state

    private val _template = MutableLiveData<Workout>()
    val template: LiveData<Workout>
        get() = _template

    init {
        viewModelScope.launch {
            workoutStateManager.state.collect {
                _state.postValue(State.Active(it))
            }
        }
    }

    fun updateStateToActive() {
        workoutStateManager.updateState(WorkoutState.ACTIVE)
    }

    sealed interface State {
        data class Active(val state: WorkoutState) : State
    }
}