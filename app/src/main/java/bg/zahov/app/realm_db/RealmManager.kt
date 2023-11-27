package bg.zahov.app.realm_db

import android.util.Log
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

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

    private fun getConfig(userId: String): RealmConfiguration.Builder {
        val config = RealmConfiguration.Builder(
            setOf(
                User::class,
                Workout::class,
                Exercise::class,
                Sets::class
            )
        )
        config.schemaVersion(1)
        config.deleteRealmIfMigrationNeeded()
        config.name("$userId.realm")
        return config
    }

    //TODO(Fix issue where if we haven't gone through signup we don't have realm file(should be fixed when firestore sync is added)
    suspend fun createRealm(userId: String, uName: String) {
        try {
            realmInstance = Realm.open(getConfig(userId).build())
            realmInstance?.write {
                copyToRealm(User().apply {
                    username = uName
                    numberOfWorkouts = 0
                })
            }
        } catch (e: Exception) {
            Log.e("Realm start error", e.toString())
        } finally {
            //might be redundant based on docs
            realmInstance?.close()
        }
    }

    private suspend fun <T> withRealm(userId: String, block: suspend (Realm) -> T): T {
        return suspendCancellableCoroutine { cancellableContinuation ->
            val realmConfig = getConfig(userId).build()

            val job = Job()

            val realm = Realm.open(realmConfig)
            cancellableContinuation.invokeOnCancellation {
                job.cancel()
                realm.close()
            }

            CoroutineScope(Dispatchers.IO + job).launch {
                val result = block(realm)
                cancellableContinuation.resume(result) {
                    cancellableContinuation.resumeWithException(it)
                }
            }
        }
    }

    suspend fun getUserInformationForProfileFragment(userId: String): Triple<String, Int, List<Workout>>? {
        return withRealm(userId) { realm ->
            val user = realm.query<User>().first().find()
            user?.let {
                //TODO retrieval of workouts should be done via flow for efficiency
                Triple(it.username.orEmpty(), it.numberOfWorkouts ?: 0, it.workouts.toList())
            }
        }
    }
    suspend fun changeUserName(userId: String, newUserName: String){
        withRealm(userId){
            val user = it.query<User>().find().first()
            it.write {
                findLatest(user)?.let{liveUser ->
                    liveUser.username = newUserName
                }
            }
        }
    }
    suspend fun getUsername(userId: String): String? {
        return withRealm(userId){realm ->
            val user = realm.query<User>().first().find()
            user?.let{
                Log.d("Username", it.username!!)
                it.username
            }
        }
    }

    //template for async finding all objects of a type(eg all workouts of a user when needed) needs small adjutments
//    suspend fun getUserInformation(userId: String): AnythingThatAUserHasALotOf?{
//        withRealm(userId){ realm ->
//            val userFlow: Flow<ResultsChange<User>> = realm.query<User>().asFlow()
//            val asyncCall: Deferred<Unit> = coroutineScope{
//                async {
//                    userFlow.collect{ results ->
//                        when(results){
//                            is InitialResults<User> ->{
//                                return results.list
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
}

