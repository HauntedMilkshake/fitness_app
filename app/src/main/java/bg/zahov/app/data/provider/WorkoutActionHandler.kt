package bg.zahov.app.data.provider

import bg.zahov.app.data.interfaces.WorkoutActionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * A singleton class responsible for handling workout-related actions, such as signaling when a workout
 * should finish. It provides a shared instance to ensure consistent state management across components.
 *
 * The class exposes a `shouldFinish` state through a [MutableStateFlow] to indicate when the workout
 * should be finished. It also provides methods to attempt finishing the workout and to complete that attempt.
 *
 */
class WorkoutActionHandler : WorkoutActionHandler {
    companion object {
        /**
         * Holds the singleton instance of [WorkoutActionHandler].
         */
        private var instance: bg.zahov.app.data.provider.WorkoutActionHandler? = null

        /**
         * Provides the singleton instance of [WorkoutActionHandler]. If the instance is not yet created,
         * a new instance will be created and returned.
         *
         * @return The singleton instance of [WorkoutActionHandler].
         */
        fun getInstance() = instance ?: WorkoutActionHandler().also { instance = it }
    }


    private val _shouldFinish = MutableStateFlow(false)

    /**
     * A [MutableStateFlow] indicating whether a finish attempt has been signaled.
     * The value is `true` when a finish attempt is in progress, and `false` otherwise.
     */
    override val shouldFinish: StateFlow<Boolean> = _shouldFinish

    /**
     * Signals that a finish attempt for the workout has been initiated by setting
     * [shouldFinish] to `true`.
     */
    override fun tryToFinish() {
        _shouldFinish.value = true
    }

    /**
     * Completes the finish attempt for the workout by resetting [shouldFinish] to `false`.
     */
    override fun completeFinishAttempt() {
        _shouldFinish.value = false
    }
}