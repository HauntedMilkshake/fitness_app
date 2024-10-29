package bg.zahov.app.data.model


enum class Language(val key: String) {
    Bulgarian("Bulgarian"),
    English("English");

    companion object {
        fun findByKey(key: String): Language {
            return Language.entries.find { it.key == key } ?: English
        }

        fun getListOfKeys(): List<String> {
            return Language.entries.map { it.key }
        }
    }
}