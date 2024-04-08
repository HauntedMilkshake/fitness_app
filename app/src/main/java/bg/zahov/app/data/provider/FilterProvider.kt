package bg.zahov.app.data.provider

import android.util.Log
import bg.zahov.app.data.model.SelectableFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FilterProvider {
    companion object {
        @Volatile
        private var instance: FilterProvider? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: FilterProvider().also { instance = it }
        }
    }

    private val _filters = MutableSharedFlow<List<SelectableFilter>>()
    val filters: Flow<List<SelectableFilter>>
        get() = _filters

    private val selectedFilters = mutableListOf<SelectableFilter>()

    suspend fun addFilter(item: SelectableFilter) {
        Log.d("selected filters before adding", selectedFilters.toString())
        selectedFilters.add(item)
        Log.d("selected filters afte  adding", selectedFilters.toString())
        emitSelectedFilters()
    }

    suspend fun removeFilter(item: SelectableFilter) {
        selectedFilters.remove(item)
        emitSelectedFilters()
    }

    private suspend fun emitSelectedFilters() {
        _filters.emit(selectedFilters)
    }

    fun getCachedFilters() = selectedFilters
}
