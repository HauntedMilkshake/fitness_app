package bg.zahov.app.data.provider

import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.Filter
import bg.zahov.app.data.model.FilterItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class FilterProvider @Inject constructor() {

    private val _filters = MutableSharedFlow<List<FilterItem>>()
    val filters: SharedFlow<List<FilterItem>> = _filters

    private var selected: List<FilterItem> = listOf()

    suspend fun updateFilter(item: FilterItem) {
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
        listOf(FilterItem(filter = Filter.BodyPartFilter(bodyPart)))
    } + enumValues<Category>().flatMap { category ->
        listOf(FilterItem(filter = Filter.CategoryFilter(category)))
    }
}
