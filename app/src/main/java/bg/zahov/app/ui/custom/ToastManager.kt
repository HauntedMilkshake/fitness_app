package bg.zahov.app.ui.custom

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Data class representing a toast message, storing a string resource ID.
 *
 * @property messageResId The string resource ID of the message to be displayed.
 */
data class Message(@StringRes val messageResId: Int)

/**
 * Singleton object responsible for managing toast messages within the app.
 *
 * `ToastManager` allows the app to display toast messages with a single point of control.
 * The messages are managed using a [StateFlow] that emits a [Message] instance whenever a
 * new message is added.
 */
object ToastManager {

    // Backing property for the toast message flow
    private val _messages: MutableStateFlow<Message?> = MutableStateFlow(null)

    /**
     * [StateFlow] of the current toast message, or `null` if no message is active.
     * Observers can collect from this flow to react to new toast messages.
     */
    val messages: StateFlow<Message?> get() = _messages.asStateFlow()

    /**
     * Displays a toast message with the specified string resource ID.
     *
     * This method updates the [messages] flow with a new [Message] only if the provided
     * [messageResId] differs from the current one, ensuring that duplicate messages are not displayed.
     *
     * @param messageResId The string resource ID of the message to be displayed in the toast.
     */
    fun showToastWithoutRepeat(@StringRes messageResId: Int) {
        _messages.update { current ->
            if (current?.messageResId != messageResId) Message(messageResId) else current
        }
    }
    /**
     * Displays a toast message with the specified string resource ID.
     *
     * This method updates the [messages] flow with a new [Message]
     *
     * @param messageResId The string resource ID of the message to be displayed in the toast.
     */
    fun showToast(@StringRes messageResId: Int) {
        _messages.update { current ->
            if (current?.messageResId != messageResId) Message(messageResId) else current
        }
    }


    /**
     * Clears the current toast message, setting the [messages] flow to `null`.
     *
     * This function should be called after the toast message has been displayed to reset
     * the state and allow new messages to be shown.
     */
    fun clearToast() {
        _messages.update { null }
    }
}
