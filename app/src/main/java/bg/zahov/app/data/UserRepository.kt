package bg.zahov.app.data

import bg.zahov.app.data.local.RealmManager
import bg.zahov.app.data.remote.SyncManager

//FIXME Check out SOLID principles - this implementation is a violation of the S - instead of
// operating only with User objects this class serves all kind of other types.
// Define repository interfaces for the distinct types in your domain and only use the interface type
// throughout the project - ideally concrete implementations should be only used during object instantiation
// A suitable set of repositories for this app would be UserRepository, WorkoutRepository (handles exercises and templates as well)
// and SettingsRepository. You can use the same Realm or Firestore infrastructure for all of them
class UserRepository(private val userId: String) {
    companion object {
        @Volatile
        private var instances: MutableMap<String, UserRepository> = mutableMapOf()

        fun getInstance(userId: String) = instances.getOrPut(userId) { UserRepository(userId) }
    }

    private var realm = RealmManager.getInstance()

    private var syncManager = SyncManager.getInstance(userId, realm)

    suspend fun getUser() = realm.getUser()

    suspend fun changeUserName(newUserName: String) {
        realm.changeUserName(newUserName)
    }
//    suspend fun deleteUser(auth: FirebaseAuth) {
//        syncManager.deleteFirebaseUser(auth)
//        realm.deleteRealm()
//    }
}