package bg.zahov.app.data.interfaces

import bg.zahov.app.data.model.Measurement
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.data.model.Measurements
import kotlinx.coroutines.flow.Flow

interface MeasurementProvider {
    suspend fun getMeasurements(): Flow<Measurements>
    suspend fun getMeasurement(type: MeasurementType): Flow<Measurements>
    suspend fun getSelectedMeasurement(): Flow<Measurements>
    suspend fun updateMeasurement(measurementType: MeasurementType, measurement: Measurement)
    suspend fun selectMeasure(type: MeasurementType)
}