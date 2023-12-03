package bg.zahov.app.repository

import bg.zahov.app.realm_db.Exercise
import bg.zahov.app.realm_db.RealmManager
import bg.zahov.app.realm_db.Workout

class UserRepository private constructor(){
    companion object {
        @Volatile
        private var repoInstance: UserRepository? = null
        fun getInstance() = repoInstance ?: synchronized(this) {
            repoInstance ?: UserRepository().also { repoInstance = it }
        }
    }
    private val realmInstance = RealmManager.getInstance()
    suspend fun getUserHomeInfo(): Triple<String, Int, List<Workout>> = realmInstance.getUserInformationForProfileFragment()
    suspend fun getUsername(): String = realmInstance.getUsername()
    suspend fun getUserExercises() = realmInstance.getUserExercises()
    suspend fun getUserSettings() = realmInstance.getUserSettings()
    suspend fun changeUserName(newUserName: String){
        realmInstance.changeUserName(newUserName)
    }
    suspend fun addExercise(newExercise: Exercise){
        realmInstance.addExercise(newExercise)
    }
    suspend fun writeNewSettings(title: String, newValue: Any){
        realmInstance.writeNewSettings(title, newValue)
    }
    suspend fun refreshSettings(){
        realmInstance.refreshSettings()
    }

}