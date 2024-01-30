package bg.zahov.app.ui.workout.add

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.ClickableSet
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.SelectableExercise
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.repository.WorkoutRepositoryImpl
import bg.zahov.app.util.toExerciseList
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

//FIXME see comments in AuthViewModel and EditProfileViewModel
class AddWorkoutViewModel : ViewModel() {
    private val repo = WorkoutRepositoryImpl.getInstance()

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _currExercises = MutableLiveData<List<Exercise>>()
    val currExercises: LiveData<List<Exercise>>
        get() = _currExercises

    fun addWorkout(name: String, ids: List<String>) {
        if (ids.isNotEmpty()) {
            viewModelScope.launch {
                repo.addWorkout(
                    Workout(
                        name = name,
                        duration = 0.0,
                        date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())),
                        isTemplate = true,
                        exercises = emptyList(),
                        ids = ids
                    )
                )
            }
        } else {
            _state.value = State.Error("Cannot create workout template without exercises!")
        }

    }

    fun addSelectedExercises(selectedExercises: List<SelectableExercise>) {
        val captured = _currExercises.value?.toMutableList()
        captured?.addAll(selectedExercises.toExerciseList())
        _currExercises.value = captured ?: listOf()
    }

    fun addExercise(newExercise: SelectableExercise) {
        val captured = _currExercises.value?.toMutableList() ?: mutableListOf()
        captured.add(newExercise.exercise)
        _currExercises.value = captured ?: listOf()
    }

    fun removeExercise(position: Int) {
        val captured = _currExercises.value?.toMutableList() ?: mutableListOf()
        captured.removeAt(position)
        _currExercises.value = captured ?: listOf()

    }

    fun addSet(ePosition: Int, set: ClickableSet) {
        _currExercises.value?.toMutableList()?.getOrNull(ePosition)?.let { exercise ->
            exercise.sets.toMutableList().add(set.set)
            Log.d("SET", "${exercise.name} ${exercise.sets.size}")
            _currExercises.value = _currExercises.value
        }
    }

    fun removeSet(ePosition: Int, sPosition: Int) {
        _currExercises.value?.toMutableList()?.getOrNull(ePosition)?.let { exercise ->
            exercise.sets.toMutableList().removeAt(sPosition)
            _currExercises.value = _currExercises.value
        }
    }

    fun resetSelectedExercises() {
        _currExercises.value = listOf()
    }

    sealed interface State {
        object Default : State
        data class Error(val message: String) : State
    }
}