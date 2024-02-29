package bg.zahov.app.data.model

data class InteractableExerciseWrapper (
    var id: String,
    var name: String,
    var bodyPart: BodyPart,
    var category: Category,
    var isTemplate: Boolean = false,
    var sets: List<ClickableSet> = listOf(),
    var note: String? = null,
    var isSelected: Boolean = false,
    var isNoteVisible: Boolean = false
)