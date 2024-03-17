package bg.zahov.app.ui.exercise.info

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import bg.zahov.app.getWorkoutProvider
import java.lang.IllegalArgumentException

class ExerciseNavigationViewModel(application: Application): AndroidViewModel(application) {
    private val exerciseProvider by lazy {
        application.getWorkoutProvider()
    }
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state
    init {
        try {
            _state.value = State.Data(exerciseProvider.getClickedTemplateExercise().name)
        } catch(e: IllegalArgumentException) {
            _state.value = State.Error("Try again by going back and selecting an exercise!")
        }
    }
    sealed interface State {
        data class Data(val exerciseName: String): State
        data class Error(val message: String): State
    }
}