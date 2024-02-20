package bg.zahov.app.data.provider

import android.os.CountDownTimer
import android.util.Log
import bg.zahov.app.data.model.RestState
import bg.zahov.app.util.parseTimeStringToLong
import bg.zahov.app.util.timeToString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RestTimerProvider {
    companion object {
        @Volatile
        private var instance: RestTimerProvider? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: RestTimerProvider().also { instance = it }
        }
    }

    private val _restTimer = MutableStateFlow(Rest())
    val restTimer: StateFlow<Rest>
        get() = _restTimer

    private val _restState = MutableStateFlow(RestState.Default)
    val restState: StateFlow<RestState>
        get() = _restState


    private lateinit var timer: CountDownTimer
    private var remainingTime: Long = 0

    suspend fun startRest(time: Long) {
        _restState.emit(RestState.Active)
        if (remainingTime == 0L) {
            _restTimer.value.fullRest = time.timeToString()
        }
        remainingTime = time
        timer = object : CountDownTimer(time, 1000) {
            override fun onTick(p0: Long) {
                CoroutineScope(Dispatchers.Main).launch {
                    remainingTime = p0
                    _restTimer.emit(
                        Rest(
                            elapsedTime = p0.timeToString(),
                            fullRest = _restTimer.value.fullRest
                        )
                    )
                }
            }

            override fun onFinish() {
                CoroutineScope(Dispatchers.Main).launch {
                    remainingTime = 0
                    _restState.emit(RestState.Finished)
                }
            }

        }.start()
    }

    suspend fun addTime(time: Long) {
        timer.cancel()
        _restTimer.value.fullRest =
            (_restTimer.value.fullRest!!.parseTimeStringToLong() + time).timeToString()
        startRest(remainingTime + time)
    }

    suspend fun removeTime(time: Long) {
        if (remainingTime >= time) {
            remainingTime -= time
            timer.cancel()
            startRest(remainingTime)
        }
    }

    suspend fun stopTimer() {
        timer.cancel()
        remainingTime = 0
        _restState.emit(RestState.Finished)
        _restState.value = RestState.Default
    }

    data class Rest(var elapsedTime: String? = null, var fullRest: String? = null)
}