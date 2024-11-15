package bg.zahov.app.data.model

import bg.zahov.fitness.app.R

/**
 * Enum class representing various body parts for exercises, each associated with a name and an icon.
 *
 * @property body A string representation of the body part name.
 * @property image An integer resource ID for the drawable icon associated with the body part.
 */
enum class BodyPart(val body: String, val image: Int) {

    /** Core body part, typically including abs and obliques. */
    Core("Core", R.drawable.ic_abs),

    /** Arms body part, including biceps, triceps, and forearms. */
    Arms("Arms", R.drawable.ic_arms),

    /** Back body part, covering muscles like lats and lower back. */
    Back("Back", R.drawable.ic_back),

    /** Chest body part, including the pectoral muscles. */
    Chest("Chest", R.drawable.ic_chest),

    /** Legs body part, including quadriceps, hamstrings, calves, and glutes. */
    Legs("Legs", R.drawable.ic_legs),

    /** Shoulders body part, including the deltoids and upper traps. */
    Shoulders("Shoulders", R.drawable.ic_shoulders),

    /** Other body parts not covered by specific groups. */
    Other("Other", R.drawable.ic_olympic),

    /** Olympic exercises, typically referring to lifts like clean and jerk or snatch. */
    Olympic("Olympic", R.drawable.ic_olympic);

    companion object {
        /**
         * Finds a [BodyPart] from the given key.
         *
         * @param key The string representation of the body part name.
         * @return The matching [BodyPart] instance, or `null` if no match is found.
         */
        fun fromKey(key: String) = entries.firstOrNull { it.body == key }
    }
}