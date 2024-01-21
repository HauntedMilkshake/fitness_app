package bg.zahov.app.data.repository

import bg.zahov.app.data.model.User
import bg.zahov.app.data.interfaces.UserRepository
import kotlinx.coroutines.flow.Flow

//FIXME Check out SOLID principles - this implementation is a violation of the S - instead of
// operating only with User objects this class serves all kind of other types.
// Define repository interfaces for the distinct types in your domain and only use the interface type
// throughout the project - ideally concrete implementations should be only used during object instantiation
// A suitable set of repositories for this app would be UserRepository, WorkoutRepository (handles exercises and templates as well)
// and SettingsRepository. You can use the same Realm or Firestore infrastructure for all of them
class UserRepositoryImpl : UserRepository {
    //Create custom type adapt
    //here cache if you want
    companion object {
        @Volatile
        private var instance: UserRepositoryImpl? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: UserRepositoryImpl().also { instance = it }
            }
    }

    override suspend fun getUser(): Flow<User> {
        TODO("Not yet implemented")
    }

    override suspend fun changeUserName(newUsername: String) {
        TODO("Not yet implemented")
    }
}