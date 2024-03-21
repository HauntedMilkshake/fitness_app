package bg.zahov.app

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.interfaces.WorkoutStateListener
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutState
import bg.zahov.app.util.timeToString
import kotlinx.coroutines.launch

class WorkoutManagerViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutStateManager by lazy {
        application.getWorkoutStateManager()
    }
    private val workoutProvider by lazy {
        application.getWorkoutProvider()
    }

    private val _state = MutableLiveData<State>(State.Inactive(View.GONE))
    val state: LiveData<State>
        get() = _state

    private val _template = MutableLiveData<Workout>()
    val template: LiveData<Workout>
        get() = _template

    private val _timer = MutableLiveData<String>()
    val timer: LiveData<String>
        get() = _timer

    val workoutStateListener: WorkoutStateListener? = null

    init {
        viewModelScope.launch {
            checkPreviousState(workoutProvider.getPreviousWorkoutState())
            launch {
                workoutStateManager.template.collect {
                    it?.let { _template.postValue(it) }
                }
            }

            launch {
                workoutStateManager.state.collect {
                    _state.postValue(
                        when (it) {
                            WorkoutState.MINIMIZED -> State.Minimized(View.VISIBLE)
                            WorkoutState.ACTIVE -> State.Active(View.GONE, true)
                            else -> State.Inactive(View.GONE)
                        }
                    )

                }
            }

            launch {
                workoutStateManager.timer.collect {
                    _timer.postValue(
                        it.timeToString()
                    )
                }
            }
        }
    }


    fun updateStateToActive() {
        viewModelScope.launch {
            workoutStateManager.updateState(WorkoutState.ACTIVE)
        }
    }

    private suspend fun checkPreviousState(previousState: bg.zahov.app.data.local.WorkoutState) {
        if (previousState.id != "default") {
            workoutStateManager.resumeWorkout(previousState)
        }
    }

    fun saveWorkoutState() {
        workoutStateListener?.saveWorkoutState()
    }

    sealed interface State {
        data class Active(val visibility: Int, val openWorkout: Boolean = false) : State
        data class Minimized(val visibility: Int) : State
        data class Inactive(val visibility: Int) : State
    }

}