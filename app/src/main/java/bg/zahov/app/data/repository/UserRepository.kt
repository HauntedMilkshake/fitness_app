package bg.zahov.app.data.repository

import bg.zahov.app.data.local.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUser(): Flow<User>
    suspend fun changeUserName(newUsername: String)
}