package bg.zahov.app.repositories

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
    suspend fun getUserHomeInfo(userId: String): Triple<String, Int, List<Workout>> {
        return realmInstance.getUserInformationForProfileFragment(userId)!!
    }
}