package bg.zahov.app.data.model

class LanguageKeys {
    companion object {
        const val BULGARIAN = "Bulgarian"
        const val ENGLISH = "English"
    }
}
enum class Language(val key: String) {
    Bulgarian(LanguageKeys.BULGARIAN),
    English(LanguageKeys.ENGLISH);

    companion object {
        fun fromKey(key: String) = values().firstOrNull { it.key == key }.toString()

    }
}