package bg.zahov.app.repository

import bg.zahov.app.backend.Exercise
import bg.zahov.app.backend.RealmManager
import bg.zahov.app.backend.Settings
import bg.zahov.app.backend.SyncManager
import bg.zahov.app.backend.User
import bg.zahov.app.backend.Workout
import com.google.firebase.auth.FirebaseAuth

//FIXME Check out SOLID principles - this implementation is a violation of the S - instead of
// operating only with User objects this class serves all kind of other types.
// Define repository interfaces for the distinct types in your domain and only use the interface type
// throughout the project - ideally concrete implementations should be only used during object instantiation
// A suitable set of repositories for this app would be UserRepository, WorkoutRepository (handles exercises and templates as well)
// and SettingsRepository. You can use the same Realm or Firestore infrastructure for all of them
class UserRepository(private var userId: String) {
    companion object {
        @Volatile
        private var repoInstance: UserRepository? = null
        // FIXME this will not return different instances for different userId
        fun getInstance(userId: String) = repoInstance ?: synchronized(this) {
            repoInstance ?: UserRepository(userId).also { repoInstance = it }
        }
    }

    private var realmInstance = RealmManager.getInstance()

    private var syncManager = SyncManager.getInstance(userId, realmInstance)
    suspend fun getUser() = realmInstance.getUser()
    suspend fun getSettings() = realmInstance.getSettings()

    suspend fun getTemplateExercises() = realmInstance.getTemplateExercises()
    suspend fun getAllWorkouts() = realmInstance.getAllWorkouts()
    suspend fun addWorkout(newWorkout: Workout) {
        realmInstance.addWorkout(newWorkout)
    }

    suspend fun getTemplateWorkouts() = realmInstance.getTemplateWorkouts()
    suspend fun changeUserName(newUserName: String) {
        realmInstance.changeUserName(newUserName)
    }

    suspend fun addExercise(newExercise: Exercise) {
        realmInstance.addExercise(newExercise)
    }

    suspend fun updateSetting(title: String, newValue: Any) {
        realmInstance.updateSetting(title, newValue)
    }

    //FIXME Realm is an implementation detail of your repository, you don't want to expose this method
    suspend fun createRealm(
        newUser: User,
        workouts: List<Workout?>?,
        exercises: List<Exercise?>?,
        settings: Settings,
    ) {
        realmInstance.createRealm(newUser, workouts, exercises, settings)
    }

    //FIXME Firestore is an implementation detail of your repository, you don't want to expose this method
    suspend fun createFirestore(user: User, settings: Settings) {
        syncManager.createFirestore(user, settings)
    }

    //FIXME Synchronization is a separate concern, this should not be part of the public repository interface
    suspend fun syncFromFirestore() {
        syncManager.initCaches()
        syncManager.syncFromFirestore()
    }

    //FIXME Synchronization is a separate concern, this should not be part of the public repository interface
    suspend fun periodicSync() {
        syncManager.initPeriodicSync()
    }

    suspend fun resetSettings() {
        realmInstance.resetSettings()
    }

    fun updateUser(newId: String) {
        userId = newId
        syncManager.updateUser(newId)
    }

    suspend fun deleteUser(auth: FirebaseAuth) {
        syncManager.deleteFirebaseUser(auth)
        realmInstance.deleteRealm()
    }

    //FIXME Realm is an implementation detail of your repository, you don't want to expose this method
    suspend fun deleteRealm() {
        realmInstance.deleteRealm()
    }
}