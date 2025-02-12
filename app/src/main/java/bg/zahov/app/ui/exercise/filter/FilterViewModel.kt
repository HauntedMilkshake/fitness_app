package bg.zahov.app.ui.exercise.filter

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.FilterItem
import bg.zahov.app.data.provider.FilterProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing and updating filter selections for exercises.
 *
 * `FilterViewModel` works with a [FilterProvider] to retrieve, update, and manage the state of filters,
 * reflecting the currently selected filters in the UI through [FilterData].
 *
 * @property filterManager The provider that supplies filter data and manages filter states.
 */
@HiltViewModel
class FilterViewModel @Inject constructor(private val filterManager: FilterProvider) :
    ViewModel() {

    // Backing property for the UI state flow
    private val _filterData = MutableStateFlow(FilterData(filterManager.getAllFilters()))

    /**
     * [StateFlow] representing the UI state, containing a list of available filters.
     */
    val filterData: StateFlow<FilterData> = _filterData

    /**
     * Data class representing the UI state of the filter selection.
     *
     * @property list A list of [FilterItem] items reflecting available filters and their selected states.
     */
    data class FilterData(val list: List<FilterItem>)

    /**
     * Initializes the ViewModel, resetting filters and observing changes in the filter data.
     *
     * This init block resets all filters to their default state on initialization and then starts
     * collecting from [filterManager.filters] to observe changes and update the selected filters
     * accordingly using [toggleFilter].
     */
    init {
        Log.d("updating filter", filterManager.toString())
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
     * @param new A list of [FilterItem] items representing the newly selected filters.
     */
    private fun toggleFilter(new: List<FilterItem>) {
        _filterData.update { old ->
            old.copy(
                list = old.list.map { item ->
                    FilterItem(item.filter, new.any { filter -> filter.name == item.name })
                }
            )
        }
    }

    /**
     * Handles filter selection clicks and updates the filter state accordingly.
     *
     * When a filter item is clicked, this function updates the filter's state
     * in [filterManager] asynchronously.
     *
     * @param item The [FilterItem] representing the clicked filter.
     */
    fun onFilterClicked(item: FilterItem) {
        viewModelScope.launch {
            filterManager.updateFilter(item)
        }
    }
}
