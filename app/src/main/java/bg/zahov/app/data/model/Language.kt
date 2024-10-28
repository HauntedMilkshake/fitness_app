package bg.zahov.app.data.model

class LanguageKeys {
    companion object {
        const val BULGARIAN = "Bulgarian"
        const val ENGLISH = "English"

        val languages = listOf(BULGARIAN, ENGLISH)
    }
}

enum class Language(val key: String) {
    Bulgarian(LanguageKeys.BULGARIAN),
    English(LanguageKeys.ENGLISH);

    companion object {
        fun fromKey(key: String) = entries.firstOrNull { it.key == key }.toString()

    }
}