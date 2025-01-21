package bg.zahov.app.ui.workout.rest

import android.util.Log
import bg.zahov.fitness.app.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.interfaces.SettingsProvider
import bg.zahov.app.data.model.RestState
import bg.zahov.app.data.model.ToastManager
import bg.zahov.app.data.provider.RestTimerProvider
import bg.zahov.app.util.parseTimeStringToLong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

/**
 * A sealed interface that represents the different states of a rest timer.
 *
 * This interface has three states:
 * - [Default]: Represents the default rest configuration.
 * - [Resting]: Represents an ongoing rest period with a timer.
 * - [Finished]: Represents a state where the rest period has completed.
 */
sealed interface Rest {

    /**
     * Represents the default configuration of rest intervals.
     *
     * @property rests An array of predefined rest durations, expressed as strings. Defaults to ["1:00", "1:30", "2:00", "2:30"].
     * @property increment A string representing the increment value for increasing rest duration (if applicable). Default is an empty string.
     * @property isCustomTimer A boolean flag indicating whether the user is using a custom timer. Default is `false`.
     */
    data class Default(
        val rests: Array<Int> = arrayOf(
            R.string.minute,
            R.string.minute_and_half,
            R.string.two_minutes,
            R.string.two_minutes_and_half
        ),
        val increment: String = "",
        val isCustomTimer: Boolean = false,
    ) :
        Rest

    /**
     * Represents an active resting state with a timer.
     *
     * @property rests An array of rest durations, similar to the [Default] state.
     * @property increment A string representing the increment value for increasing rest duration.
     * @property startingTime A string indicating the starting time of the rest period.
     * @property timer A float value representing the progress of the timer, where `0f` represents no progress.
     */
    data class Resting(
        val rests: Array<Int> = arrayOf(
            R.string.minute,
            R.string.minute_and_half,
            R.string.two_minutes,
            R.string.two_minutes
        ),
        val increment: String = "",
        val startingTime: String = "",
        val remaining: String = "",
        val timer: Float = 0f,
    ) : Rest

    /**
     * Represents a state where the rest period has finished.
     */
    data object Finished : Rest
}

/**
 * ViewModel responsible for managing the state and logic of a rest timer in the application.
 *
 * This ViewModel interacts with the application's settings and manages the rest timer's state
 * using the provided dependencies.
 *
 * @property settingsProvider An instance of [SettingsProvider] used to retrieve and manage user-specific settings,
 * such as timer configurations or preferences.
 * @property restManager An instance of [RestTimerProvider] responsible for controlling the behavior of the rest timer,
 * such as starting, pausing, and tracking progress.
 *
 **/
class RestTimerViewModel(
    private val settingsProvider: SettingsProvider = Inject.settingsProvider,
    private val restManager: RestTimerProvider = Inject.restTimerProvider,
    private val toastManager: ToastManager = ToastManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<Rest>(Rest.Default())
    val uiState: StateFlow<Rest> = _uiState

    private var timerDelta: Long = 0
    private var lastCustomRest: String = "1:00"

    init {
        Log.d("init", "init")
        viewModelScope.launch {
            launch { /* TODO() */}
            launch {
                restManager.restTimer.collect {
                    if (!(it.elapsedTime.isNullOrEmpty()) && !(it.fullRest.isNullOrEmpty())) {
                        updateStateValues(
                            startingTime = it.fullRest,
                            timer = (it.elapsedTime!!.parseTimeStringToLong()
                                .toFloat() / it.fullRest!!.parseTimeStringToLong()
                                .toFloat()).coerceIn(0f, 1f),
                            increment = (timerDelta / 1000).toString()
                        )
                    }
                }
            }
            launch {
                restManager.restState.collect {
                    when (it) {
                        RestState.Active -> _uiState.value = Rest.Resting()
                        RestState.Finished -> _uiState.value = Rest.Finished
                        RestState.Default -> _uiState.value = Rest.Default()
                    }
                }
            }
        }
    }

    /**
     * Updates the current UI state with the provided values, while preserving existing values
     * if no new ones are provided.
     *
     * This function updates the `_uiState` based on the current state, handling both [Rest.Default]
     * and [Rest.Resting] states. It selectively updates properties like `rests`, `increment`,
     * `startingTime`, and `timer` while leaving other values unchanged if `null` is passed.
     *
     * @param rests An optional array of rest durations to update. If `null`, the current value is retained.
     * @param increment An optional string representing the increment value. If `null`, the current value is retained.
     * @param startingTime An optional string representing the starting time of the timer (only applicable to [Rest.Resting]).
     *                     If `null`, the current value is retained.
     * @param timer An optional float representing the timer progress (only applicable to [Rest.Resting]).
     *              If `null`, the current value is retained.
     **/
    private fun updateStateValues(
        rests: Array<Int>? = null,
        increment: String? = null,
        startingTime: String? = null,
        timer: Float? = null,
    ) {
        _uiState.value = when (val currentState = _uiState.value) {
            is Rest.Default -> {
                currentState.copy(
                    rests = rests ?: currentState.rests,
                    increment = increment ?: currentState.increment
                )
            }

            is Rest.Resting -> {
                currentState.copy(
                    rests = rests ?: currentState.rests,
                    increment = increment ?: currentState.increment,
                    startingTime = startingTime ?: currentState.startingTime,
                    timer = timer ?: currentState.timer
                )
            }

            else -> currentState // No-op for other cases
        }
    }

    /**
     * Sets the last custom rest timer.
     *
     * @param timer The custom timer value in string format.
     */
    fun selectCustomTimer(timer: String) {
        lastCustomRest = timer
    }

    /**
     * Starts a default timer based on the provided title.
     *
     * @param title The label for the timer (e.g., "1:00", "1:30", "2:00", "2:30").
     *              If the title is not recognized, a default time of 3 minutes is used.
     */
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

    /**
     * Updates the UI state to enable custom timer creation.
     */
    fun onCreateCustomTimerClick() {
        _uiState.value = (_uiState.value as Rest.Default).copy(isCustomTimer = true)
    }

    /**
     * Starts a custom timer based on the last selected custom time.
     *
     * Attempts to parse the custom time string and start the timer. If parsing fails,
     * the exception is caught, and a new toast can be triggered.
     */
    fun onCustomTimerStart() {
        try {
            startTimer("00:$lastCustomRest".parseTimeStringToLong())
        } catch (_: IllegalArgumentException) {
            toastManager.showToast(R.string.invalid_time_format)
        }
    }

    /**
     * Starts the timer with the specified duration.
     *
     * @param time The timer duration in milliseconds.
     */
    private fun startTimer(time: Long) {
        viewModelScope.launch {
            restManager.startRest(time)
        }
    }

    /**
     * Adds a predefined amount of time to the active timer.
     */
    fun addTime() {
        viewModelScope.launch {
            restManager.addTime(timerDelta)
        }
    }

    /**
     * Removes a predefined amount of time from the active timer.
     */
    fun removeTime() {
        viewModelScope.launch {
            restManager.removeTime(timerDelta)
        }
    }

    /**
     * Cancels the active timer and stops the rest session.
     */
    fun cancelTimer() {
        viewModelScope.launch {
            restManager.stopRest()
        }
    }
}