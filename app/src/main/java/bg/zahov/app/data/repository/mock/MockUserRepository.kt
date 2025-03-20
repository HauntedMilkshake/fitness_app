package bg.zahov.app.data.repository.mock

import bg.zahov.app.data.model.Measurement
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.data.model.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class MockUserRepository @Inject constructor() : bg.zahov.app.data.interfaces.UserRepository {
    private val userFlow = MutableStateFlow<User>(User(name = "test"))
    override suspend fun getUser(): Flow<User> = userFlow

    override suspend fun changeUserName(newUsername: String): Task<Void> {
        userFlow.value.copy(newUsername)
        return Tasks.forResult(null)
    }

    override suspend fun updateMeasurement(
        measurementType: MeasurementType,
        measurement: Measurement,
    ) {
        /* TODO() */
    }
}