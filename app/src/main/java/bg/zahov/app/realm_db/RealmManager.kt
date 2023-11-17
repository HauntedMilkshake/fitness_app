package bg.zahov.app.realm_db

import android.util.Log
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.runBlocking

class RealmManager private constructor() {

    companion object {
        @Volatile
        private var instance: RealmManager? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: RealmManager().also { instance = it }
            }
    }

    private var realmInstance: Realm? = null

    fun createRealm(userId: String) {
        try {
            val config = RealmConfiguration.Builder(setOf(User::class, Workout::class, Exercise::class, Sets::class))
            config.schemaVersion(1)
            config.deleteRealmIfMigrationNeeded()
            config.name("$userId.realm")
            realmInstance = Realm.open(config.build())
        } catch (e: Exception) {
            Log.e("Realm start error", e.toString())
        }
    }

//    fun <T : RealmObject> createInRealm(objectToCopyRealm: T) {
//        realmInstance?.write {
//            copyToRealm(objectToCopyRealm)
//        }
//    }
}
