package bg.zahov.app.ui.exercise

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.SelectableFilter
import bg.zahov.app.getWorkoutProvider
import kotlinx.coroutines.launch


class ExerciseViewModel(application: Application) : AndroidViewModel(application) {
    private val repo by lazy {
        application.getWorkoutProvider()
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _userExercises = MutableLiveData<List<Exercise>>()
    val userExercises: LiveData<List<Exercise>>
        get() = _userExercises

    private val _searchFilters = MutableLiveData<List<SelectableFilter>>(listOf())
    val searchFilters: LiveData<List<SelectableFilter>>
        get() = _searchFilters

    private var search: String? = null
    private val allExercises: MutableList<Exercise> = mutableListOf()
    private var selectedFilters: MutableList<SelectableFilter> = mutableListOf()

    init {
        getExercises()
    }

    fun getExercises() {
        _state.value = State.Loading(true)
        viewModelScope.launch {
            try {
                repo.getTemplateExercises().collect {
                    _userExercises.postValue(it)
                    allExercises.apply {
                        clear()
                        addAll(it)
                    }

                    _state.postValue(State.Default)
                }

            } catch (e: CriticalDataNullException) {
                _state.postValue(State.ErrorFetching(e.message, true))
            }
        }
    }

    fun getBodyPartItems(): List<SelectableFilter> {
        val bodyPartFilters = enumValues<BodyPart>().map { SelectableFilter(it.name) }
        bodyPartFilters.forEach {
            it.selected = _searchFilters.value?.any { filter -> filter.name == it.name } == true
        }
        return bodyPartFilters
    }

    fun getCategoryItems(): List<SelectableFilter> {
        val categoryFilters = enumValues<Category>().map { SelectableFilter(it.name) }
        categoryFilters.forEach {
            it.selected = _searchFilters.value?.any { filter -> filter.name == it.name } == true
        }
        return categoryFilters
    }

    fun addFilter(filter: SelectableFilter) {
        selectedFilters = _searchFilters.value?.toMutableList() ?: mutableListOf()
        selectedFilters.add(filter)

        _searchFilters.value = selectedFilters

        searchExercises(search)
    }

    fun removeFilter(filter: SelectableFilter) {
        selectedFilters = _searchFilters.value?.toMutableList() ?: mutableListOf()
        selectedFilters.remove(filter)
        _searchFilters.value = selectedFilters

        searchExercises(search)
    }

    fun searchExercises(name: String?) {
        val newExercises = _userExercises.value?.let {
            when {
                name.isNullOrEmpty() && selectedFilters.isEmpty() -> allExercises
                name.isNullOrEmpty() && selectedFilters.isNotEmpty() -> {
                    allExercises.filter { exercise ->
                        selectedFilters.any { filter ->
                            filter.name == exercise.bodyPart.key || filter.name == exercise.category.key
                        }
                    }
                }

                !name.isNullOrEmpty() && selectedFilters.isEmpty() -> allExercises.filter {
                    it.name.contains(name, true)
                }

                else -> allExercises.filter { exercise ->
                    val nameMatches = name.isNullOrEmpty() || exercise.name.contains(name, true)
                    val categoryMatches =
                        selectedFilters.any { filter -> filter.name == exercise.category.key }
                    val bodyPartMatches =
                        selectedFilters.any { filter -> filter.name == exercise.bodyPart.key }

                    nameMatches && (categoryMatches || bodyPartMatches)
                }
            }
        } ?: emptyList()

        search = name
        _userExercises.value = newExercises

        if (newExercises.isEmpty()) _state.value = State.NoResults(true)
    }

    sealed interface State {
        object Default : State

        data class Loading(val isLoading: Boolean) : State

        data class ErrorFetching(val error: String?, val shutdown: Boolean) : State

        data class NoResults(val areThereResults: Boolean) : State
    }
}