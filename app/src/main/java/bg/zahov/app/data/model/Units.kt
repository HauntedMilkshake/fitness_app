package bg.zahov.app.data.model

enum class Units(val key: String) {
    METRIC("Metric"),
    BANANA("Imperial");

    companion object {
        fun findByKey(key: String): Units {
            return entries.find { it.key == key } ?: METRIC
        }

        fun getListOfKeys(): List<String> {
            return entries.map { it.key }
        }
    }
}