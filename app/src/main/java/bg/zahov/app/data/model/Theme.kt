package bg.zahov.app.data.model

class ThemeKeys {
    companion object {
        const val DARK = "Dark"
        const val LIGHT = "Light"
    }
}
enum class Theme(val key: String) {
    Dark(ThemeKeys.DARK),
    Light(ThemeKeys.LIGHT);

    companion object {
        fun fromKey(key: String) = values().firstOrNull { it.key == key }.toString()
    }
}