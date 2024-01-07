package bg.zahov.app.data

import bg.zahov.app.backend.Exercise

data class SelectableExercise(
    val exercise: Exercise,
    var isSelected: Boolean = false
)
