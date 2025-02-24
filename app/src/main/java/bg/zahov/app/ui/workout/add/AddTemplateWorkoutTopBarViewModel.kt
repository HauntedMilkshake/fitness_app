package bg.zahov.app.ui.workout.add

import androidx.lifecycle.ViewModel
import bg.zahov.app.data.interfaces.WorkoutProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class AddTemplateWorkoutTopBarViewModel @Inject constructor(
    private val workoutProvider: WorkoutProvider,
) : ViewModel() {

    fun onSave() {
        workoutProvider.triggerAddTemplate()
    }
}