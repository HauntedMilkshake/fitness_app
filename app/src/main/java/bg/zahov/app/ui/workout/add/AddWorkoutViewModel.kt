package bg.zahov.app.ui.workout.add

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.ClickableSet
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.SelectableExercise
import bg.zahov.app.data.model.Sets
import bg.zahov.app.data.model.Workout
import bg.zahov.app.getWorkoutProvider
import bg.zahov.app.util.toExerciseList
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class AddWorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val repo by lazy {
        application.getWorkoutProvider()
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _currExercises = MutableLiveData<List<Exercise>>()
    val currExercises: LiveData<List<Exercise>>
        get() = _currExercises

    fun addWorkout(name: String, ids: List<String>) {
        if (ids.isEmpty()) {
            _state.value = State.Error("Cannot create workout template without exercises!")
            return
        }

        viewModelScope.launch {
            repo.addWorkout(
                Workout(
                    name = name,
                    duration = 0.0,
                    date = LocalDate.now()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())),
                    isTemplate = true,
                    exercises = emptyList(),
                    ids = ids
                )
            )
        }
    }

//    fun addSelectedExercises(selectedExercises: List<SelectableExercise>) {
//        val captured = _currExercises.value?.toMutableList() ?: mutableListOf()
//        captured.addAll(selectedExercises.toExerciseList())
//        _currExercises.value = captured
//    }

    fun addExercise(newExercise: Exercise) {
        val captured = _currExercises.value?.toMutableList() ?: mutableListOf()
        captured.add(newExercise)
        _currExercises.value = captured
        captured.forEach {
            Log.d("ADD", it.name)
        }
    }

    fun removeExercise(item: Exercise) {
        val captured = _currExercises.value?.toMutableList() ?: mutableListOf()
        captured.remove(item)
        _currExercises.value = captured
        captured.forEach {
            Log.d("DELETE", it.name)
        }
    }

    fun addSet(exercise: Exercise, set: Sets) {
        val exercises = _currExercises.value?.toMutableList() ?: emptyList()
        val foundExercise = exercises.find { it == exercise }
        foundExercise?.let {
            val newSets = it.sets.toMutableList()
            newSets.add(set)
            it.sets = newSets
        }
        _currExercises.value = exercises
    }

    fun removeSet(exercise: Exercise, set: Sets) {
        val exercises = _currExercises.value?.toMutableList() ?: emptyList()
        val foundExercise = exercises.find { it == exercise }
        foundExercise?.let {
            if (it.sets.isEmpty()) {
                val newSets = it.sets.toMutableList()
                newSets.remove(set)
                it.sets = newSets
            }
        }

        _currExercises.value = exercises
    }

    fun resetSelectedExercises() {
        _currExercises.value = listOf()
    }

    sealed interface State {
        data class Error(val eMessage: String) : State
        data class Notify(val nMessage: String) : State
    }
}