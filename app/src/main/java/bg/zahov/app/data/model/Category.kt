package bg.zahov.app.data.model

enum class Category(val key: String) {
    Barbell("Barbell"),
    Dumbbell("Dumbbell"),
    Machine("Machine"),
    AdditionalWeight("AdditionalWeight"),
    Cable("Cable"),
    None("None"),

    //        reps only, cardio, and timed are soonTM
    AssistedWeight("AssistedWeight"),
    RepsOnly("RepsOnly"),
    Cardio("Cardio"),
    Timed("Timed");

    companion object {
        fun fromKey(key: String) = entries.firstOrNull { it.key.equals(key, true) }
    }
}
