package bg.zahov.app.ui.exercise.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Filter
import bg.zahov.app.data.model.FilterItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddExerciseViewModel(
    private val repo: WorkoutProvider = Inject.workoutProvider,
) : ViewModel() {

    private val _newExerciseData = MutableStateFlow(NewExerciseData())
    val newExerciseData: StateFlow<NewExerciseData> = _newExerciseData

    data class NewExerciseData(
        val name: String = "",
        val bodyPartFilters: List<FilterItem> = enumValues<BodyPart>().flatMap { bodyPart ->
            listOf(FilterItem(filter = Filter.BodyPartFilter(bodyPart)))
        },
        val categoryFilters: List<FilterItem> = enumValues<Category>().flatMap { category ->
            listOf(FilterItem(filter = Filter.CategoryFilter(category)))
        },
        val uiEventState: EventState = EventState.HideDialog,
        val userMessage: String = "",
    )

    enum class EventState {
        HideDialog,
        ShowBodyPartFilter,
        ShowCategoryFilter,
        NavigateBack,
    }

    fun onNameChange(name: String) {
        _newExerciseData.update { old ->
            old.copy(name = name)
        }
    }

    fun changeEvent(event: EventState) {
        if (event != EventState.NavigateBack) {
            _newExerciseData.update { old -> old.copy(uiEventState = event) }
        } else {
            _newExerciseData.update { old -> old.copy(uiEventState = event) }
            _newExerciseData.update { old -> old.copy(uiEventState = EventState.HideDialog) }
        }
    }

    fun addExercise(exerciseTitle: String) {
        viewModelScope.launch {
            val bodyPart = getSelectedBodyPart()
            val category = getSelectedCategory()

            if (bodyPart == null || category == null) {
                _newExerciseData.update { old ->
                    old.copy(userMessage = "Don't leave empty fields")
                }
            } else {
                repo.addTemplateExercise(
                    Exercise(
                        exerciseTitle,
                        bodyPart = bodyPart,
                        category = category,
                        true
                    )
                )
                _newExerciseData.update { old -> old.copy(userMessage = "Success") }
            }
        }
    }


    /**
     * Finds the selected BodyPartFilter from a list of filters.
     *
     * @return The selected BodyPartFilter, or null if none is selected.
     */
    fun getSelectedBodyPart(): BodyPart? {
        return (_newExerciseData.value.bodyPartFilters
            .firstOrNull { it.selected && it.filter is Filter.BodyPartFilter }
            ?.filter as? Filter.BodyPartFilter)?.bodyPart
    }

    /**
     * Finds the selected BodyPartFilter from a list of filters.
     *
     * @return The selected BodyPartFilter, or null if none is selected.
     */
    fun getSelectedCategory(): Category? {
        return (_newExerciseData.value.bodyPartFilters
            .firstOrNull { it.selected && it.filter is Filter.CategoryFilter }
            ?.filter as? Filter.CategoryFilter)?.category
    }

    fun onCategoryFilterChange(filter: FilterItem) {
        _newExerciseData.update { old ->
            old.copy(
                categoryFilters = old.categoryFilters.map { item ->
                    item.copy(selected = (item.name == filter.name))
                }
            )
        }
    }

    fun onBodyPartFilterChange(filter: FilterItem) {
        _newExerciseData.update { old ->
            old.copy(
                bodyPartFilters = old.bodyPartFilters.map { item ->
                    item.copy(selected = (item.name == filter.name))
                }
            )
        }
    }
}

