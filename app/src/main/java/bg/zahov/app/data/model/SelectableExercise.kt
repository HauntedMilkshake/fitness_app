package bg.zahov.app.data.model

import bg.zahov.app.data.local.Exercise

data class SelectableExercise(
    val exercise: Exercise,
    var isSelected: Boolean = false
)
