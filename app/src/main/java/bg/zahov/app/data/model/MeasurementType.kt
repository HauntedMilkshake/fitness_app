package bg.zahov.app.data.model

/**
 * Enum class representing different types of body measurements.
 * Each measurement type has an associated key that describes the measurement in a user-friendly format.
 *
 * @property key A descriptive name for the measurement type.
 */
enum class MeasurementType(val key: String) {
    /** Represents the user's weight measurement. */
    Weight("Weight"),

    /** Represents the user's body fat percentage measurement. */
    BodyFatPercentage("Body fat percentage"),

    /** Represents the user's caloric intake measurement. */
    CaloricIntake("Caloric intake"),

    /** Represents the measurement of the user's neck circumference. */
    Neck("Neck"),

    /** Represents the measurement of the user's shoulder width. */
    Shoulders("Shoulders"),

    /** Represents the measurement of the user's chest circumference. */
    Chest("Chest"),

    /** Represents the measurement of the user's left bicep circumference. */
    LeftBicep("Left bicep"),

    /** Represents the measurement of the user's right bicep circumference. */
    RightBicep("Right bicep"),

    /** Represents the measurement of the user's left forearm circumference. */
    LeftForearm("Left forearm"),

    /** Represents the measurement of the user's right forearm circumference. */
    RightForearm("Right forearm"),

    /** Represents the measurement of the user's waist circumference. */
    Waist("Waist"),

    /** Represents the measurement of the user's hip circumference. */
    Hips("Hips"),

    /** Represents the measurement of the user's left thigh circumference. */
    LeftThigh("Left thigh"),

    /** Represents the measurement of the user's right thigh circumference. */
    RightThigh("Right thigh"),

    /** Represents the measurement of the user's left calf circumference. */
    LeftCalf("Left calf"),

    /** Represents the measurement of the user's right calf circumference. */
    RightCalf("Right calf"),

    /** Represents the measurement of the user's reps. */
    Reps("Reps");
}