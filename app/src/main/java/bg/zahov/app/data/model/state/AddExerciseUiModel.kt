package bg.zahov.app.data.model.state

import bg.zahov.app.ui.exercise.add.AddExerciseViewModel

data class AddExerciseUiModel (
    val notify: String? = null,
    val action: Int? = null
)

object AddExerciseUiMapper {
    fun map(state: AddExerciseViewModel.State) = when(state) {
        AddExerciseViewModel.State.Default -> AddExerciseUiModel()
        is AddExerciseViewModel.State.Added -> AddExerciseUiModel(notify = state.message, action = state.action)
    }
}