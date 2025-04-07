package bg.zahov.app.data.repository

import bg.zahov.app.data.interfaces.MeasurementRepository
import bg.zahov.app.data.model.Measurement
import bg.zahov.app.data.model.MeasurementType
import javax.inject.Inject

/**
 * Implementation of the [MeasurementRepository] interface, responsible for interacting with the Firestore
 * database to fetch and update measurement data.
 *
 * This class uses the [FirestoreManager] to retrieve and store measurement data in a Firestore-based database.
 */
class MeasurementRepositoryImpl @Inject constructor(private val firestore: FirestoreManager) :
    MeasurementRepository {
    /**
     * Retrieves a specific measurement for the given [MeasurementType] from the Firestore database.
     *
     * @param type The type of the measurement to fetch (e.g., weight, body fat percentage).
     * @return The measurement data for the specified type.
     */
    override suspend fun getMeasurement(type: MeasurementType) = firestore.getMeasurement(type)

    /**
     * Updates or inserts a new measurement of the specified [MeasurementType] in the Firestore database.
     * This function ensures that the measurement data is either updated or added if it doesn't exist.
     *
     * @param measurementType The type of measurement to update or insert.
     * @param measurement The measurement data to upsert into the database.
     */
    override suspend fun updateMeasurement(
        measurementType: MeasurementType,
        measurement: Measurement,
    ) {
        firestore.upsertMeasurement(measurementType, measurement)
    }
}
