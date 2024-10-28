package bg.zahov.app.data.model

class SoundKeys {
    companion object {
        //WIP
        const val SOUND_1 = "cool_name_1"
        const val SOUND_2 = "cool_name_2"
        const val SOUND_3 = "cool_name_3"

        val sounds = listOf(SOUND_1, SOUND_2, SOUND_3)
    }
}

enum class Sound(val key: String) {
    SOUND_1(SoundKeys.SOUND_1),
    SOUND_2(SoundKeys.SOUND_2),
    SOUND_3(SoundKeys.SOUND_3);

    companion object {
        fun fromKey(key: String) = entries.firstOrNull { it.key == key }.toString()
    }
}