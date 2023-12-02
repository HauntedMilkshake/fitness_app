package bg.zahov.app.realm_db

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
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
    private val database = FirebaseFirestore.getInstance()
    private val realmMutex = Mutex()

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
            realmMutex.withLock{
                realmInstance = Realm.open(getConfig(userId).build())
                realmInstance?.write {
                    copyToRealm(User().apply {
                        username = uName
                        numberOfWorkouts = 0
                    })
                }
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
                realmMutex.withLock{
                    if (!File(realmConfig.path).exists()) { //would be good to get this out in a different function
//                        if(FirebaseAuth.getInstance().currentUser == null){
                        database.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).get()
                            .addOnSuccessListener { document ->
                                if(document.exists()){
                                    launch{
                                    createRealm(userId, document.getString("username")!!)
                                    }
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Firestore", "Error getting document", exception)
                            }
//                    }
                    // If it doesn't exist, create the Realm
                }
                    val result = block(realm)
                    cancellableContinuation.resume(result) {
                        cancellableContinuation.resumeWithException(it)
                    }
                }
            }
        }
    }
//TODO(Sync to firestore is needed asap literally)
    suspend fun getUserInformationForProfileFragment(userId: String): Triple<String?, Int, List<Workout>> {
        return withRealm(userId) { realm ->
            val user = realm.query<User>().find().first()
                // list is empty
            user.let {
                //TODO retrieval of workouts should be done via flow for efficiency
                Triple(it.username!!, it.numberOfWorkouts!!, it.workouts.toList())
            }
                //?: Triple(null, 0, listOf())
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
            val user = realm.query<User>().find().first()
            user.let{
                Log.d("Username", it.username!!)
                it.username
            }
        }
    }
    suspend fun addExercise(userId: String, newExercise: Exercise){
        withRealm(userId){realm ->
            val user = realm.query<User>().find().first()
            realm.write {
                findLatest(user)?.let { liveUser ->
                    liveUser.customExercises.add(newExercise)
                    Log.d("Success", "Added new exercise to db")
                }
            }
        }
    }
    suspend fun getUserExercises(userId: String): List<Exercise>{
        return withRealm(userId){realm ->
            realm.query<User>().find().first().let{
                it.customExercises
            }
        }
    }
//    suspend fun syncToRealm(){
//
//    }
//    suspend fun updateRealmFromSync(){
//
//    }

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

