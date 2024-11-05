package bg.zahov.app.ui.exercise.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.model.FilterWrapper
import bg.zahov.app.data.provider.FilterProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing and updating filter selections for exercises.
 *
 * `FilterViewModel` works with a [FilterProvider] to retrieve, update, and manage the state of filters,
 * reflecting the currently selected filters in the UI through [UiState].
 *
 * @property filterManager The provider that supplies filter data and manages filter states.
 */
class FilterViewModel(private val filterManager: FilterProvider = Inject.filterProvider) : ViewModel() {

    // Backing property for the UI state flow
    private val _uiState = MutableStateFlow(UiState(filterManager.getAllFilters()))

    /**
     * [StateFlow] representing the UI state, containing a list of available filters.
     */
    val uiState: StateFlow<UiState> = _uiState

    /**
     * Data class representing the UI state of the filter selection.
     *
     * @property list A list of [FilterWrapper] items reflecting available filters and their selected states.
     */
    data class UiState(val list: List<FilterWrapper>)

    /**
     * Initializes the ViewModel, resetting filters and observing changes in the filter data.
     *
     * This init block resets all filters to their default state on initialization and then starts
     * collecting from [filterManager.filters] to observe changes and update the selected filters
     * accordingly using [toggleFilter].
     */
    init {
        viewModelScope.launch {
            filterManager.reset()
            filterManager.filters.collectLatest { item ->
                toggleFilter(item)
            }
        }
    }

    /**
     * Updates the current UI state with the latest selected filters.
     *
     * This function first clears the selected state of all filters in the current list, then sets
     * the selected state based on the provided [new] list of filters.
     *
     * @param new A list of [FilterWrapper] items representing the newly selected filters.
     */
    private fun toggleFilter(new: List<FilterWrapper>) {
        _uiState.update { old ->
            old.copy(list = old.copy().list.apply {
                forEach { it.selected = false }
                new.forEach { filter ->
                    find { itemToFind -> itemToFind.name == filter.name }?.let {
                        it.selected = true
                    }
                }
            })
        }
    }

    /**
     * Handles filter selection clicks and updates the filter state accordingly.
     *
     * When a filter item is clicked, this function updates the filter's state
     * in [filterManager] asynchronously.
     *
     * @param item The [FilterWrapper] representing the clicked filter.
     */
    fun onFilterClicked(item: FilterWrapper) {
        viewModelScope.launch {
            filterManager.updateFilter(item)
        }
    }
}
