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

class FilterViewModel(private val filterManager: FilterProvider = Inject.filterProvider) :
    ViewModel() {
    private val _uiState = MutableStateFlow(UiState(filterManager.getAllFilters()))
    val uiState: StateFlow<UiState> = _uiState

    data class UiState(val list: List<FilterWrapper>)

    init {
        viewModelScope.launch {
            filterManager.reset()
            filterManager.filters.collectLatest { item ->
                toggleFilter(item)
            }
        }
    }


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

    fun onFilterClicked(item: FilterWrapper) {
        viewModelScope.launch {
            filterManager.updateFilter(item)
        }
    }
}