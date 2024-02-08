package bg.zahov.app.data.interfaces

import bg.zahov.app.data.model.User
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUser(): Flow<User>
    suspend fun changeUserName(newUsername: String): Task<Void>
}