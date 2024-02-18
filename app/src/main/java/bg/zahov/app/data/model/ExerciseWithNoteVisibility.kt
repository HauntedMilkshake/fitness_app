package bg.zahov.app.data.model

data class ExerciseWithNoteVisibility (
    val exercise: Exercise,
    var noteVisibility: Boolean = false
)