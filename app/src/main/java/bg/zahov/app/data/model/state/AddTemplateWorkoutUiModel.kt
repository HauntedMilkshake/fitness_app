package bg.zahov.app.data.model.state

import bg.zahov.app.ui.workout.add.AddTemplateWorkoutViewModel

data class AddTemplateWorkoutUiModel(
    val eMessage: String? = null,
    val nMessage: String? = null,
    val success: Boolean = false,
)

object AddTemplateWorkoutUiMapper {
    fun map(state: AddTemplateWorkoutViewModel.State) = when(state) {
        is AddTemplateWorkoutViewModel.State.Error -> AddTemplateWorkoutUiModel(eMessage = state.eMessage, null)
        is AddTemplateWorkoutViewModel.State.Success -> AddTemplateWorkoutUiModel(null, nMessage = state.nMessage, true)
        AddTemplateWorkoutViewModel.State.Default -> AddTemplateWorkoutUiModel()
    }
}