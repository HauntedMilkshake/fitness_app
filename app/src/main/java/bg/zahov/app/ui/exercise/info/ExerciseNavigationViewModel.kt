package bg.zahov.app.ui.exercise.info

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.getWorkoutProvider
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class ExerciseNavigationViewModel(application: Application) : AndroidViewModel(application) {
    private val exerciseProvider by lazy {
        application.getWorkoutProvider()
    }
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    init {
        viewModelScope.launch {
            try {
                exerciseProvider.getClickedTemplateExercise().collect {
                    Log.d("collecting exercise", it.name)
                    _state.postValue(State.Data(it.name))
                }
            } catch (e: IllegalArgumentException) {
                _state.postValue(State.Error("Try again by going back and selecting an exercise!"))
            }
        }
    }

    sealed interface State {
        data class Data(val exerciseName: String) : State
        data class Error(val message: String) : State
    }
}