package bg.zahov.app.data.provider

import bg.zahov.app.data.model.InteractableExerciseWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReplaceableExerciseProvider {
    companion object {
        @Volatile
        private var instance: ReplaceableExerciseProvider? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: ReplaceableExerciseProvider().also { instance = it }
        }
    }
    private val _exerciseToReplace = MutableStateFlow<InteractableExerciseWrapper?>(null)

    val exerciseToReplace: StateFlow<InteractableExerciseWrapper?>
        get() = _exerciseToReplace

    fun updateExerciseToReplace(exercise: InteractableExerciseWrapper) {
        _exerciseToReplace.value = exercise
    }
    fun resetExerciseToReplace() {
        _exerciseToReplace.value = null
    }
}