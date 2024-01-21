package bg.zahov.app.data.model

class CategoryKeys {
    companion object {
        const val BARBELL = "Barbell"
        const val DUMBBELL = "Dumbbell"
        const val MACHINE = "Machine"
        const val ADDITIONAL_WEIGHT = "Additional weight"
        const val ASSISTED_WEIGHT = "Assisted weight"
        const val REPS_ONLY = "Reps only"
        const val CARDIO = "Cardio"
        const val TIMED = "Timed"
        const val NONE = "None"
        const val CABLE = "Cable"
    }
}

enum class Category(val key: String) {
    Barbell(CategoryKeys.BARBELL),
    Dumbbell(CategoryKeys.DUMBBELL),
    Machine(CategoryKeys.MACHINE),
    AdditionalWeight(CategoryKeys.ADDITIONAL_WEIGHT),
    AssistedWeight(CategoryKeys.ASSISTED_WEIGHT),
    RepsOnly(CategoryKeys.REPS_ONLY),
    Cardio(CategoryKeys.CARDIO),
    Timed(CategoryKeys.TIMED),
    None(CategoryKeys.NONE),
    Cable(CategoryKeys.CABLE);

    companion object {
        fun fromKey(key: String) = values().firstOrNull { it.key == key }.toString()

    }
}
