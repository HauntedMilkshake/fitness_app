package bg.zahov.app.data.model

class SetKeys {
    companion object {
        const val WARMUP = "Warmup"
        const val DROP_SET = "Drop set"
        const val FAILURE = "Failure"
    }
}
enum class SetType(val key: String) {
    WARMUP(SetKeys.WARMUP),
    DROP_SET(SetKeys.DROP_SET),
    FAILURE(SetKeys.FAILURE);

    companion object {
        fun fromKey(key: String) = values().firstOrNull { it.key == key }.toString()

    }
}