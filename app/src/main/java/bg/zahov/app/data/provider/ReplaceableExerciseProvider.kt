package bg.zahov.app.data.provider

import bg.zahov.app.data.model.SelectableExercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReplaceableExerciseProvider {
    companion object {
        @Volatile
        var instance: ReplaceableExerciseProvider? = null

        fun getInstance() = instance ?: synchronized(this){
            instance ?: ReplaceableExerciseProvider().also { instance = it }
        }
    }
    private val _exerciseToReplace = MutableStateFlow<SelectableExercise?>(null)

    val exerciseToReplace: StateFlow<SelectableExercise?>
        get() = _exerciseToReplace

    fun updateExerciseToReplace(exercise: SelectableExercise) {
        _exerciseToReplace.value = exercise
    }
}