package bg.zahov.app.data.model
class BodyPartKeys {
    companion object {
        const val CORE = "Core"
        const val ARMS = "Arms"
        const val BACK = "Back"
        const val CHEST = "Chest"
        const val LEGS = "Legs"
        const val SHOULDERS = "Shoulders"
        const val OTHER = "Other"
        const val OLYMPIC = "Olympic"
    }
}

enum class BodyPart(val key: String) {
    Core(BodyPartKeys.CORE),
    Arms(BodyPartKeys.ARMS),
    Back(BodyPartKeys.BACK),
    Chest(BodyPartKeys.CHEST),
    Legs(BodyPartKeys.LEGS),
    Shoulders(BodyPartKeys.SHOULDERS),
    Other(BodyPartKeys.OTHER),
    Olympic(BodyPartKeys.OLYMPIC);
}