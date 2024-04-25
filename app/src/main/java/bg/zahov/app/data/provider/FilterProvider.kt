package bg.zahov.app.data.provider

import bg.zahov.app.ui.exercise.filter.FilterWrapper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class FilterProvider {
    companion object {
        @Volatile
        private var instance: FilterProvider? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: FilterProvider().also { instance = it }
        }
    }

    private val _filters = MutableSharedFlow<List<FilterWrapper>>()
    val filters: SharedFlow<List<FilterWrapper>>
        get() = _filters

    private val selectedFilters = mutableListOf<FilterWrapper>()

    suspend fun addFilter(item: FilterWrapper) {
        selectedFilters.add(item)
        emitSelectedFilters()
    }

    suspend fun removeFilter(item: FilterWrapper) {
        selectedFilters.remove(item)
        emitSelectedFilters()
    }

    private suspend fun emitSelectedFilters() {
        _filters.emit(selectedFilters)

    }
}
