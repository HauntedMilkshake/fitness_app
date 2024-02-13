package bg.zahov.app

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    //    val timer = workoutStateManager.timer.map {
//        String.format(
//            "%02d:%02d:%02d",
//            (it / (1000 * 60 * 60)) % 24,
//            (it / (1000 * 60)) % 60,
//            (it / 1000) % 60
//        )
//    }
//        .asLiveData(viewModelScope.coroutineContext)
    private val _timer = MutableLiveData<String>()
    val timer: LiveData<String>
        get() = _timer

    init {
        viewModelScope.launch {
            launch {
                workoutStateManager.template.collect {
                    it?.let { _template.postValue(it) }
                }
            }

            launch {
                workoutStateManager.state.collect {
                    Log.d("STATE", it.name)
                    _state.postValue(State.Active(it))

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
        }
    }


    fun updateStateToActive() {
        viewModelScope.launch {
            Log.d("UPDATING STATE FROM ACTIVITY", "ACTIVE")
            workoutStateManager.updateState(WorkoutState.ACTIVE)
        }
    }

    sealed interface State {
        data class Active(val state: WorkoutState) : State
    }

}