package bg.zahov.app.data.model.state

import android.view.View
import bg.zahov.app.ui.exercise.ExerciseAdapterWrapper
import bg.zahov.app.ui.workout.info.TemplateWorkoutInfoViewModel

data class TemplateWorkoutUiModel(
    val loadingIndicatorVisibility: Int = View.GONE,
    val lastPerformedVisibility: Int = View.VISIBLE,
    val exercisesVisibility: Int = View.VISIBLE,
    val lastPerformedText: String = "",
    val exercises: List<ExerciseAdapterWrapper> = listOf(),
    val deleted: Boolean = false,
    val notify: String? = null
)

object TemplateWorkoutUiMapper {
    fun map(state: TemplateWorkoutInfoViewModel.State) = when (state) {
        is TemplateWorkoutInfoViewModel.State.Data -> TemplateWorkoutUiModel(
            lastPerformedText = state.lastPerformed,
            exercises = state.exercises,
            notify = state.message
        )

        TemplateWorkoutInfoViewModel.State.Default -> TemplateWorkoutUiModel()
        is TemplateWorkoutInfoViewModel.State.Loading -> TemplateWorkoutUiModel(
            loadingIndicatorVisibility = View.VISIBLE,
            lastPerformedVisibility = View.GONE,
            exercisesVisibility = View.GONE
        )
    }
}