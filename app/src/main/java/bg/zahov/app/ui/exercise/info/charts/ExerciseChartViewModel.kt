package bg.zahov.app.ui.exercise.info.charts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import bg.zahov.app.getWorkoutProvider

class ExerciseChartViewModel(application: Application): AndroidViewModel(application) {
    private val workoutProvider by lazy {
        application.getWorkoutProvider()
    }


}