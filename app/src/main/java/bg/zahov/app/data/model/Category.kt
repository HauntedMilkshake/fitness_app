package bg.zahov.app.data.model

class CategoryKeys {
    companion object {
        const val BARBELL = "Barbell"
        const val DUMBBELL = "Dumbbell"
        const val MACHINE = "Machine"
        const val ADDITIONAL_WEIGHT = "AdditionalWeight"
        const val NONE = "None"
        const val CABLE = "Cable"

        //reps only, cardio, and timed are soonTM
//        const val ASSISTED_WEIGHT = "AssistedWeight"
//        const val REPS_ONLY = "RepsOnly"
//        const val CARDIO = "Cardio"
//        const val TIMED = "Timed"
    }
}

enum class Category(val key: String) {
    Barbell(CategoryKeys.BARBELL),
    Dumbbell(CategoryKeys.DUMBBELL),
    Machine(CategoryKeys.MACHINE),
    AdditionalWeight(CategoryKeys.ADDITIONAL_WEIGHT),
    Cable(CategoryKeys.CABLE),
    None(CategoryKeys.NONE);
    //    AssistedWeight(CategoryKeys.ASSISTED_WEIGHT),
//    RepsOnly(CategoryKeys.REPS_ONLY),
//    Cardio(CategoryKeys.CARDIO),
//    Timed(CategoryKeys.TIMED),

    companion object {
        fun fromKey(key: String) = values().firstOrNull { it.key.equals(key, true) }
    }
}
