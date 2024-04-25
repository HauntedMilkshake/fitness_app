package bg.zahov.app.ui.exercise.filter

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.getFilterProvider
import bg.zahov.fitness.app.R
import kotlinx.coroutines.launch

class FilterViewModel(application: Application) : AndroidViewModel(application) {
    private val filterManager by lazy {
        application.getFilterProvider()
    }
    private val _bodyPartFilters =
        MutableLiveData(enumValues<BodyPart>().map { FilterWrapper(it.name) })
    val bodyPartFilters: LiveData<List<FilterWrapper>>
        get() = _bodyPartFilters

    private val _categoryFilters =
        MutableLiveData(enumValues<Category>().map { FilterWrapper(it.name) })
    val categoryFilters: LiveData<List<FilterWrapper>>
        get() = _categoryFilters

    init {
        viewModelScope.launch {
            filterManager.filters.collect { item ->
                item.forEach {
                    when {
                        BodyPart.fromKey(it.name) != null -> {
                            toggleFilter(it, _bodyPartFilters)
                        }

                        Category.fromKey(it.name) != null -> {
                            toggleFilter(it, _categoryFilters)
                        }
                    }
                }
            }
        }
    }

    private fun toggleFilter(filter: FilterWrapper, filters: MutableLiveData<List<FilterWrapper>>) {
        val new = filters.value.orEmpty()
        new.find { itemToFind -> itemToFind.name == filter.name }?.let {
            Log.d("toggling item", it.toString())
            it.backgroundResource =
                if (it.backgroundResource == R.drawable.filter_item_clicked) R.drawable.filter_item_unclicked else R.drawable.filter_item_clicked
            Log.d("toggling item", it.toString())
        }
        filters.postValue(new)
    }

    fun onFilterClicked(item: FilterWrapper) {
        viewModelScope.launch {
            if (item.backgroundResource == R.drawable.filter_item_clicked) {
                filterManager.removeFilter(item)
            } else {
                filterManager.addFilter(item)
            }
        }
    }
}