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
    CORE(BodyPartKeys.CORE),
    ARMS(BodyPartKeys.ARMS),
    BACK(BodyPartKeys.BACK),
    CHEST(BodyPartKeys.CHEST),
    LEGS(BodyPartKeys.LEGS),
    SHOULDERS(BodyPartKeys.SHOULDERS),
    OTHER(BodyPartKeys.OTHER),
    OLYMPIC(BodyPartKeys.OLYMPIC);

    companion object {
        fun fromKey(key: String) = values().firstOrNull { it.key == key }.toString()
    }
}