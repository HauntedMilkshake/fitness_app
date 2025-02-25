package bg.zahov.app.data.provider

import bg.zahov.app.data.model.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ReplaceableExerciseProvider @Inject constructor(){
    private val _exerciseToReplace = MutableStateFlow<Exercise?>(null)

    val exerciseToReplace: StateFlow<Exercise?>
        get() = _exerciseToReplace

    fun updateExerciseToReplace(exercise: Exercise) {
        _exerciseToReplace.value = exercise
    }
    fun resetExerciseToReplace() {
        _exerciseToReplace.value = null
    }
}