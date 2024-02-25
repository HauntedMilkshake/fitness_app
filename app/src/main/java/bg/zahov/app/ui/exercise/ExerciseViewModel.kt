package bg.zahov.app.ui.exercise

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.SelectableExercise
import bg.zahov.app.data.model.SelectableFilter
import bg.zahov.app.getAddExerciseToWorkoutProvider
import bg.zahov.app.getFilterProvider
import bg.zahov.app.getReplaceableExerciseProvider
import bg.zahov.app.getSelectableExerciseProvider
import bg.zahov.app.getWorkoutProvider
import kotlinx.coroutines.launch


class ExerciseViewModel(application: Application) : AndroidViewModel(application) {
    private val repo by lazy {
        application.getWorkoutProvider()
    }

    private val selectableExerciseProvider by lazy {
        application.getSelectableExerciseProvider()
    }

    private val replaceableExerciseProvider by lazy {
        application.getReplaceableExerciseProvider()
    }

    private val addExerciseToWorkoutProvider by lazy {
        application.getAddExerciseToWorkoutProvider()
    }

    private val filterProvider by lazy {
        application.getFilterProvider()
    }
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _userExercises = MutableLiveData<List<SelectableExercise>>()
    val userExercises: LiveData<List<SelectableExercise>>
        get() = _userExercises

    private val _searchFilters = MutableLiveData<List<SelectableFilter>>(listOf())
    val searchFilters: LiveData<List<SelectableFilter>>
        get() = _searchFilters

    var replaceable = false
    var selectable = false
    var addable = false
    private var search: String? = null
    private val allExercises: MutableList<Exercise> = mutableListOf()
    private var currentlySelectedExerciseToReplace: Int? = null

    init {
        getExercises()
        getFilters()
    }

    fun onSelectableExerciseClicked(exercise: SelectableExercise, position: Int) {
        val captured = _userExercises.value.orEmpty()
        if (replaceable) {
            currentlySelectedExerciseToReplace?.let {
                if (captured[it] != exercise) {
                    captured[it].isSelected = false
                }
            }
        }
        captured[position].let {
            it.isSelected = !it.isSelected
            currentlySelectedExerciseToReplace =
                if (replaceable && it.isSelected) captured.indexOf(it) else null
        }
        _userExercises.value = captured
    }

    fun onConfirm() {
        replaceable = false
        selectable = false
        addable = false

        val selectedExercises = _userExercises.value.orEmpty()
        selectedExercises.forEach {
            if (it.isSelected) {
                it.isSelected = false
            }
        }
        _userExercises.value = selectedExercises
    }

    fun confirmSelectedExercises() {
        when {
            replaceable -> {
                _userExercises.value?.find { it.isSelected }?.let {
                    replaceableExerciseProvider.updateExerciseToReplace(it)
                }
            }

            else -> {
                val selectedExercises = mutableListOf<SelectableExercise>()
                _userExercises.value?.forEach {
                    if (it.isSelected) {
                        selectedExercises.add(it)
                    }
                }

                if (selectedExercises.isNotEmpty()) {
                    Log.d("adding to providers", "adding to providers")
                    if (addable) {
                        Log.d("ADDABLE", "ADDABLE")
                        addExerciseToWorkoutProvider.addExercises(selectedExercises)
                    }
                    if (selectable) selectableExerciseProvider.addExercises(selectedExercises)
                }
            }
        }

        onConfirm()
    }

    fun getExercises() {
        viewModelScope.launch {
            _state.postValue(State.Loading(true))
            try {
                repo.getTemplateExercises().collect {
                    _userExercises.postValue(it.map { exercise ->
                        SelectableExercise(
                            exercise,
                            false
                        )
                    })
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

    private fun getFilters() {
        viewModelScope.launch {
            filterProvider.filters.collect {
                _searchFilters.postValue(it)
                searchExercises(search)
            }
        }
    }

    fun searchExercises(name: String?) {
        Log.d("search", "search")
        val selectedFilters = _searchFilters.value ?: mutableListOf()
        val newExercises = _userExercises.value?.let {
            //TODO(Test it vs allExercises)
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
        _userExercises.value = newExercises.map { SelectableExercise(it, false) }

        if (newExercises.isEmpty()) _state.value = State.NoResults(true)
    }

    fun removeFilter(filter: SelectableFilter) {
//        selectedFilters = searchFilters.value.orEmpty().toMutableList()
//        selectedFilters.remove(filter)
//        _searchFilters.value = selectedFilters
        viewModelScope.launch {
            filterProvider.removeFilter(filter)
        }
        searchExercises(search)
    }

    sealed interface State {
        object Default : State

        data class Loading(val isLoading: Boolean) : State

        data class ErrorFetching(val error: String?, val shutdown: Boolean) : State

        data class NoResults(val areThereResults: Boolean) : State
    }
}