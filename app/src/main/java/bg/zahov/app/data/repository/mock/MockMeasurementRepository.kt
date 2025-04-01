package bg.zahov.app.data.repository.mock

import bg.zahov.app.data.interfaces.MeasurementRepository
import bg.zahov.app.data.model.Measurement
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.data.model.Measurements
import java.time.LocalDateTime
import javax.inject.Inject

class MockMeasurementRepository @Inject constructor() : MeasurementRepository {

    private val measurements = mapOf(
        MeasurementType.Weight to mutableListOf(
            Measurement(LocalDateTime.now().minusDays(1), 70.0),
            Measurement(LocalDateTime.now().minusDays(7), 71.5),
        ),
        MeasurementType.BodyFatPercentage to mutableListOf(
            Measurement(LocalDateTime.now().minusDays(1), 15.0),
            Measurement(LocalDateTime.now().minusDays(7), 15.3),
        ),
        MeasurementType.CaloricIntake to mutableListOf(
            Measurement(LocalDateTime.now().minusDays(1), 2500.0),
            Measurement(LocalDateTime.now().minusDays(2), 2300.0),
        ),
        MeasurementType.Neck to mutableListOf(
            Measurement(LocalDateTime.now().minusDays(1), 40.0),
            Measurement(LocalDateTime.now().minusDays(7), 39.5),
        ),
        MeasurementType.Shoulders to mutableListOf(
            Measurement(LocalDateTime.now().minusDays(1), 120.0),
            Measurement(LocalDateTime.now().minusDays(7), 119.0),
        ),
        MeasurementType.Chest to mutableListOf(
            Measurement(LocalDateTime.now().minusDays(1), 105.0),
            Measurement(LocalDateTime.now().minusDays(7), 104.5),
        ),
        MeasurementType.LeftBicep to mutableListOf(
            Measurement(LocalDateTime.now().minusDays(1), 36.0),
            Measurement(LocalDateTime.now().minusDays(7), 35.8),
        ),
        MeasurementType.RightBicep to mutableListOf(
            Measurement(LocalDateTime.now().minusDays(1), 36.5),
            Measurement(LocalDateTime.now().minusDays(7), 36.0),
        ),
        MeasurementType.LeftForearm to mutableListOf(
            Measurement(LocalDateTime.now().minusDays(1), 28.0),
            Measurement(LocalDateTime.now().minusDays(7), 27.5),
        ),
        MeasurementType.RightForearm to mutableListOf(
            Measurement(LocalDateTime.now().minusDays(1), 28.5),
            Measurement(LocalDateTime.now().minusDays(7), 28.0),
        ),
        MeasurementType.Waist to mutableListOf(
            Measurement(LocalDateTime.now().minusDays(1), 85.0),
            Measurement(LocalDateTime.now().minusDays(7), 86.0),
        ),
        MeasurementType.Hips to mutableListOf(
            Measurement(LocalDateTime.now().minusDays(1), 95.0),
            Measurement(LocalDateTime.now().minusDays(7), 95.5),
        ),
        MeasurementType.LeftThigh to mutableListOf(
            Measurement(LocalDateTime.now().minusDays(1), 60.0),
            Measurement(LocalDateTime.now().minusDays(7), 60.5),
        ),
        MeasurementType.RightThigh to mutableListOf(
            Measurement(LocalDateTime.now().minusDays(1), 60.5),
            Measurement(LocalDateTime.now().minusDays(7), 60.8),
        ),
        MeasurementType.LeftCalf to mutableListOf(
            Measurement(LocalDateTime.now().minusDays(1), 40.0),
            Measurement(LocalDateTime.now().minusDays(7), 39.8),
        ),
        MeasurementType.RightCalf to mutableListOf(
            Measurement(LocalDateTime.now().minusDays(1), 40.5),
            Measurement(LocalDateTime.now().minusDays(7), 40.0),
        ),
        MeasurementType.Reps to mutableListOf(
            Measurement(LocalDateTime.now().minusDays(1), 10.0),
            Measurement(LocalDateTime.now().minusDays(7), 12.0),
        )
    )

    override suspend fun getMeasurement(type: MeasurementType) =
        Measurements(measurements = mapOf(type to (measurements[type] ?: listOf())))

    override suspend fun updateMeasurement(
        measurementType: MeasurementType,
        measurement: Measurement,
    ) {
        measurements[measurementType]?.add(measurement)
    }

}