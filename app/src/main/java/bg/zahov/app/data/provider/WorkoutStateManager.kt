package bg.zahov.app.data.provider

import android.util.Log
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

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

    private val _timer = MutableSharedFlow<Long>()
    val timer: SharedFlow<Long>
        get() = _timer

    private suspend fun startTimer() = CoroutineScope(Dispatchers.Default).launch {
        _timer.emit(lastTime)
        while (true) {
            delay(1000)
            Log.d("EMIT", lastTime.toString())
            lastTime += 1000
        }
    }


    private suspend fun stopTimer() = startTimer().cancel()


//    private val _timer =  flow {
//        while (timerFlag) {
//            emit(lastTime)
//            delay(1000)
//            lastTime += 1000
//
//            Log.d("time", lastTime.toString())
//        }
//    }
//
//    val timer
//        get() = _timer

    private var timerFlag: Boolean = false
    private var lastTime: Long = 0L
    suspend fun updateState(newState: WorkoutState) {
        when (newState) {
            WorkoutState.INACTIVE -> {
                stopTimer()
            }

            else -> {
                if (!startTimer().isActive) {
                    startTimer()
                }
            }
        }
        _state.value = newState
    }

    fun updateTemplate(workout: Workout) {
        _template.value = workout
    }
}