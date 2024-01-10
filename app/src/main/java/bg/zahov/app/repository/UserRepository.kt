package bg.zahov.app.repository

import android.util.Log
import bg.zahov.app.backend.Exercise
import bg.zahov.app.backend.RealmManager
import bg.zahov.app.backend.Settings
import bg.zahov.app.backend.SyncManager
import bg.zahov.app.backend.User
import bg.zahov.app.backend.Workout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private var userId: String) {
    companion object {
        @Volatile
        private var repoInstance: UserRepository? = null
        fun getInstance(userId: String) = repoInstance ?: synchronized(this) {
            repoInstance ?: UserRepository(userId).also { repoInstance = it }
        }
    }

    private var realmInstance: RealmManager? = RealmManager.getInstance()

    private var syncManager: SyncManager? = SyncManager.getInstance(userId, realmInstance!!)
    suspend fun getTemplateExercises() = realmInstance?.getTemplateExercises()
    suspend fun getSettings() = realmInstance?.getSettings()

    suspend fun getUser() = realmInstance?.getUser()
    suspend fun getAllWorkouts() = realmInstance?.getAllWorkouts()
    suspend fun addWorkout(newWorkout: Workout){
        realmInstance?.addWorkout(newWorkout)
    }

    suspend fun getTemplateWorkouts() = realmInstance?.getTemplateWorkouts()
    suspend fun changeUserName(newUserName: String) {
        realmInstance?.changeUserName(newUserName)
    }

    suspend fun addExercise(newExercise: Exercise) {
        realmInstance?.addExercise(newExercise)
    }

    suspend fun updateSetting(title: String, newValue: Any) {
        realmInstance?.updateSetting(title, newValue)
    }

    suspend fun createRealm(newUser: User, workouts: List<Workout?>?, exercises: List<Exercise?>?, settings: Settings) {
        Log.d("ID", "IN REPO -> $userId")
        realmInstance = RealmManager.getInstance()
        realmInstance?.createRealm(newUser, workouts, exercises, settings)
    }
    fun updateUser(newId: String){
        userId = newId
        syncManager!!.updateUser(newId)
    }
    suspend fun createFirestore(user: User, settings: Settings) {
        syncManager?.updateUser(userId)
        syncManager?.createFirestore(user, settings)
    }
    suspend fun syncFromFirestore() {
        syncManager?.syncFromFirestore()
    }
    suspend fun periodicSync() {
        syncManager?.initPeriodicSync()
    }
    suspend fun resetSettings() {
        realmInstance?.resetSettings()
    }

    suspend fun deleteUser(auth: FirebaseAuth){
        syncManager?.deleteFirebaseUser(auth)
        realmInstance?.deleteRealm()
    }
    suspend fun deleteRealm(){
        realmInstance?.deleteRealm()
    }
    fun isSyncRequired() = realmInstance?.doesUserHaveRealm() ?: false
}