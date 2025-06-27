package bg.zahov.app.data.provider

import bg.zahov.app.data.interfaces.ExercisesTopBarHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Manager class for handling the state and actions of the exercise top bar.
 * This is implemented as a singleton to ensure a single instance is used across the app.
 */
class ExercisesTopBarManager @Inject constructor() : ExercisesTopBarHandler {
    // Holds the current state of the dialog's visibility.
    private val _openDialog = MutableStateFlow(false)

    /**
     * A [StateFlow] representing whether the dialog is open.
     */
    override val openDialog: StateFlow<Boolean>
        get() = _openDialog

    /**
     * Updates the dialog's open state.
     *
     * @param isOpen Whether the dialog should be open.
     */
    override suspend fun changeOpenDialog(isOpen: Boolean) {
        _openDialog.emit(isOpen)
    }

    // Holds the current search query.
    private val _search = MutableStateFlow("")

    /**
     * A [StateFlow] representing the current search query.
     */
    override val search: StateFlow<String>
        get() = _search

    /**
     * Updates the search query.
     *
     * @param text The new search query.
     */
    override suspend fun changeSearch(text: String) {
        _search.emit(text)
    }
}