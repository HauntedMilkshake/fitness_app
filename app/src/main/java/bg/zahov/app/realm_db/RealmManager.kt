package bg.zahov.app.realm_db

import android.util.Log
import bg.zahov.app.data.Language
import bg.zahov.app.data.Units
import bg.zahov.app.utils.FireStoreAdapter
import bg.zahov.app.utils.toFirestoreMap
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import java.io.File
import kotlin.coroutines.resumeWithException
class RealmManager (userId: String) {
    companion object {
        @Volatile
        private var instance: RealmManager? = null
        fun getInstance(userId: String) =
            instance ?: synchronized(this) {
                instance ?: RealmManager(userId).also { instance = it }
            }
    }
    private var realmInstance: Realm? = null
    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val firestoreAdapter = FireStoreAdapter()
    private val uid = userId
    private val realmConfig by lazy {
        getConfig(uid).build()
    }

    //makes sure realm gets locked in a single thread
    private val realmMutex = Mutex()

    //caching user for in app faster data retrieval
    private var userCache: User? = null


    fun doesUserHaveLocalRealmFile() = File(realmConfig.path).exists()

    //user sync from firestore
    private fun createUserFromFirestore(document: DocumentSnapshot){
        userCache = firestoreAdapter.adapt(document.data!!)
        Log.d("USER", userCache?.username.toString() )

    }
    //ensures we open the correct realm File
    private fun getConfig(userId: String): RealmConfiguration.Builder {
        val config = RealmConfiguration.Builder(
            setOf(
                User::class,
                Workout::class,
                Exercise::class,
                Sets::class,
                Settings::class
            )
        )
        config.schemaVersion(1)
        config.deleteRealmIfMigrationNeeded()
        config.name("$userId.realm")
        return config
    }
    suspend fun createRealmFromFirestore(){
                firestoreInstance.collection("users").document(uid).get()
                    .addOnSuccessListener { document ->
                        createUserFromFirestore(document)
                        Log.d("Realm", "Sync")
                        CoroutineScope(Dispatchers.IO).launch{
                            createRealm(userCache!!)
                        }
                    }
    }
    suspend fun createRealm(user: User) {
        try {
            realmMutex.withLock {
                realmInstance = Realm.open(realmConfig)
                realmInstance?.write {
                    copyToRealm(user)
                }
            }
        } catch (e: Exception) {
            Log.e("Realm start error", e.toString())
        } finally {
            realmInstance?.close()
        }
    }
    suspend fun addUserToFirestore(user: User) {
        firestoreInstance.collection("users")
            .document(uid)
            .set(user.toFirestoreMap())
            .addOnSuccessListener {
            }
            .addOnFailureListener {
            }
    }

    private suspend fun <T> withRealm(block: suspend (Realm) -> T): T {
        return suspendCancellableCoroutine { cancellableContinuation ->

            val job = Job()

            val realm = Realm.open(realmConfig)
            cancellableContinuation.invokeOnCancellation {
                job.cancel()
                realm.close()
            }

            CoroutineScope(Dispatchers.IO + job).launch {
                realmMutex.withLock{
                        val result = block(realm)
                        cancellableContinuation.resume(result) {
                            cancellableContinuation.resumeWithException(it)
                        }
                }
            }
        }
    }

//suspend fun getUserInformationForProfileFragment(): Deferred<Triple<String, Int, List<Workout>>> {
//    return coroutineScope {
//        async {
//            val information = userCache?.let { user ->
//                Log.d("Information from user", user.settings.toString())
//                Triple(user.username ?: "invalid", user.numberOfWorkouts ?: -1, user.workouts)
//            } ?: withRealm { realm ->
//                realm.query<User>().first().find().let { realmUser ->
//                    userCache = realmUser
//                    Triple(
//                        realmUser?.username ?: "invalid",
//                        realmUser?.numberOfWorkouts ?: -1,
//                        realmUser?.workouts ?: listOf<Workout>()
//                    )
//                }
//            }
//
//            information
//        }
//    }
//}
        suspend fun getUserInformationForProfileFragment(): Triple<String, Int, List<Workout>> {
        Log.d("USER", userCache?.username ?: "tough luck")
        val information  = userCache?.let { user ->
            Log.d("Information from user", user.settings.toString())
            Triple(user.username ?: "invalid", user.numberOfWorkouts ?: -1, user.workouts)
            } ?: withRealm { realm ->
            realm.query<User>().first().find().let { realmUser ->
                    userCache = realmUser
                    Triple(realmUser?.username ?: "invalid", realmUser?.numberOfWorkouts ?: -1, realmUser?.workouts ?: listOf<Workout>())
                }
            }

        return information
    }

    suspend fun getUsername(): String {
        return userCache?.username ?: withRealm { realm ->
            val realmUser = realm.query<User>().find().first()
            userCache = realmUser
            realmUser.username ?: "invalid"
        }
    }
    suspend fun getUserExercises(): List<Exercise>{
        return userCache?.customExercises ?: withRealm { realm ->
            val realmUser = realm.query<User>().find().first()
            userCache = realmUser
            realmUser.customExercises
        }
    }
    suspend fun getUserSettings(): Settings{
        return userCache?.settings ?: withRealm { realm ->
            val realmUser = realm.query<User>().find().first()
            userCache = realmUser
            realmUser.settings!!
        }
    }
    suspend fun changeUserName(newUserName: String){
        withRealm { realm ->
            val realmUser = realm.query<User>().find().first()
            realm.write {
                findLatest(realmUser)?.let{
                    it.username = newUserName

                    CoroutineScope(Dispatchers.Default).launch {
                        syncFireStoreName(newUserName)
                    }
                }
            }
        }
        userCache?.username = newUserName
    }
    private suspend fun syncFireStoreName(newUserName: String){
        val userDocRef = firestoreInstance.collection("users").document(uid)
        userDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(User::class.java)
                user?.let {
                    userDocRef.set(mapOf("username" to newUserName), SetOptions.merge())
                }
            }
        }.await()
    }
    suspend fun addExercise(newExercise: Exercise){
        withRealm {realm ->
            val user = realm.query<User>().find().first()
            realm.write {
                findLatest(user)?.customExercises?.add(newExercise)

//                    addExerciseToFirestore(newExercise)
            }
        }
    }
    private suspend fun addExerciseToFirestore(newExercise: Exercise){
        val userDocRef = firestoreInstance.collection("users").document(uid)
        userDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(User::class.java)
                user?.let {
                    val updatedExercises = it.customExercises.toMutableList().apply {
                        add(newExercise)
                    }
                    userDocRef.set(mapOf("customExercises" to updatedExercises), SetOptions.merge())
                }
            }
        }.await()
    }
    suspend fun writeNewSettings(title: String, newValue: Any){
        withRealm {realm ->
            val realmUser = realm.query<User>().find().first()
            realm.write {
                findLatest(realmUser)?.let{
                    when(title){
                        "Language" -> {
                            if(newValue is String){
                                it.settings!!.language = Language.valueOf(newValue).name
                            }
                        }
                        "Weight", "Distance" -> {
                            if(newValue is String){
                                it.settings!!.weight = Units.valueOf(newValue).name
                                it.settings!!.distance = Units.valueOf(newValue).name
                            }
                        }
                        "Sound effects" -> {
                            if(newValue is Boolean){
                                it.settings!!.soundEffects = newValue
                            }
                        }
                        "Theme" -> {
                            if(newValue is String){
                                it.settings!!.theme = newValue
                            }
                        }
                        "Timer increment value" -> {
                            if(newValue is Int){
                                it.settings!!.restTimer = newValue
                            }
                        }
                        "Vibrate upon finish" -> {
                            if(newValue is Boolean){
                                it.settings!!.vibration = newValue
                            }
                        }
                        "Sound" -> {
                            if(newValue is String){
                                it.settings!!.soundSettings = newValue
                            }
                        }
                        "Show update template" -> {
                            if(newValue is Boolean){
                                it.settings!!.updateTemplate = newValue
                            }
                        }
                        "Use samsung watch during workout" ->  {
                            if(newValue is Boolean){
                                it.settings!!.fit = newValue
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
//    suspend fun refreshSettings(){
//        withRealm(uid){ realm ->
//            val realmUser = realm.query<User>().find().first()
//            realm.write {
//                realmUser.settings = Settings()
//            }
//        }
//    }
}
//might be better of to have a certain point where e update the user as a whole instead of doing it gradually for performance purposes
//TODO(writeNewSettings for firestore)

//TODO(potentially add sync setting but don't know how to handle some cases - 49)

//TODO(replace find with flow - everywhere)

//TODO(adequate handling on addUserToFirestore onSuccess and onFail)

//TODO(adding every new feature to firestore is stupid needs to be refactored)
//possibly by making checks every once in a while if the user has internet to sync the data
//or making the user manually press a button for sync
