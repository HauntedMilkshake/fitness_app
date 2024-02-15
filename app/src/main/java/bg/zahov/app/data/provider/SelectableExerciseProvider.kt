package bg.zahov.app.data.provider

import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.SelectableExercise
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class SelectableExerciseProvider {
    companion object {
        @Volatile
        private var instance: SelectableExerciseProvider? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: SelectableExerciseProvider().also { instance = it }
        }
    }

    private val _exercises = MutableStateFlow<List<SelectableExercise>>(mutableListOf())
    val exercises: StateFlow<List<SelectableExercise>>
        get() = _exercises

    private val _selectedExercises = MutableStateFlow<List<SelectableExercise>>(mutableListOf())
    val selectedExercises: StateFlow<List<SelectableExercise>>
        get() = _selectedExercises

    fun initExercises(exercises: List<Exercise>) {
        _exercises.value = exercises.map { SelectableExercise(it, false) }
    }

    fun addExercise(newExercise: SelectableExercise) {
        val selected = _selectedExercises.value.toMutableList()
        newExercise.isSelected = true
        selected.add(newExercise)

        _selectedExercises.value = selected
    }

    fun removeExercise(exercise: SelectableExercise) {
        val selected = _selectedExercises.value.toMutableList()
        exercise.isSelected = false
        selected.remove(exercise)

        _selectedExercises.value = selected
    }

    fun resetSelectedExercises() {
        _selectedExercises.value = mutableListOf()
    }
}