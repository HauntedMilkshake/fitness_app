package bg.zahov.app.data.model

data class OnGoingWorkoutExerciseWrapper (
    var name: String,
    var bodyPart: BodyPart,
    var category: Category,
    var isTemplate: Boolean,
    var sets: List<ClickableSet>,
    var note: String?,

)