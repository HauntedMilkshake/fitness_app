package bg.zahov.app.data.provider

import bg.zahov.app.data.model.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class SelectableExerciseProvider @Inject constructor() {
    companion object {
        @Volatile
        private var instance: SelectableExerciseProvider? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: SelectableExerciseProvider().also { instance = it }
        }
    }

    private val _selectedExercises = MutableStateFlow<List<Exercise>>(mutableListOf())
    val selectedExercises: StateFlow<List<Exercise>>
        get() = _selectedExercises

    fun addExercises(newExercises: List<Exercise>) {
        val selected = _selectedExercises.value.toMutableList()
        selected.addAll(newExercises)
        _selectedExercises.value = selected
    }

    fun removeExercise(exercise: Exercise) {
        val selected = _selectedExercises.value.toMutableList()
//        exercise.isSelected = false
        selected.remove(exercise)
        _selectedExercises.value = selected
    }

    fun resetSelectedExercises() {
        _selectedExercises.value = mutableListOf()
    }
}