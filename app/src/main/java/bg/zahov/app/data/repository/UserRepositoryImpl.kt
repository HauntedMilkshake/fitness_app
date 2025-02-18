package bg.zahov.app.data.repository

import bg.zahov.app.data.model.User
import bg.zahov.app.data.interfaces.UserRepository
import bg.zahov.app.data.model.Measurement
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.data.remote.FirestoreManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl : UserRepository {
    companion object {
        @Volatile
        private var instance: UserRepositoryImpl? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: UserRepositoryImpl().also { instance = it }
            }
    }

    private val firestore = FirestoreManager.getInstance()

    override suspend fun getUser(): Flow<User> = firestore.getUser()

    override suspend fun changeUserName(newUsername: String) = firestore.updateUsername(newUsername)
    override suspend fun updateMeasurement(
        measurementType: MeasurementType,
        measurement: Measurement,
    ) {
        firestore.upsertMeasurement(measurementType, measurement)
    }
}