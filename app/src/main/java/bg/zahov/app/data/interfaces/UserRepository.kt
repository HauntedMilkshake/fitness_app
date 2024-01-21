package bg.zahov.app.data.interfaces

import bg.zahov.app.data.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUser(): Flow<User>
    suspend fun changeUserName(newUsername: String)
}