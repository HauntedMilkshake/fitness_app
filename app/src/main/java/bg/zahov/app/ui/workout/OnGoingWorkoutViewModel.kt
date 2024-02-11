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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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
                val date = LocalDate.now()
                    .format(
                        DateTimeFormatter.ofPattern(
                            "dd-MM-yyyy",
                            Locale.getDefault()
                        )
                    )
                it?.let { _workout.postValue(it) } ?: _workout.postValue(
                    Workout(
                        "New workout",
                        null,
                        date,
                        false,
                        listOf(),
                        listOf()
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
            //TODO(ADD WORKOUT TO HISTORY)
            //Optional parameter so I can reuse the function for cancel
//            newWorkout?.let {
//                repo.addWorkoutToHistory(it)
//            }
            workoutStateManager.updateState(WorkoutState.INACTIVE)
        }
    }
}