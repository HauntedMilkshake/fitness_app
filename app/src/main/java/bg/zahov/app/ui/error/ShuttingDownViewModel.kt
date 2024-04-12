package bg.zahov.app.ui.error

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.getServiceErrorProvider
import kotlinx.coroutines.launch

class ShuttingDownViewModel(application: Application) : AndroidViewModel(application) {
    private val serviceErrorHandler by lazy {
        application.getServiceErrorProvider()
    }
    private val _state = MutableLiveData<State>(State.CountDown(currentTime = "5"))
    val state: LiveData<State>
        get() = _state
    private var countDownTimer: CountDownTimer? = null

    init {
        startCountdown()
    }

    private fun startCountdown() {
        countDownTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _state.value = State.CountDown((millisUntilFinished / 1000).toString())
            }

            override fun onFinish() {
                viewModelScope.launch {
                    serviceErrorHandler.stopApplication()
                }
            }
        }.start()
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }

    sealed interface State {
        data class CountDown(val currentTime: String) : State
    }
}