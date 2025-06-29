package bg.zahov.app.ui.exercise.topbar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.interfaces.ExercisesTopBarHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Data class representing the state of the exercise top bar.
 *
 * @property isSearchActive Indicates if the search bar is active.
 * @property searchQuery The current search query.
 */
data class ExerciseTopBarData(
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
)

/**
 * ViewModel responsible for managing the state of the exercise top bar.
 *
 * @property exerciseTopBarManager Manager for handling top bar operations, injected via [Inject].
 */
@HiltViewModel
class TopBarExerciseViewModel @Inject constructor(
    private val exerciseTopBarManager: ExercisesTopBarHandler
) : ViewModel() {

    // Holds the current state of the exercise top bar.
    private val _exerciseData = MutableStateFlow(ExerciseTopBarData())
    val exerciseData: StateFlow<ExerciseTopBarData> = _exerciseData

    init {
        // Collects changes in the search query from the manager and updates the state.
        viewModelScope.launch {
            exerciseTopBarManager.search.collect { newSearchQuery ->
                _exerciseData.update { old -> old.copy(searchQuery = newSearchQuery) }
            }
        }
    }

    /**
     * Updates the search query.
     *
     * @param text The new search query.
     */
    fun changeSearch(text: String) {
        viewModelScope.launch {
            exerciseTopBarManager.changeSearch(text)
        }
    }

    /**
     * Opens the dialog by updating the manager's state.
     */
    fun changeIsDialogOpen() {
        viewModelScope.launch {
            exerciseTopBarManager.changeOpenDialog(true)
        }
    }

    /**
     * Toggles the search bar's active state.
     *
     * @param isOpen Whether the search bar should be active.
     */
    fun changeIsSearchActive(isOpen: Boolean) {
        _exerciseData.update { old -> old.copy(isSearchActive = isOpen) }
    }
}