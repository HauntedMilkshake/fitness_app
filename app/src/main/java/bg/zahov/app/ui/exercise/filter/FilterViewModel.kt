package bg.zahov.app.ui.exercise.filter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.SelectableFilter
import bg.zahov.app.getFilterProvider
import kotlinx.coroutines.launch

class FilterViewModel(application: Application) : AndroidViewModel(application) {
    private val filterManager by lazy {
        application.getFilterProvider()
    }
    private val _bodyPartFilters =
        MutableLiveData(enumValues<BodyPart>().map { SelectableFilter(it.name) })
    val bodyPartFilters: LiveData<List<SelectableFilter>>
        get() = _bodyPartFilters

    private val _categoryFilters =
        MutableLiveData(enumValues<Category>().map { SelectableFilter(it.name) })
    val categoryFilters: LiveData<List<SelectableFilter>>
        get() = _categoryFilters

    init {
        filterManager.getCachedFilters().forEach { item ->
            when {
                BodyPart.fromKey(item.name) != null -> {
                    val new = _bodyPartFilters.value?.toMutableList()
                    new?.find { itemToFind -> itemToFind.name == item.name }?.selected =
                        item.selected
                    _bodyPartFilters.postValue(new)
                }

                Category.fromKey(item.name) != null -> {
                    val new = _categoryFilters.value?.toMutableList()
                    new?.find { itemToFind -> itemToFind.name == item.name }?.selected =
                        item.selected
                    _categoryFilters.postValue(new)
                }
            }

        }
    }

    fun onFilterClicked(item: SelectableFilter) {
        viewModelScope.launch {
            if (item.selected) filterManager.addFilter(item) else filterManager.removeFilter(item)
        }
    }
}