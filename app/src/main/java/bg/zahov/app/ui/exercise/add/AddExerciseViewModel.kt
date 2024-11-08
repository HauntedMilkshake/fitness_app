package bg.zahov.app.ui.exercise.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.Inject
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.ui.exercise.filter.FilterWrapper
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
        val category: Category? = null,
        val bodyPart: BodyPart? = null,
        val userMessage: String = "",
        val showDialogCategory: Boolean = false,
        val showDialogBodyPart: Boolean = false,
    )

    fun onNameChange(name: String) {
        _newExerciseData.update { old ->
            old.copy(name = name)
        }
    }

    fun addExercise(exerciseTitle: String) {
        viewModelScope.launch {
            _newExerciseData.value.bodyPart?.let {
                _newExerciseData.value.category?.let { it1 ->
                    repo.addTemplateExercise(
                        Exercise(exerciseTitle, it, it1, true,)
                    )
                }
            }
        }
        _newExerciseData.update { old -> old.copy(userMessage = "success") }

    }

    fun onFilterChange(filter: FilterWrapper) {
        when (filter) {
        }
        _newExerciseData.update { old -> old.copy() }
    }
}
