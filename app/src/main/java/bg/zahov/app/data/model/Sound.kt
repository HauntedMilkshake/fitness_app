package bg.zahov.app.data.model

enum class Sound(val key: String) {
    SOUND_1("cool_name_1"),
    SOUND_2("cool_name_2"),
    SOUND_3("cool_name_3");

    companion object {
        fun findByKey(key: String): Sound {
            return Sound.entries.find { it.key == key } ?: SOUND_1
        }

        fun getListOfKeys(): List<String> {
            return Sound.entries.map { it.key }
        }
    }
}