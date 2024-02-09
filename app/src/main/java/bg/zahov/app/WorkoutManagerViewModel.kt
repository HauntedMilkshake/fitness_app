package bg.zahov.app

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class WorkoutManagerViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutStateManager by lazy {
        application.getWorkoutStateManager()
    }

    private val _state = MutableLiveData<State>(State.Active(WorkoutState.INACTIVE))
    val state: LiveData<State>
        get() = _state

    init {
        viewModelScope.launch {
            workoutStateManager.getState().collect {
                Log.d("WORKOUT COLLECT", it.name)
                _state.postValue(State.Active(it))
            }
        }
    }

    private val _template = MutableLiveData<Workout>()
    val template: LiveData<Workout>
        get() = _template

    sealed interface State {
        data class Active(val state: WorkoutState) : State
    }
}