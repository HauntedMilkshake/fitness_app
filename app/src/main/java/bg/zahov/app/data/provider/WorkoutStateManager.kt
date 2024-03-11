package bg.zahov.app.data.provider

import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WorkoutStateManager {
    companion object {

        @Volatile
        private var instance: WorkoutStateManager? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: WorkoutStateManager().also { instance = it }
        }
    }

    private val _state = MutableSharedFlow<WorkoutState>()
    val state: SharedFlow<WorkoutState>
        get() = _state

    private val _template = MutableStateFlow<Workout?>(null)
    val template: StateFlow<Workout?>
        get() = _template

    private val _timer = MutableSharedFlow<Long>()
    val timer: SharedFlow<Long>
        get() = _timer

    private var job: Job? = null

    private suspend fun startTimer() {

        job = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                _timer.emit(lastTime)
                delay(1000)
                lastTime += 1000
            }
        }
    }

    private fun stopTimer() = job?.cancel().also { job = null }

    private var lastTime: Long = 0L
    suspend fun updateState(newState: WorkoutState) {
        when (newState) {
            WorkoutState.INACTIVE -> {
                stopTimer()
                lastTime = 0L
            }

            else -> {
                if (job == null) {
                    startTimer()
                }
            }
        }
        _state.emit(newState)
    }

    suspend fun updateTemplate(workout: Workout) {
        updateState(WorkoutState.ACTIVE)
        _template.value = workout
    }
}