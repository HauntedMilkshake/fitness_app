package bg.zahov.app.data.model

class UnitsKeys {
    companion object {
        const val METRIC = "Metric"
        const val BANANA = "Imperial"

        val units = listOf(METRIC, BANANA)
    }
}

enum class Units(val key: String) {
    Metric(UnitsKeys.METRIC),
    Imperial(UnitsKeys.BANANA);

    companion object {
        fun fromKey(key: String) = entries.firstOrNull { it.key == key }.toString()
    }
}