package bg.zahov.app.data.interfaces

import bg.zahov.app.data.model.Measurement
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.data.model.Measurements

interface MeasurementRepository {
    suspend fun getMeasurement(type: MeasurementType): Measurements
    suspend fun updateMeasurement(measurementType: MeasurementType, measurement: Measurement)
}