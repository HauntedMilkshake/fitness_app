package bg.zahov.app.ui.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.repository.WorkoutRepositoryImpl
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class WorkoutViewModel : ViewModel() {
    private val repo = WorkoutRepositoryImpl.getInstance()

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _templates = MutableLiveData<List<Workout>>(listOf())
    val templates: LiveData<List<Workout>>
        get() = _templates

    init {
        getWorkouts()
    }

    fun getWorkouts() {
        viewModelScope.launch {
            repo.getTemplateWorkouts()?.collect {
                _templates.postValue(it)
            } ?: _state.postValue(State.Error("Error fetching workouts"))
        }
    }

    sealed interface State {
        object Default: State
        data class Loading(val isLoading: Boolean): State
        data class Error(val error: String): State
    }
}