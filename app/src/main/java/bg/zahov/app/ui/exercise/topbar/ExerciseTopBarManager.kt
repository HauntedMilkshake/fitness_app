package bg.zahov.app.ui.exercise.topbar

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Manager class for handling the state and actions of the exercise top bar.
 * This is implemented as a singleton to ensure a single instance is used across the app.
 */
class ExerciseTopBarManager {

    companion object {
        @Volatile
        private var instance: ExerciseTopBarManager? = null

        /**
         * Provides a thread-safe singleton instance of [ExerciseTopBarManager].
         *
         * @return The singleton instance of the manager.
         */
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: ExerciseTopBarManager().also { instance = it }
        }
    }

    // Holds the current state of the dialog's visibility.
    private val _openDialog = MutableStateFlow(false)

    /**
     * A [StateFlow] representing whether the dialog is open.
     */
    val openDialog: StateFlow<Boolean>
        get() = _openDialog

    /**
     * Updates the dialog's open state.
     *
     * @param isOpen Whether the dialog should be open.
     */
    suspend fun changeOpenDialog(isOpen: Boolean) {
        _openDialog.emit(isOpen)
    }

    // Holds the current search query.
    private val _search = MutableStateFlow("")

    /**
     * A [StateFlow] representing the current search query.
     */
    val search: StateFlow<String>
        get() = _search

    /**
     * Updates the search query.
     *
     * @param text The new search query.
     */
    suspend fun changeSearch(text: String) {
        _search.emit(text)
    }
}