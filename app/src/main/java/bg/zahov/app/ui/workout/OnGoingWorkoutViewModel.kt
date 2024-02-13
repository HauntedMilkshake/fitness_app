package bg.zahov.app.ui.workout

import android.app.Application
import android.util.Log
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

    private val _timer = MutableLiveData<String>()
    val timer: LiveData<String>
        get() = _timer

    init {
        viewModelScope.launch {
            launch {
                workoutStateManager.template.collect {
                    it?.let { _workout.postValue(it) } ?: run {
                        val workout = Workout(
                            hashString("New workout"),
                            "New workout",
                            duration = null,
                            date = currDateToString(),
                            isTemplate = false,
                            exercises = listOf(),
                            ids = listOf()
                        )
                        _workout.postValue(workout)
                        workoutStateManager.updateTemplate(workout)
                    }
                }
            }
            launch {
                workoutStateManager.timer.collect {
                    _timer.postValue(
                        String.format(
                            "%02d:%02d:%02d",
                            (it / (1000 * 60 * 60)) % 24,
                            (it / (1000 * 60)) % 60,
                            (it / 1000) % 60
                        )
                    )
                }
            }

        }
    }

    fun minimize() {
        viewModelScope.launch {
            workoutStateManager.updateState(WorkoutState.MINIMIZED)
        }
    }

    fun cancel() {
        viewModelScope.launch {
            Log.d("UPDATING STATE FROM WORKOUT", "INACTIVE")
            workoutStateManager.updateState(WorkoutState.INACTIVE)
        }
    }

    fun finishWorkout() {
        viewModelScope.launch {
            _workout.value?.let {
                repo.addWorkoutToHistory(it)
            }
            workoutStateManager.updateState(WorkoutState.INACTIVE)
        }
    }
}