package bg.zahov.app.ui.workout.rest

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.getSettingsProvider
import bg.zahov.app.util.parseTimeStringToLong
import bg.zahov.app.util.timeToString
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class RestTimerViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsProvider by lazy {
        application.getSettingsProvider()
    }
    private val _state = MutableLiveData<State>(State.Default)
    val state: LiveData<State>
        get() = _state

    private val _increment = MutableLiveData("")
    val increment: LiveData<String>
        get() = _increment

    private val _startingTime = MutableLiveData<String>()
    val startingTime: LiveData<String>
        get() = _startingTime

    val rests: MutableList<String> = mutableListOf()

    private lateinit var timer: CountDownTimer
    private var remainingTime: Long = 0
    private var timerDelta: Long = 0

    init {
        viewModelScope.launch {
            settingsProvider.getSettings().collect {
                _increment.postValue("${(it.obj?.restTimer ?: 30)} s")
                timerDelta = (it.obj?.restTimer?: 30).toLong() * 1000
            }
        }
    }

    fun getRestsArray(): List<String> {
        return if(rests.isEmpty()) {
            for(seconds in 0 .. 10*60 step 5) {
                rests.add(String.format("%02d:%02d", seconds / 60, seconds % 60))
            }
            rests
        } else {
            rests
        }

    }

    fun onCreateCustomTimer() {
        _state.value = State.AddingCustomTimer
    }

    fun onDefaultTimerClick(title: String) {
        startTimer(
            when (title) {
                "1:00" -> 60 * 1000
                "1:30" -> 90 * 1000
                "2:00" -> 120 * 1000
                "2:30" -> 150 * 1000
                else -> 180 * 1000
            }
        )
    }

    fun onCustomTimerStart(customTime: String) {
        try {
            startTimer("00:$customTime".parseTimeStringToLong())
        } catch (e: IllegalArgumentException) {
            //TODO()
        }
    }

    private fun startTimer(time: Long) {
        remainingTime = time
        _startingTime.value = _startingTime.value ?: time.timeToString()

        timer = object : CountDownTimer(time, 1000) {
            override fun onTick(p0: Long) {
                remainingTime = p0
                _state.value = State.CountDown((p0).timeToString())
            }

            override fun onFinish() {
                _state.value = State.OnTimerFinished
            }

        }.start()
    }

    fun addTime() {
        var newTime = _startingTime.value?.parseTimeStringToLong()

        if(newTime != null) {
            newTime += timerDelta
        }
        _startingTime.value = newTime?.timeToString()

        remainingTime += timerDelta
        timer.cancel()
        startTimer(remainingTime)
    }

    fun removeTime() {
        if (remainingTime >= timerDelta) {
            remainingTime -= timerDelta
            timer.cancel()
            startTimer(remainingTime)
        }

    }

    fun cancelTimer() {
        timer.cancel()
        remainingTime = 0
    }

    sealed interface State {
        object Default : State
        object AddingCustomTimer : State
        object OnTimerFinished : State
        data class CountDown(val timer: String) : State

    }
}