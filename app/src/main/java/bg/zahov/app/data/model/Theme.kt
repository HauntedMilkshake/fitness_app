package bg.zahov.app.data.model

class ThemeKeys {
    companion object {
        const val DARK = "Dark"
        const val LIGHT = "Light"

        val theme = listOf(DARK, LIGHT)
    }
}
enum class Theme(val key: String) {
    Dark(ThemeKeys.DARK),
    Light(ThemeKeys.LIGHT);

    companion object {
        fun fromKey(key: String) = entries.firstOrNull { it.key == key }.toString()
    }
}