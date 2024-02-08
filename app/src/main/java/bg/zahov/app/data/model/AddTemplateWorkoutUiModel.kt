package bg.zahov.app.data.model

import bg.zahov.app.ui.workout.add.AddWorkoutViewModel

data class AddTemplateWorkoutUiModel(
    val eMessage: String? = null,
    val nMessage: String? = null
)

object AddTemplateWorkoutUiMapper {
    fun map(state: AddWorkoutViewModel.State) = when(state) {
        is AddWorkoutViewModel.State.Error -> AddTemplateWorkoutUiModel(state.eMessage)
        is AddWorkoutViewModel.State.Success -> AddTemplateWorkoutUiModel(nMessage = state.nMessage)
    }
}