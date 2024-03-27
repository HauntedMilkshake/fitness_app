package bg.zahov.app.data.provider

import bg.zahov.app.data.interfaces.MeasurementProvider
import bg.zahov.app.data.model.Measurement
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.data.model.Measurements
import bg.zahov.app.data.repository.MeasurementRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class MeasurementProviderImpl: MeasurementProvider {

    companion object {
        @Volatile
        private var instance: MeasurementProviderImpl? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: MeasurementProviderImpl().also { instance = it }
            }
    }


    private val measurementRepo = MeasurementRepositoryImpl.getInstance()
    private val _selectedMeasurement = MutableSharedFlow<Measurements>()
    private val selectedMeasurement: SharedFlow<Measurements> = _selectedMeasurement

    override suspend fun getMeasurements(): Flow<Measurements> = measurementRepo.getMeasurements()

    override suspend fun getMeasurement(type: MeasurementType): Flow<Measurements> = measurementRepo.getMeasurement(type)

    override suspend fun getSelectedMeasurement(): Flow<Measurements> = selectedMeasurement

    override suspend fun updateMeasurement(
        measurementType: MeasurementType,
        measurement: Measurement
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun selectMeasure(type: MeasurementType) {
        getMeasurement(type).collect {
            _selectedMeasurement.emit(it)
        }
    }
}