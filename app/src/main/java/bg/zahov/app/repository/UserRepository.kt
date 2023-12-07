package bg.zahov.app.repository

import bg.zahov.app.realm_db.Exercise
import bg.zahov.app.realm_db.RealmManager
import bg.zahov.app.realm_db.User

class UserRepository (userId: String){
    companion object {
        @Volatile
        private var repoInstance: UserRepository? = null
        fun getInstance(userId: String) = repoInstance ?: synchronized(this) {
            repoInstance ?: UserRepository(userId).also { repoInstance = it }
        }
    }

    private val realmInstance = RealmManager.getInstance(userId)
    suspend fun getExercises() = realmInstance.getExercises()
    suspend fun getSettings() = realmInstance.getSettings()

    suspend fun getUser() = realmInstance.getUser()
    suspend fun changeUserName(newUserName: String){
        realmInstance.changeUserName(newUserName)
    }
    suspend fun addExercise(newExercise: Exercise){
        realmInstance.addExercise(newExercise)
    }
    suspend fun writeNewSettings(title: String, newValue: Any){ realmInstance.writeNewSetting(title, newValue) }

    suspend fun createRealm(newUser: User) {
        realmInstance.createRealm(newUser)
    }
    suspend fun createRealmFromFirestore(){
        realmInstance.createRealmFromFirestore()
    }
    suspend fun syncFromRealmToFirestore(){
       realmInstance.syncFromRealmToFirestore()
    }
    suspend fun resetSettings(){
        realmInstance.resetSettings()
    }
    fun doesUserHaveRealm(): Boolean = realmInstance.doesUserHaveLocalRealmFile()
}