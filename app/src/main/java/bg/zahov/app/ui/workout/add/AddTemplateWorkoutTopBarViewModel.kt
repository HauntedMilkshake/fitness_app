package bg.zahov.app.ui.workout.add

import androidx.lifecycle.ViewModel
import bg.zahov.app.data.interfaces.WorkoutProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Enables the top bar to interact with the screen itself
 */
@HiltViewModel
class AddTemplateWorkoutTopBarViewModel @Inject constructor(
    private val workoutProvider: WorkoutProvider,
) : ViewModel() {

    /**
     * Sends a signal to the provider so that we can start saving add template
     * @see [WorkoutProvider]
     */
    fun onSave() {
        workoutProvider.triggerAddTemplate()
    }
}