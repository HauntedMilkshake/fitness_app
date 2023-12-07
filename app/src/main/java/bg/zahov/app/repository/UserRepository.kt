package bg.zahov.app.repository

import androidx.lifecycle.LiveData
import bg.zahov.app.realm_db.Exercise
import bg.zahov.app.realm_db.RealmManager
import bg.zahov.app.realm_db.User
import bg.zahov.app.realm_db.Workout
import io.realm.kotlin.ext.asFlow
import io.realm.kotlin.notifications.ObjectChange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class UserRepository (userId: String){
    companion object {
        @Volatile
        private var repoInstance: UserRepository? = null
        fun getInstance(userId: String) = repoInstance ?: synchronized(this) {
            repoInstance ?: UserRepository(userId).also { repoInstance = it }
        }
    }
    private val realmInstance = RealmManager.getInstance(userId)
//    suspend fun getUsername(): Flow<ObjectChange<String>> {
//        return flow {
//            realmInstance.getUser()
//        }
//    }
//    suspend fun getUserExercises() = realmInstance.getUserExercises()
//    suspend fun getUserSettings() = realmInstance.getUserSettings()

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