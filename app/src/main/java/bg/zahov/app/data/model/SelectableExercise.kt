package bg.zahov.app.data.model


data class SelectableExercise(
    val exercise: Exercise,
    var isSelected: Boolean = false
)
