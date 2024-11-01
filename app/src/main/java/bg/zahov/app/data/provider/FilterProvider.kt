package bg.zahov.app.data.provider

import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.Filter
import bg.zahov.app.data.model.FilterWrapper
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
    val filters: SharedFlow<List<FilterWrapper>> = _filters

    private var selected: List<FilterWrapper> = listOf()

    suspend fun updateFilter(item: FilterWrapper) {
        selected = if (selected.any { it.filter == item.filter }) {
            selected.filter { it.filter != item.filter }
        } else {
            selected + item
        }

        _filters.emit(selected)
    }

    fun reset() {
        selected = listOf()
    }

    fun getAllFilters() = enumValues<BodyPart>().flatMap { bodyPart ->
        listOf(FilterWrapper(filter = Filter.BodyPartFilter(bodyPart)))
    } + enumValues<Category>().flatMap { category ->
        listOf(FilterWrapper(filter = Filter.CategoryFilter(category)))
    }
}
