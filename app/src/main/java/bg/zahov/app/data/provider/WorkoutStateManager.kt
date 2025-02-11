package bg.zahov.app.data.provider

import bg.zahov.app.data.interfaces.WorkoutActions
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class WorkoutStateManager @Inject constructor() : WorkoutActions {
    companion object {

        @Volatile
        private var instance: WorkoutStateManager? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: WorkoutStateManager().also { instance = it }
        }
    }

    private val _shouldSave = MutableStateFlow(false)
    val shouldSave: Flow<Boolean> = _shouldSave

    private val _state = MutableStateFlow(WorkoutState.INACTIVE)
    val state: StateFlow<WorkoutState>
        get() = _state

    private val _template = MutableStateFlow<Workout?>(null)
    val template: StateFlow<Workout?>
        get() = _template

    private val _timer = MutableStateFlow<Long>(0)
    val timer: StateFlow<Long>
        get() = _timer

    private var job: Job? = null
    private var workoutState = WorkoutState.INACTIVE
    var resuming: Boolean = false

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

    suspend fun saveWorkout() {
        _shouldSave.emit(true)
    }

    override suspend fun <T> startWorkout(workout: T?, lastTime: Long?, isResuming: Boolean) {
        if (isResuming) resuming = true
        if (updateState(WorkoutState.ACTIVE)) {
            if (workout != null && workout is Workout) {
                _template.value = workout
            }
            lastTime?.let { time -> this.lastTime = time }
            if (job == null) startTimer()
        }
    }

    override suspend fun finishWorkout() {
        clear()
    }

    override suspend fun minimizeWorkout() {
        updateState(WorkoutState.MINIMIZED)
    }

    override suspend fun cancel() {
        clear()
    }

    override suspend fun clear() {
        _template.value = null
        stopTimer()
        lastTime = 0L
        updateState(WorkoutState.INACTIVE)

    }

    private fun updateState(newState: WorkoutState): Boolean {
        return if (workoutState != newState) {
            workoutState = newState
            _state.value = workoutState
            true
        } else {
            false
        }
    }
}

