package bg.zahov.app.data.provider

import android.util.Log
import bg.zahov.app.data.interfaces.MeasurementProvider
import bg.zahov.app.data.model.Measurement
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.data.model.Measurements
import bg.zahov.app.data.repository.MeasurementRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update

/**
 * Implementation of the [MeasurementProvider] interface that provides measurement data retrieval,
 * selection, and updating functionalities.
 *
 * This class is responsible for managing and storing selected measurements and interacting with
 * the underlying repository to fetch and update measurements.
 */
class MeasurementProviderImpl : MeasurementProvider {

    companion object {
        @Volatile
        private var instance: MeasurementProviderImpl? = null

        /**
         * Returns the singleton instance of [MeasurementProviderImpl].
         * This ensures that only one instance of the provider exists throughout the application.
         */
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: MeasurementProviderImpl().also { instance = it }
            }
    }

    private val measurementRepo = MeasurementRepositoryImpl.getInstance()
    private val _selectedMeasurement = MutableStateFlow(Measurements())
    private val selectedMeasurement: StateFlow<Measurements> = _selectedMeasurement
    private var selectedMeasurementValue = Measurements()

    /**
     * Fetches the measurement for the specified [MeasurementType].
     *
     * @param type The type of measurement to fetch (e.g., weight, body fat percentage).
     * @return The measurement of the specified type.
     */
    override suspend fun getMeasurement(type: MeasurementType) =
        measurementRepo.getMeasurement(type)

    /**
     * Observes the selected measurement data, emitting the current state of the selected measurements.
     *
     * @return A [Flow] that emits the current state of selected measurements.
     */
    override suspend fun getSelectedMeasurement(): Flow<Measurements> = selectedMeasurement

    /**
     * Updates the measurement of the specified [MeasurementType] with the provided [Measurement].
     * This operation will update both the repository and the in-memory selected measurement data.
     *
     * @param measurementType The type of the measurement to update.
     * @param measurement The new measurement data to store.
     */
    override suspend fun updateMeasurement(
        measurementType: MeasurementType,
        measurement: Measurement,
    ) {
        measurementRepo.updateMeasurement(measurementType, measurement)
        addInputToSelectedMeasurement(measurementType, measurement)
    }

    /**
     * Selects a measurement of the specified [MeasurementType] and sets it as the current selected measurement.
     * The selected measurement will be stored in the in-memory state.
     *
     * @param type The type of the measurement to select.
     */
    override suspend fun selectMeasure(type: MeasurementType) {
        val measurement = getMeasurement(type)
        _selectedMeasurement.value = measurement
        selectedMeasurementValue = measurement
    }

    /**
     * Adds a new [Measurement] to the list of measurements for the specified [MeasurementType].
     * This will append the new measurement to the existing measurements and update the in-memory state.
     *
     * @param type The type of measurement to add to.
     * @param measurement The new measurement to add.
     */
    override suspend fun addInputToSelectedMeasurement(
        type: MeasurementType,
        measurement: Measurement,
    ) {
        val newList = mutableListOf<Measurement>()
        val existingMeasurements = selectedMeasurementValue.measurements[type]

        // Add existing measurements if present.
        existingMeasurements?.let { newList.addAll(it) }
        newList.add(measurement)

        // Update the selected measurement with the new list of measurements.
        _selectedMeasurement.value = Measurements(
            measurements = selectedMeasurementValue.measurements + (type to newList)
        )
    }
}