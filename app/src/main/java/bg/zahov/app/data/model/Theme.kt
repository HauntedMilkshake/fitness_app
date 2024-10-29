package bg.zahov.app.data.model

enum class Theme(val key: String) {
    Dark("DARK"),
    Light("LIGHT");

    companion object {
        fun findByKey(key: String): Theme {
            return Theme.entries.find { it.key == key } ?: Dark
        }

        fun getListOfKeys(): List<String> {
            return Theme.entries.map { it.key }

        }
    }
}