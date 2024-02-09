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

    fun minimize() {
        viewModelScope.launch {
            workoutStateManager.setWorkoutState(WorkoutState.MINIMIZED)
        }
    }

    fun finishWorkout(newWorkout: Workout? = null) {
        viewModelScope.launch {
            //TODO(ADD WORKOUT TO HISTORY)
            //Optional parameter so I can reuse the function for cancel
//            newWorkout?.let {
//                repo.addWorkoutToHistory(it)
//            }
            workoutStateManager.setWorkoutState(WorkoutState.INACTIVE)
        }
    }

    init {
        viewModelScope.launch {
            workoutStateManager.getTemplate()?.let {
                _workout.value = it
            }
        }
    }
}