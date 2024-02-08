package bg.zahov.app.data.model

import bg.zahov.app.ui.workout.add.AddWorkoutViewModel

data class AddTemplateWorkoutUiModel(
    val eMessage: String? = null,
    val nMessage: String? = null,
    val success: Boolean = false,
)

object AddTemplateWorkoutUiMapper {
    fun map(state: AddWorkoutViewModel.State) = when(state) {
        is AddWorkoutViewModel.State.Error -> AddTemplateWorkoutUiModel(eMessage = state.eMessage, null)
        is AddWorkoutViewModel.State.Success -> AddTemplateWorkoutUiModel(null, nMessage = state.nMessage, true)
    }
}