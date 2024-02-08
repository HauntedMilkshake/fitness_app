package bg.zahov.app.ui.workout

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.model.Workout
import bg.zahov.app.getWorkoutProvider
import kotlinx.coroutines.launch

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val repo by lazy {
        application.getWorkoutProvider()
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _templates = MutableLiveData<List<Workout>>(listOf())
    val templates: LiveData<List<Workout>>
        get() = _templates

    init {
        getWorkouts()
    }

    private fun getWorkouts() {
        viewModelScope.launch {
            try {
                repo.getTemplateWorkouts().collect {
                    _templates.postValue(it)
                }
            } catch (e: CriticalDataNullException) {
                _state.postValue(State.Error(e.message, false))
            }
        }
    }

    sealed interface State {
        data class Error(val error: String?, val shutdown: Boolean) : State
    }
}