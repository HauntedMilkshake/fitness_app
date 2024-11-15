package bg.zahov.app.data.model

/**
 * Enum class representing different categories of exercise equipment or exercise types.
 *
 * @property key A string identifier for each category.
 */
enum class Category(val key: String) {

    /** Barbell category, typically used for exercises involving a barbell. */
    Barbell("Barbell"),

    /** Dumbbell category, for exercises using dumbbells. */
    Dumbbell("Dumbbell"),

    /** Machine category, for exercises performed on weight machines. */
    Machine("Machine"),

    /** AdditionalWeight category, for exercises requiring extra weights beyond typical equipment. */
    AdditionalWeight("AdditionalWeight"),

    /** Cable category, for exercises using cable machines. */
    Cable("Cable"),

    /** None category, for exercises that do not require equipment. */
    None("None"),

    /** AssistedWeight category, for exercises involving assisted weight (e.g., assisted pull-ups). */
    AssistedWeight("AssistedWeight"),

    /** RepsOnly category, for exercises based solely on repetitions. */
    RepsOnly("RepsOnly"),

    /** Cardio category, for exercises aimed at cardiovascular endurance. */
    Cardio("Cardio"),

    /** Timed category, for exercises measured by time duration rather than reps or weight. */
    Timed("Timed");
}
