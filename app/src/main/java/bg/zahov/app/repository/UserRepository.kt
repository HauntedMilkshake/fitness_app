package bg.zahov.app.repository

import bg.zahov.app.realm_db.Exercise
import bg.zahov.app.realm_db.RealmManager
import bg.zahov.app.realm_db.Settings
import bg.zahov.app.realm_db.User
import bg.zahov.app.realm_db.Workout

class UserRepository(userId: String) {
    companion object {
        @Volatile
        private var repoInstance: UserRepository? = null
        fun getInstance(userId: String) = repoInstance ?: synchronized(this) {
            repoInstance ?: UserRepository(userId).also { repoInstance = it }
        }
    }

    private val realmInstance = RealmManager.getInstance(userId)
    suspend fun getTemplateExercises() = realmInstance.getTemplateExercises()
    suspend fun getSettings() = realmInstance.getSettings()

    suspend fun getUser() = realmInstance.getUser()
    suspend fun getAllWorkouts() = realmInstance.getAllWorkouts()

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

    suspend fun createRealm(newUser: User, workouts: List<Workout?>?, exercises: List<Exercise?>?, settings: Settings) {
        realmInstance.createRealm(newUser, workouts, exercises, settings)
    }

    suspend fun syncFromFirestore() {
        realmInstance.syncFromFirestore()
    }

    suspend fun syncToFirestore(user: User, workouts: List<Workout?>?, exercises: List<Exercise?>?, settings: Settings) {
        realmInstance.syncToFirestore(user, workouts, exercises, settings)
    }

    suspend fun resetSettings() {
        realmInstance.resetSettings()
    }

}