package bg.zahov.app.data.repository

import bg.zahov.app.data.interfaces.UserRepository
import bg.zahov.app.data.model.Measurement
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.data.model.User
import bg.zahov.app.data.remote.FirestoreManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val firestore: FirestoreManager) :
    UserRepository {

    override suspend fun getUser(): Flow<User> = firestore.getUser()

    override suspend fun changeUserName(newUsername: String) = firestore.updateUsername(newUsername)
    override suspend fun updateMeasurement(
        measurementType: MeasurementType,
        measurement: Measurement,
    ) {
        firestore.upsertMeasurement(measurementType, measurement)
    }
}