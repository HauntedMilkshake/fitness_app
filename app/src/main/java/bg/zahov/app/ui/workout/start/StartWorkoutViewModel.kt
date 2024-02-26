package bg.zahov.app.ui.workout.start

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutState
import bg.zahov.app.getWorkoutProvider
import bg.zahov.app.getWorkoutStateManager
import bg.zahov.app.util.currDateToString
import kotlinx.coroutines.launch

class StartWorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val repo by lazy {
        application.getWorkoutProvider()
    }

    private val workoutState by lazy {
        application.getWorkoutStateManager()
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _templates = MutableLiveData<List<Workout>>(listOf())
    val templates: LiveData<List<Workout>>
        get() = _templates

    init {
        getWorkouts()
        viewModelScope.launch {
            workoutState.state.collect {
                when (it) {
                    WorkoutState.MINIMIZED -> _state.postValue(State.Active(true))
                    WorkoutState.INACTIVE -> _state.postValue(State.Active(false))
                    else -> {}
                }
            }
        }
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

    fun startEmptyWorkout() {
        viewModelScope.launch {
            workoutState.updateState(WorkoutState.ACTIVE)
        }
    }

    fun startWorkoutFromTemplate(position: Int) {
        viewModelScope.launch {
            _templates.value?.get(position)?.let {
                workoutState.updateTemplate(it)
            }
            workoutState.updateState(WorkoutState.ACTIVE)
        }
    }

    fun deleteTemplateWorkout(position: Int) {
        val list = _templates.value?.toMutableList()
        val toBeRemoved = list?.removeAt(position)
        _templates.value = list ?: listOf()
        viewModelScope.launch {
            toBeRemoved?.let { repo.deleteTemplateWorkout(it) }
        }
    }

    fun addDuplicateTemplateWorkout(position: Int) {
        val template = _templates.value?.get(position)
        val count = _templates.value?.count { template?.id == it.id }
        template?.let { workout ->
            val dupe = Workout(
                id = workout.id,
                name = "${workout.name} duplicate $count",
                duration = null,
                date = currDateToString(),
                isTemplate = true,
                exercises = workout.exercises
            )
            val list = _templates.value?.toMutableList()
            list?.add(dupe)
            _templates.value = list ?: listOf()
            viewModelScope.launch {
                repo.addTemplateWorkout(
                    dupe
                )
            }
        }
    }

    sealed interface State {
        data class Error(val error: String?, val shutdown: Boolean) : State

        data class Active(val isWorkoutActive: Boolean, val message: String? = null) : State
    }
}
