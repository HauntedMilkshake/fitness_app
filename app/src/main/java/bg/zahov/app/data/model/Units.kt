package bg.zahov.app.data.model

class UnitsKeys {
    companion object {
        const val METRIC = "Metric"
        const val BANANA = "Imperial"
    }
}
enum class Units(val key: String) {
    METRIC(UnitsKeys.METRIC),
    BANANA(UnitsKeys.BANANA);

    companion object {
        fun fromKey(key: String) = values().firstOrNull { it.key == key }.toString()
    }
}