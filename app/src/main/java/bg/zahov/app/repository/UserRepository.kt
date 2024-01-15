package bg.zahov.app.repository

import bg.zahov.app.backend.Exercise
import bg.zahov.app.backend.RealmManager
import bg.zahov.app.backend.Settings
import bg.zahov.app.backend.SyncManager
import bg.zahov.app.backend.User
import bg.zahov.app.backend.Workout
import com.google.firebase.auth.FirebaseAuth

class UserRepository(private var userId: String) {
    companion object {
        @Volatile
        private var repoInstance: UserRepository? = null
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

    suspend fun createRealm(
        newUser: User,
        workouts: List<Workout?>?,
        exercises: List<Exercise?>?,
        settings: Settings,
    ) {
        realmInstance.createRealm(newUser, workouts, exercises, settings)
    }

    suspend fun createFirestore(user: User, settings: Settings) {
        syncManager.createFirestore(user, settings)
    }

    suspend fun syncFromFirestore() {
        syncManager.initCaches()
        syncManager.syncFromFirestore()
    }

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

    suspend fun deleteRealm() {
        realmInstance.deleteRealm()
    }
}