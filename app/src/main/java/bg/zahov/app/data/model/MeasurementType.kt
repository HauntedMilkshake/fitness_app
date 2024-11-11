package bg.zahov.app.data.model

/**
 * Defines various types of body measurements, each with an associated descriptive key.
 *
 * @property key A human-readable description of the measurement type.
 */
enum class MeasurementType(val key: String) {
    /** Measurement of body weight. */
    Weight("Weight"),

    /** Measurement of body fat percentage. */
    BodyFatPercentage("Body fat percentage"),

    /** Measurement of daily caloric intake. */
    CaloricIntake("Caloric intake"),

    /** Measurement of neck circumference. */
    Neck("Neck"),

    /** Measurement of shoulder width. */
    Shoulders("Shoulders"),

    /** Measurement of chest circumference. */
    Chest("Chest"),

    /** Measurement of left bicep circumference. */
    LeftBicep("Left bicep"),

    /** Measurement of right bicep circumference. */
    RightBicep("Right bicep"),

    /** Measurement of left forearm circumference. */
    LeftForearm("Left forearm"),

    /** Measurement of right forearm circumference. */
    RightForearm("Right forearm"),

    /** Measurement of waist circumference. */
    Waist("Waist"),

    /** Measurement of hip circumference. */
    Hips("Hips"),

    /** Measurement of left thigh circumference. */
    LeftThigh("Left thigh"),

    /** Measurement of right thigh circumference. */
    RightThigh("Right thigh"),

    /** Measurement of left calf circumference. */
    LeftCalf("Left calf"),

    /** Measurement of right calf circumference. */
    RightCalf("Right calf");

    /**
     * Companion object that provides utility functions for [MeasurementType].
     */
    companion object {
        /**
         * Finds the corresponding [MeasurementType] based on the given key.
         * Comparison is case-insensitive.
         *
         * @param key The key to look up.
         * @return The matching [MeasurementType], or `null` if no match is found.
         */
        fun fromKey(key: String): MeasurementType? =
            entries.firstOrNull { it.key.equals(key, true) }
    }
}
