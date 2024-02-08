package bg.zahov.app.data.model

import bg.zahov.app.ui.exercise.add.AddExerciseViewModel

data class AddExerciseUiModel (
    val notify: String? = null,
    val isAdded: Boolean = false
)

object AddExerciseUiMapper {
    fun map(state: AddExerciseViewModel.State) = when(state) {
        AddExerciseViewModel.State.Default -> AddExerciseUiModel()
        is AddExerciseViewModel.State.Added -> AddExerciseUiModel(notify = state.message, isAdded = state.isAdded)
    }
}