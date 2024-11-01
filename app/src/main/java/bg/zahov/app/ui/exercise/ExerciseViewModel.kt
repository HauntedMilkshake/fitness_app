package bg.zahov.app.ui.exercise

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Filter
import bg.zahov.app.data.model.FilterWrapper
import bg.zahov.app.data.provider.AddExerciseToWorkoutProvider
import bg.zahov.app.data.provider.FilterProvider
import bg.zahov.app.data.provider.ReplaceableExerciseProvider
import bg.zahov.app.data.provider.SelectableExerciseProvider
import bg.zahov.app.util.toExerciseWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExercisesWrapper(
    val name: String,
    val bodyPart: BodyPart,
    val category: Category,
    var selected: Boolean = false
)

enum class ExerciseFlag {
    Default,
    Selecting,
    Replacing,
    Adding;
}

class ExerciseViewModel(
    private val repo: WorkoutProvider = Inject.workoutProvider,
    private val selectableExerciseProvider: SelectableExerciseProvider = Inject.selectedExerciseProvider,
    private val replaceableExerciseProvider: ReplaceableExerciseProvider = Inject.replaceableExerciseProvider,
    private val addExerciseToWorkoutProvider: AddExerciseToWorkoutProvider = Inject.workoutAddedExerciseProvider,
    private val filterProvider: FilterProvider = Inject.filterProvider,
    private val serviceError: ServiceErrorHandler = Inject.serviceErrorHandler
) : ViewModel() {
    data class UiState(
        val exercises: List<ExercisesWrapper> = listOf(),
        val filters: List<FilterWrapper> = listOf(),
        val loading: Boolean = true,
        val search: String = "",
        val flag: ExerciseFlag = ExerciseFlag.Default,
        val showDialog: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private var currentlySelectedExerciseToReplace: Int? = null
    private var allExercises: List<Exercise> = mutableListOf()

    init {
        viewModelScope.launch {
            launch {
                filterProvider.filters.collect { filters ->
                    _uiState.update { old ->
                        old.copy(filters = filters, loading = true)
                    }
                    _uiState.update { old ->
                        old.copy(exercises = getFiltered(), loading = false)
                    }
                }
            }
            launch {
                try {
                    repo.getTemplateExercises().collect { exercises ->
                        allExercises = exercises
                        _uiState.update { old ->
                            old.copy(
                                exercises = allExercises.map { it.toExerciseWrapper() },
                                loading = false
                            )
                        }
                    }
                } catch (e: CriticalDataNullException) {
                    serviceError.stopApplication()
                }
            }
        }
    }

    fun onExerciseClicked(position: Int) {
        _uiState.update { old ->
            val updatedExercises = old.exercises.mapIndexed { index, exercise ->
                when (index) {
                    position -> exercise.copy(selected = exercise.selected.not())
                    currentlySelectedExerciseToReplace -> exercise.copy(selected = false)
                    else -> exercise
                }
            }
            currentlySelectedExerciseToReplace =
                if (_uiState.value.flag == ExerciseFlag.Replacing) position else null
            old.copy(exercises = updatedExercises)
        }
    }

    private fun getFiltered(
        filter: List<FilterWrapper> = _uiState.value.filters,
        exercises: List<ExercisesWrapper> = allExercises.map { it.toExerciseWrapper() },
        searchString: String = _uiState.value.search
    ): List<ExercisesWrapper> {
        return exercises.filter { exercise ->
            val matchesFilters = filter.isEmpty() ||
                    filter.any { filterWrapper ->
                        matchesFilter(exercise, filterWrapper.filter)
                    }

            val matchesSearch = searchString.isBlank() ||
                    exercise.name.contains(searchString, ignoreCase = true)

            matchesFilters && matchesSearch
        }
    }

    private fun matchesFilter(exercise: ExercisesWrapper, filter: Filter): Boolean {
        return when (filter) {
            is Filter.CategoryFilter -> {
                exercise.category == filter.category
            }

            is Filter.BodyPartFilter -> {
                exercise.bodyPart == filter.bodyPart
            }
        }
    }


    fun setClickedExercise(name: String) {
        viewModelScope.launch {
            allExercises.find { it.name == name }?.let {
                repo.setClickedTemplateExercise(it)
            }
        }
    }

    fun onConfirm() {
        _uiState.update { old -> old.copy(flag = ExerciseFlag.Default) }
        selectableExerciseProvider.resetSelectedExercises()
    }

    fun confirmSelectedExercises() {
        when (_uiState.value.flag) {
            ExerciseFlag.Replacing -> {
                _uiState.value.exercises.find { it.selected }?.let {
                    allExercises.find { template -> template.name == it.name }?.let {
                        replaceableExerciseProvider.updateExerciseToReplace(it)
                    }
                }
            }

            else -> {
                val selectedExercises = mutableListOf<Exercise>()
                _uiState.value.exercises.forEach {
                    if (it.selected) {
                        allExercises
                            .find { exercise -> exercise.name == it.name }
                            ?.let { found -> selectedExercises.add(found) }
                    }
                }

                if (selectedExercises.isNotEmpty()) {
                    if (_uiState.value.flag == ExerciseFlag.Adding) {
                        addExerciseToWorkoutProvider.addExercises(selectedExercises)
                    }
                    if (_uiState.value.flag == ExerciseFlag.Selecting)
                        selectableExerciseProvider.addExercises(selectedExercises)
                }
            }
        }
        onConfirm()
    }

    fun onSearchChange(search: String) {
        _uiState.update { old ->
            old.copy(
                search = search,
                exercises = getFiltered(searchString = search)
            )
        }
    }

    fun updateFlag(addable: Boolean, replaceable: Boolean, selectable: Boolean) {
        val flag = when {
            addable -> ExerciseFlag.Adding
            replaceable -> ExerciseFlag.Replacing
            selectable && replaceable.not() -> ExerciseFlag.Selecting
            else -> ExerciseFlag.Default
        }
        _uiState.update { old -> old.copy(flag = flag) }
    }

    fun updateShowDialog(showDialog: Boolean) {
        _uiState.update { old -> old.copy(showDialog = showDialog) }
    }

    fun removeFilter(filter: FilterWrapper) {
        viewModelScope.launch {
            filterProvider.updateFilter(filter)
        }
    }
}