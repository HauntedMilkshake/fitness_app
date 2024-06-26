package bg.zahov.app.ui.exercise

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.getAddExerciseToWorkoutProvider
import bg.zahov.app.getFilterProvider
import bg.zahov.app.getReplaceableExerciseProvider
import bg.zahov.app.getSelectableExerciseProvider
import bg.zahov.app.getServiceErrorProvider
import bg.zahov.app.getWorkoutProvider
import bg.zahov.app.ui.exercise.filter.FilterWrapper
import bg.zahov.app.util.toExerciseAdapterWrapper
import bg.zahov.fitness.app.R
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

    private val serviceError by lazy {
        application.getServiceErrorProvider()
    }
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _userExercises = MutableLiveData<List<ExerciseAdapterWrapper>>()
    val userExercises: LiveData<List<ExerciseAdapterWrapper>>
        get() = _userExercises

    private val _searchFilters = MutableLiveData<List<FilterWrapper>>(listOf())
    val searchFilters: LiveData<List<FilterWrapper>>
        get() = _searchFilters

    var replaceable = false
    var selectable = false
    var addable = false
    private var search: String? = null
    private var exerciseTemplates = listOf<Exercise>()
    private val allExercises: MutableList<ExerciseAdapterWrapper> = mutableListOf()
    private var currentlySelectedExerciseToReplace: Int? = null
    private var currentSearchFilters = listOf<FilterWrapper>()

    init {
        viewModelScope.launch {
            launch {
                _state.postValue(State.Loading(View.VISIBLE))
                try {
                    repo.getTemplateExercises().collect {
                        exerciseTemplates = it
                        val templateExercises =
                            it.map { exercise -> exercise.toExerciseAdapterWrapper() }

                        _userExercises.postValue(templateExercises)

                        allExercises.apply {
                            clear()
                            addAll(templateExercises)
                        }

                        _state.postValue(State.Default)
                    }
                } catch (e: CriticalDataNullException) {
                    serviceError.stopApplication()
                }
            }
            launch {
                filterProvider.filters.collect {
                    currentSearchFilters = it
                    _searchFilters.postValue(it)
                    searchExercises(search)
                }
            }
        }
    }

    fun onExerciseClicked(position: Int) {
        val captured = _userExercises.value.orEmpty().toMutableList()

        if (replaceable) {
            currentlySelectedExerciseToReplace?.let { index ->
                captured[index].backgroundResource = R.color.background
            }
        }

        captured[position].backgroundResource =
            if (captured[position].backgroundResource == R.color.selected) {
                R.color.background
            } else {
                R.color.selected
            }

        currentlySelectedExerciseToReplace = if (replaceable) position else null

        _userExercises.value = captured
    }

    fun setClickedExercise(name: String) {
        viewModelScope.launch {
            exerciseTemplates.find { it.name == name }?.let {
                repo.setClickedTemplateExercise(it)
            }
        }
    }

    fun onConfirm() {
        replaceable = false
        selectable = false
        addable = false

        val selectedExercises = _userExercises.value.orEmpty()
        selectedExercises.forEach {
            if (it.backgroundResource == R.color.selected) {
                it.backgroundResource = R.color.background
            }
        }
        _userExercises.value = selectedExercises
        resetSelectedExercises()
    }

    fun confirmSelectedExercises() {
        when {
            replaceable -> {
                _userExercises.value?.find { it.backgroundResource == R.color.selected }?.let {
                    exerciseTemplates.find { template -> template.name == it.name }?.let {
                        replaceableExerciseProvider.updateExerciseToReplace(it)
                    }
                }
            }

            else -> {
                val selectedExercises = mutableListOf<Exercise>()
                _userExercises.value?.forEach {
                    if (it.backgroundResource == R.color.selected) {
                        exerciseTemplates.find { exercise -> exercise.name == it.name }
                            ?.let { found ->
                                selectedExercises.add(found)
                            }
                    }
                }

                if (selectedExercises.isNotEmpty()) {
                    if (addable) {
                        addExerciseToWorkoutProvider.addExercises(selectedExercises)
                    }
                    if (selectable) selectableExerciseProvider.addExercises(selectedExercises)
                }
            }
        }
        onConfirm()
    }


    fun searchExercises(name: String?) {
        val selectedFilters = currentSearchFilters
        val newExercises = _userExercises.value?.let {
            when {
                name.isNullOrEmpty() && selectedFilters.isEmpty() -> allExercises
                name.isNullOrEmpty() && selectedFilters.isNotEmpty() -> {
                    allExercises.filter { exercise ->
                        selectedFilters.any { filter ->
                            filter.name == exercise.bodyPart || filter.name == exercise.category
                        }
                    }
                }

                !name.isNullOrEmpty() && selectedFilters.isEmpty() -> allExercises.filter {
                    it.name.contains(name, true)
                }

                else -> allExercises.filter { exercise ->
                    val nameMatches = name.isNullOrEmpty() || exercise.name.contains(name, true)
                    val categoryMatches =
                        selectedFilters.any { filter -> filter.name == exercise.category }
                    val bodyPartMatches =
                        selectedFilters.any { filter -> filter.name == exercise.bodyPart }

                    nameMatches && (categoryMatches || bodyPartMatches)
                }
            }
        } ?: emptyList()

        search = name
        _userExercises.value = newExercises

        if (newExercises.isEmpty()) _state.value = State.NoResults(View.VISIBLE)
    }

    fun removeFilter(filter: FilterWrapper) {
        viewModelScope.launch {
            filterProvider.removeFilter(filter)
        }
        searchExercises(search)
    }

    private fun resetSelectedExercises() {
        selectableExerciseProvider.resetSelectedExercises()
    }

    sealed interface State {
        object Default : State

        data class Loading(val loadingVisibility: Int) : State

        data class ErrorFetching(val error: String?, val shutdown: Boolean) : State

        data class NoResults(val resultsVisibility: Int) : State
    }
}