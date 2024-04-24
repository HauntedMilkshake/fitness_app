package bg.zahov.app.data.repository

import bg.zahov.app.data.interfaces.MeasurementRepository
import bg.zahov.app.data.model.Measurement
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.data.model.Measurements
import bg.zahov.app.data.remote.FirestoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class MeasurementRepositoryImpl : MeasurementRepository {
    companion object {
        @Volatile
        private var instance: MeasurementRepositoryImpl? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: MeasurementRepositoryImpl().also { instance = it }
            }
    }

    private val firestore = FirestoreManager.getInstance()

//    override suspend fun getMeasurements(): Flow<Measurements> = firestore.getMeasurements()
    override suspend fun getMeasurement(type: MeasurementType) = firestore.getMeasurement(type)


    override suspend fun updateMeasurement(
        measurementType: MeasurementType,
        measurement: Measurement
    ) {
        firestore.upsertMeasurement(measurementType, measurement)
    }
}
