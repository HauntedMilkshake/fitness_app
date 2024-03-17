package bg.zahov.app.ui.exercise.info.records

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import bg.zahov.app.getWorkoutProvider

class ExerciseRecordsViewModel(application: Application): AndroidViewModel(application) {
    private val workoutProvider by lazy {
        application.getWorkoutProvider()
    }
}