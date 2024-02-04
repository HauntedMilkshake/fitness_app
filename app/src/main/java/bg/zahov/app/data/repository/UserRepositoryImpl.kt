package bg.zahov.app.data.repository

import bg.zahov.app.data.model.User
import bg.zahov.app.data.interfaces.UserRepository
import bg.zahov.app.data.remote.FirestoreManager
import kotlinx.coroutines.flow.Flow

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

    override suspend fun getUser(): Flow<User> {
        return firestore.getUser()
    }

    override suspend fun changeUserName(newUsername: String) = firestore.updateUsername(newUsername)
}