package bg.zahov.app.realm_db

import android.util.Log
import bg.zahov.app.data.Language
import bg.zahov.app.data.Units
import bg.zahov.app.utils.FireStoreAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
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
    private val auth = FirebaseAuth.getInstance()
    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val firestoreAdapter = FireStoreAdapter()

    //makes sure realm gets locked in a single thread
    private val realmMutex = Mutex()
    private val uid = auth.currentUser!!.uid
    //basically a global user instance
    //if a user has synced data and he doesn't have a realm file yet we create him a realm file from createRealm
    //caching user for in app faster data retrieval
    private var user: User? = null

    init {
        if(doesUserHaveSyncData()){
            if(realmInstance == null){
                firestoreInstance.collection("users").document(uid).get()
                    .addOnSuccessListener { document ->
                        createUserFromFirestore(document)
                        CoroutineScope(Dispatchers.IO).launch{
                            createRealm(uid, user!!)
                        }
                    }
            }
        }else if(realmInstance == null){
            CoroutineScope(Dispatchers.IO).launch {
                createUserFromRealm()
            }
        }
    }
    //checks if the authenticated user has firestore data
    private fun doesUserHaveSyncData(): Boolean{

        var result = false

        firestoreInstance.collection("users").document(uid).get()
            .addOnSuccessListener { result = it.exists() }

        return result
    }
    //user sync from firestore
    private fun createUserFromFirestore(document: DocumentSnapshot){
        user = firestoreAdapter.adapt(document.data!!)
    }
    //function is fine as it is called only within instances where a user has realm file
    private suspend fun createUserFromRealm(){
        return withRealm(uid){realm ->
         realm.query<User>().first().find().let {
             user = it
         }
        }
    }
    //function ensures we open the correct realm File
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
    suspend fun createRealm(userId: String, user: User) {
        try {
            realmMutex.withLock{
                realmInstance = Realm.open(getConfig(userId).build())
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
                        val result = block(realm)
                        cancellableContinuation.resume(result) {
                            cancellableContinuation.resumeWithException(it)
                        }
                }
            }
        }
    }
    suspend fun getUserInformationForProfileFragment(): Triple<String, Int, List<Workout>> {
        val information  = user?.let { user ->
            Triple(user.username ?: "invalid", user.numberOfWorkouts ?: -1, user.workouts)
            } ?: withRealm(uid) { realm ->
            realm.query<User>().first().find().let { realmUser ->
                    user = realmUser
                    Triple(realmUser?.username ?: "invalid", realmUser?.numberOfWorkouts ?: -1, realmUser?.workouts ?: listOf<Workout>())
                }
            }
        return information
    }
    suspend fun getUsername(): String {
        return user?.username ?: withRealm(uid) { realm ->
            val realmUser = realm.query<User>().find().first()
            user = realmUser
            realmUser.username ?: "invalid"
        }
    }
    suspend fun getUserExercises(): List<Exercise>{
        return user?.customExercises ?: withRealm(uid){ realm ->
            val realmUser = realm.query<User>().find().first()
            user = realmUser
            realmUser.customExercises
        }
    }
    suspend fun getUserSettings(): Settings{
        return user?.settings ?: withRealm(uid){realm ->
            val realmUser = realm.query<User>().find().first()
            user = realmUser
            realmUser.settings
        }
    }
    suspend fun changeUserName(newUserName: String){
        withRealm(uid){ realm ->
            val realmUser = realm.query<User>().find().first()
            realm.write {
                findLatest(realmUser)?.let{
                    it.username = newUserName

                    CoroutineScope(Dispatchers.IO).launch {
                        syncFireStoreName(newUserName)
                    }
                }
            }
        }
        user?.username = newUserName
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
        withRealm(uid){realm ->
            val user = realm.query<User>().find().first()
            realm.write {
                findLatest(user)?.customExercises?.add(newExercise)

                CoroutineScope(Dispatchers.IO).launch {
                    addExerciseToFirestore(newExercise)
                }
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
        withRealm(uid){realm ->
            val realmUser = realm.query<User>().find().first()
            realm.write {
                findLatest(realmUser)?.let{
                    when(title){
                        "Language" -> {
                            if(newValue is String){
                                it.settings.language = Language.valueOf(newValue).name
                            }
                        }
                        "Weight", "Distance" -> {
                            if(newValue is String){
                                it.settings.weight = Units.valueOf(newValue).name
                                it.settings.distance = Units.valueOf(newValue).name
                            }
                        }
                        "Sound effects" -> {
                            if(newValue is Boolean){
                                it.settings.soundEffects = newValue
                            }
                        }
                        "Theme" -> {
                            if(newValue is String){
                                it.settings.theme = newValue
                            }
                        }
                        "Timer increment value" -> {
                            if(newValue is Int){
                                it.settings.restTimer = newValue
                            }
                        }
                        "Vibrate upon finish" -> {
                            if(newValue is Boolean){
                                it.settings.vibration = newValue
                            }
                        }
                        "Sound" -> {
                            if(newValue is String){
                                it.settings.soundSettings = newValue
                            }
                        }
                        "Show update template" -> {
                            if(newValue is Boolean){
                                it.settings.updateTemplate = newValue
                            }
                        }
                        "Use samsung watch during workout" ->  {
                            if(newValue is Boolean){
                                it.settings.fit = newValue
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
    suspend fun refreshSettings(){
        withRealm(uid){realm ->
            val realmUser = realm.query<User>().find().first()
            realm.write {
                realmUser.settings = Settings()
            }
        }
    }
}
//TODO(writeNewSettings for firestore)
//TODO(potentially add sync but don't know how to handle it - 49)
//TODO(replace find with flow - everywhere)

