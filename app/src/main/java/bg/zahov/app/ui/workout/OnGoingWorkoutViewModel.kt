package bg.zahov.app.ui.workout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutState
import bg.zahov.app.getWorkoutProvider
import bg.zahov.app.getWorkoutStateManager
import bg.zahov.app.util.currDateToString
import bg.zahov.app.util.hashString
import kotlinx.coroutines.launch

class OnGoingWorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutStateManager by lazy {
        application.getWorkoutStateManager()
    }

    private val repo by lazy {
        application.getWorkoutProvider()
    }

    private val _workout = MutableLiveData<Workout>()
    val workout: LiveData<Workout>
        get() = _workout

    init {
        viewModelScope.launch {
            workoutStateManager.template.collect {
                it?.let { _workout.postValue(it) } ?: _workout.postValue(
                    Workout(
                        hashString("New workout"),
                        "New workout",
                        duration = null,
                        date = currDateToString(),
                        isTemplate = false,
                        exercises = listOf(),
                        ids = listOf()
                    )
                )
            }
        }
    }

    fun minimize() {
        viewModelScope.launch {
            workoutStateManager.updateState(WorkoutState.MINIMIZED)
        }
    }

    fun finishWorkout(newWorkout: Workout? = null) {
        viewModelScope.launch {
            newWorkout?.let {
                repo.addWorkoutToHistory(it)
            }
            workoutStateManager.updateState(WorkoutState.INACTIVE)
        }
    }
}