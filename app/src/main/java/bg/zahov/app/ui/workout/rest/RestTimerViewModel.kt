package bg.zahov.app.ui.workout.rest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.model.RestState
import bg.zahov.app.data.model.ToastManager
import bg.zahov.app.data.provider.RestTimerProvider
import bg.zahov.app.util.parseTimeStringToLong
import bg.zahov.fitness.app.R
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
     *
     * @see [RestInfo]
     */
    data class Default(
        val rests: List<String> = RestInfo().rests,
        val increment: String = RestInfo().increment,
        val isCustomTimer: Boolean = false,
        val pickerValue: String = rests.first(),
    ) : Rest

    /**
     * Represents an active resting state with a timer.
     *
     * @property rests An array of rest durations, similar to the [Default] state.
     * @property increment A string representing the increment value for increasing rest duration.
     * @property startingTime A string indicating the starting time of the rest period.
     * @property timer A float value representing the progress of the timer, where `0f` represents no progress.
     *
     * @see [RestInfo]
     */
    data class Resting(
        val rests: List<String> = RestInfo().rests,
        val increment: String = "30",
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
 * Sharing rests and increment
 */
data class RestInfo(
    val rests: List<String> = listOf(
        "1:00",
        "1:30",
        "2:00",
        "2:30"
    ),
    val increment: String = "30",
)

/**
 * ViewModel responsible for managing the state and logic of a rest timer in the application.
 *
 * This ViewModel interacts with the application's settings and manages the rest timer's state
 * using the provided dependencies.
 *
 * @property restManager An instance of [RestTimerProvider] responsible for controlling the behavior of the rest timer,
 * such as starting, pausing, and tracking progress.
 *
 **/
class RestTimerViewModel(
    private val restManager: RestTimerProvider = Inject.restTimerProvider,
    private val toastManager: ToastManager = ToastManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<Rest>(Rest.Default())
    val uiState: StateFlow<Rest> = _uiState

    private var timerDelta: Long = 30000

    init {
        observeRestTimer()
        observeRestState()
    }

    /**
     * Observes the rest timer updates from the `restManager` and updates the state values
     * based on elapsed time and full rest duration.
     *
     * This function ensures the timer's progress is calculated as a ratio of elapsed time
     * to full rest time and coerces it within the range [0, 1].
     * @see parseTimeStringToLong
     * @see updateStateValues
     */
    private fun observeRestTimer() {
        viewModelScope.launch {
            restManager.restTimer.collect {
                val elapsedTime = it.elapsedTime ?: ""
                val fullRest = it.fullRest ?: ""
                if (elapsedTime.isNotEmpty() && fullRest.isNotEmpty()) {
                    updateStateValues(
                        startingTime = it.fullRest,
                        timer = (elapsedTime.parseTimeStringToLong()
                            .toFloat() / fullRest.parseTimeStringToLong()
                            .toFloat()).coerceIn(0f, 1f),
                    )
                }
            }
        }
    }

    /**
     * Observes the rest state updates from the `restManager` and updates the UI state accordingly.
     *
     * Depending on the current rest state:
     * - Sets the UI state to `Resting` when active.
     * - Sets the UI state to `Finished` and resets the state when finished.
     * - Sets the UI state to `Default` for the default state.
     */
    private fun observeRestState() {
        viewModelScope.launch {
            restManager.restState.collect {
                when (it) {
                    RestState.Active -> _uiState.value = Rest.Resting()
                    RestState.Finished -> {
                        _uiState.value = Rest.Finished
                        resetState()
                    }

                    RestState.Default -> _uiState.value = Rest.Default()
                }
            }
        }
    }

    private fun resetState() {
        _uiState.value = Rest.Default()
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
        rests: List<String>? = null,
        increment: String? = null,
        startingTime: String? = null,
        timer: Float? = null,
        numberPicker: String? = null,
    ) {
        _uiState.value = when (val currentState = _uiState.value) {
            is Rest.Default -> {
                currentState.copy(
                    rests = rests ?: currentState.rests,
                    increment = increment ?: currentState.increment,
                    pickerValue = numberPicker ?: currentState.pickerValue
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
            startTimer("00:${(_uiState.value as Rest.Default).pickerValue}".parseTimeStringToLong())
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

    /**
     * Updates the number picker value with a new one from the number picker on each scroll
     * @param newValue - the new custom rest chosen
     */
    fun updateNumberPicker(newValue: String) {
        updateStateValues(numberPicker = newValue)
    }
}