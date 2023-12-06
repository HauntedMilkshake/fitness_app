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
import io.realm.kotlin.ext.asFlow
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.DeletedObject
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.ObjectChange
import io.realm.kotlin.notifications.PendingObject
import io.realm.kotlin.notifications.SingleQueryChange
import io.realm.kotlin.notifications.UpdatedObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
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

    //might be redundant as it always returns true if I am not mistaken
    fun doesUserHaveLocalRealmFile() = File(realmConfig.path).exists()

    //user sync from firestore
    private fun createUserFromFirestore(document: DocumentSnapshot) {
        userCache = firestoreAdapter.adapt(document.data!!)
        Log.d("USER", userCache?.username.toString())

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
    suspend fun createRealmFromFirestore() {
        firestoreInstance.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                createUserFromFirestore(document)
                Log.d("Realm", "Sync")
                CoroutineScope(Dispatchers.IO).launch {
                    createRealm(userCache!!)
                }
            }
    }
    suspend fun createRealm(user: User) {
        withContext(Dispatchers.IO) {
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
    }
    private fun addUserToFirestore(user: User) {
        firestoreInstance.collection("users")
            .document(uid)
            .set(user.toFirestoreMap())
            .addOnSuccessListener {
            }
            .addOnFailureListener {
            }
    }
    suspend fun syncFromRealmToFirestore() {
        userCache?.let {
            addUserToFirestore(it)
        } ?: withRealm {
            val user = it.query<User>().find().first()
            addUserToFirestore(user)
            userCache = user
        }
    }
    private suspend fun <T> withRealm(block: suspend (Realm) -> T): T {
        return withContext(Dispatchers.IO) {
            val realm = Realm.open(realmConfig)
            block(realm)
        }
    }
    suspend fun getUserInformationForProfileFragment(): Triple<String, Int, List<Workout>> {
        Log.d("USER", userCache?.username ?: "tough luck")
        val information = userCache?.let { user ->
            Log.d("Information from user", user.settings.toString())
            Triple(user.username ?: "invalid", user.numberOfWorkouts ?: -1, user.workouts)
        } ?: withRealm { realm ->
            realm.query<User>().first().find().let { realmUser ->
                userCache = realmUser
                Log.d("USER", realmUser?.username ?: "shit")
                Triple(
                    realmUser?.username ?: "invalid",
                    realmUser?.numberOfWorkouts ?: -1,
                    realmUser?.workouts ?: listOf<Workout>()
                )
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

    suspend fun getUserExercises(): List<Exercise> {
        return userCache?.customExercises ?: withRealm { realm ->
            val realmUser = realm.query<User>().find().first()
            userCache = realmUser
            realmUser.customExercises
        }
    }

    suspend fun changeUserName(newUserName: String) {
        withRealm { realm ->
            val realmUser = realm.query<User>().find().first()
            realm.write {
                findLatest(realmUser)?.let {
                    it.username = newUserName
//
//                    CoroutineScope(Dispatchers.Default).launch {
//                        syncFireStoreName(newUserName)
//                    }
                }
            }
        }
        userCache?.username = newUserName
    }

    private suspend fun syncFireStoreName(newUserName: String) {
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

    suspend fun addExercise(newExercise: Exercise) {
        withRealm { realm ->
            val user = realm.query<User>().find().first()
            realm.write {
                findLatest(user)?.customExercises?.add(newExercise)
            }
        }
    }

    suspend fun resetSettings(){
        withRealm { realm ->
            val realmUser = realm.query<User>().find().first()
           realm.write {
               findLatest(realmUser)?.let{
                   it.settings = Settings()
               }
           }
            userCache?.settings= Settings()
        }
    }
    suspend fun writeNewSetting(title: String, newValue: Any) {
        withRealm { realm ->
            val realmUser = realm.query<User>().find().first()
            realm.write {
                findLatest(realmUser)?.let {
                    Log.d("WRITING", title)
                    Log.d("WRITING", it.settings!!.language)
                    when (title) {
                        "Language" -> {
                            if (newValue is String) {
                                Log.d("WRITING", newValue)
                                Log.d("WRITING", Language.valueOf(newValue).name)
                                it.settings!!.language = Language.valueOf(newValue).name
                            }
                        }

                        "Units" -> {
                            if (newValue is String) {
                                it.settings!!.weight = Units.valueOf(newValue).name
                                it.settings!!.distance = Units.valueOf(newValue).name
                            }
                        }

                        "Sound effects" -> {
                            if (newValue is Boolean) {
                                it.settings!!.soundEffects = newValue
                            }
                        }

                        "Theme" -> {
                            if (newValue is String) {
                                it.settings!!.theme = newValue
                            }
                        }

                        "Timer increment value" -> {
                            if (newValue is Int) {
                                it.settings!!.restTimer = newValue
                            }
                        }

                        "Vibrate upon finish" -> {
                            if (newValue is Boolean) {
                                it.settings!!.vibration = newValue
                            }
                        }

                        "Sound" -> {
                            if (newValue is String) {
                                it.settings!!.soundSettings = newValue
                            }
                        }

                        "Show update template" -> {
                            if (newValue is Boolean) {
                                it.settings!!.updateTemplate = newValue
                            }
                        }

                        "Use samsung watch during workout" -> {
                            if (newValue is Boolean) {
                                it.settings!!.fit = newValue
                            }
                        }

                        else -> {}
                    }
                    Log.d("WRITING", it.settings!!.language)
                    userCache = it
                }
            }
        }
    }
    suspend fun getUserSettings(): Flow<Settings> = withContext(Dispatchers.IO) {
        return@withContext userCache?.settings?.asFlow()?.map { objectChange ->
            when (objectChange) {
                is InitialObject -> objectChange.obj
                is UpdatedObject -> objectChange.obj
                else -> throw UnsupportedOperationException("Unexpected ObjectChange type")
            }
        } ?: withContext(Dispatchers.Main) {
            withRealm { realm ->
                val user = realm.query<User>().find().first()
                user.settings?.asFlow()?.map { objectChange ->
                    when (objectChange) {
                        is InitialObject -> objectChange.obj
                        is UpdatedObject -> objectChange.obj
                        else -> throw UnsupportedOperationException("Unexpected ObjectChange type")
                    }
                } ?: flowOf(Settings())
            }
        }
    }



}




//TODO(doesUserHaveRealm might not be working)
//TODO(Cache isn't being initialized)

