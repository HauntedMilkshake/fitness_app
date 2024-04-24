package bg.zahov.app.data.provider

import bg.zahov.app.data.interfaces.MeasurementProvider
import bg.zahov.app.data.model.Measurement
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.data.model.Measurements
import bg.zahov.app.data.repository.MeasurementRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MeasurementProviderImpl : MeasurementProvider {

    companion object {
        @Volatile
        private var instance: MeasurementProviderImpl? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: MeasurementProviderImpl().also { instance = it }
            }
    }


    private val measurementRepo = MeasurementRepositoryImpl.getInstance()
    private val _selectedMeasurement = MutableStateFlow(Measurements())
    private val selectedMeasurement: StateFlow<Measurements> = _selectedMeasurement
    private var selectedMeasurementValue = Measurements()

    override suspend fun getMeasurement(type: MeasurementType) = measurementRepo.getMeasurement(type)

    override suspend fun getSelectedMeasurement(): Flow<Measurements> = selectedMeasurement

    override suspend fun updateMeasurement(
        measurementType: MeasurementType,
        measurement: Measurement,
    ) {
        measurementRepo.updateMeasurement(measurementType, measurement)
        addInputToSelectedMeasurement(measurementType, measurement)
    }

    override suspend fun selectMeasure(type: MeasurementType) {
        val measurement = getMeasurement(type)
        _selectedMeasurement.value = measurement
        selectedMeasurementValue = measurement
    }

    override suspend fun addInputToSelectedMeasurement(
        type: MeasurementType,
        measurement: Measurement,
    ) {
        val newList = mutableListOf<Measurement>()
        val existingMeasurements = selectedMeasurementValue.measurements[type]

        existingMeasurements?.let { newList.addAll(it) }
        newList.add(measurement)

        _selectedMeasurement.value = Measurements(
            measurements = selectedMeasurementValue.measurements + (type to newList)
        )
    }

}