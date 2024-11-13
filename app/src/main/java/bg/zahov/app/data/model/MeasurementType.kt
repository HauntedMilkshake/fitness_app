package bg.zahov.app.data.model

enum class MeasurementType(val key: String) {
    Weight("Weight"),
    BodyFatPercentage("Body fat percentage"),
    CaloricIntake("Caloric intake"),
    Neck("Neck"),
    Shoulders("Shoulders"),
    Chest("Chest"),
    LeftBicep("Left bicep"),
    RightBicep("Right bicep"),
    LeftForearm("Left forearm"),
    RightForearm("Right forearm"),
    Waist("Waist"),
    Hips("Hips"),
    LeftThigh("Left thigh"),
    RightThigh("Right thigh"),
    LeftCalf("Left calf"),
    RightCalf("Right calf");
}