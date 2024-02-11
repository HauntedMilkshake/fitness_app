package bg.zahov.app.data.provider

import android.util.Log
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class WorkoutStateManager {
    companion object {

        @Volatile
        private var instance: WorkoutStateManager? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: WorkoutStateManager().also { instance = it }
        }
    }

    private val _state = MutableStateFlow(WorkoutState.INACTIVE)
    val state: StateFlow<WorkoutState>
        get() = _state

    private val _template: MutableStateFlow<Workout?> = MutableStateFlow(null)
    val template: StateFlow<Workout?>
        get() = _template

    fun updateState(newState: WorkoutState) {
        _state.value = newState
    }

    fun updateTemplate(workout: Workout) {
        _template.value = workout
    }
}