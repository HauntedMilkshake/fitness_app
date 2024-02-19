package bg.zahov.app.data.provider

import bg.zahov.app.data.model.SelectableExercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AddExerciseToWorkoutProvider {
    companion object {
        @Volatile
        private var instance: AddExerciseToWorkoutProvider? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: AddExerciseToWorkoutProvider().also { instance = it }
        }
    }

    private val _selectedExercises = MutableStateFlow<List<SelectableExercise>>(mutableListOf())
    val selectedExercises: StateFlow<List<SelectableExercise>>
        get() = _selectedExercises

    fun addExercises(newExercises: List<SelectableExercise>) {
        val selected = _selectedExercises.value.toMutableList()
        selected.addAll(newExercises)
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