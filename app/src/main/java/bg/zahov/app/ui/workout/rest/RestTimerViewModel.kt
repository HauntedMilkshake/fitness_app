package bg.zahov.app.ui.workout.rest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.RestState
import bg.zahov.app.getRestTimerProvider
import bg.zahov.app.getSettingsProvider
import bg.zahov.app.util.parseTimeStringToLong
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class RestTimerViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsProvider by lazy {
        application.getSettingsProvider()
    }

    private val restManager by lazy {
        application.getRestTimerProvider()
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

    private val rests: MutableList<String> = mutableListOf()

    private var timerDelta: Long = 0

    init {
        viewModelScope.launch {
            launch {
                settingsProvider.getSettings().collect {
                    _increment.postValue("${(it.obj?.restTimer ?: 30)} s")
                    timerDelta = (it.obj?.restTimer ?: 30).toLong() * 1000
                }
            }
            launch {
                restManager.restTimer.collect {
                    if (!(it.elapsedTime.isNullOrEmpty()) && !(it.fullRest.isNullOrEmpty())) {
                        _state.postValue(State.CountDown(it.elapsedTime!!))
                        _startingTime.postValue(it.fullRest!!)
                    }
                }
            }
            launch {
                restManager.restState.collect {
                    _state.postValue(
                        when (it) {
                            RestState.Active -> State.CountDown("00:00")
                            RestState.Finished -> State.OnTimerFinished
                            RestState.Default -> State.Default
                        }
                    )
                }
            }
        }
    }

    fun getRestsArray(): List<String> {
        return if (rests.isEmpty()) {
            for (seconds in 0..10 * 60 step 5) {
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
            //:)
        }
    }

    private fun startTimer(time: Long) {
        viewModelScope.launch {
            restManager.startRest(time)
        }
    }

    fun addTime() {
        viewModelScope.launch {
            restManager.addTime(timerDelta)
        }
    }

    fun removeTime() {
        viewModelScope.launch {
            restManager.removeTime(timerDelta)
        }
    }

    fun cancelTimer() {
        viewModelScope.launch {
            restManager.stopRest()
        }
    }

    sealed interface State {
        object Default : State
        object AddingCustomTimer : State
        object OnTimerFinished : State
        data class CountDown(val timer: String) : State

    }
}