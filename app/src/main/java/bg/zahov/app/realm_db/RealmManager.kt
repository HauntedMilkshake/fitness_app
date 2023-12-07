package bg.zahov.app.realm_db

import android.util.Log
import bg.zahov.app.data.Language
import bg.zahov.app.data.Sound
import bg.zahov.app.data.Theme
import bg.zahov.app.data.Units
import bg.zahov.app.utils.FireStoreAdapter
import bg.zahov.app.utils.toFirestoreMap
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.asFlow
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ListChange
import io.realm.kotlin.notifications.ObjectChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
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

    //caching user for in app faster data retrieval
    private var userCache: User? = null

    //might be redundant as it always returns true if I am not mistaken
    fun doesUserHaveLocalRealmFile() = File(realmConfig.path).exists()

    //user sync from firestore
    private fun createUserFromFirestore(document: DocumentSnapshot) {
        userCache = firestoreAdapter.adapt(document.data!!)
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
                    realmInstance = Realm.open(realmConfig)
                    realmInstance?.write {
                        copyToRealm(user)
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
    suspend fun getSettings() : Flow<ObjectChange<Settings>>{
        return withRealm {realm ->
            realm.query<User>().find().first().settings!!.asFlow()
        }
    }
    suspend fun getExercises(): Flow<ListChange<Exercise>> {
        return withRealm {realm ->
            realm.query<User>().find().first().customExercises.asFlow()
        }
    }
    suspend fun getUser(): Flow<ObjectChange<User>> {
        return withRealm{realm ->
            realm.query<User>().find().first().asFlow()
        }
    }

    suspend fun changeUserName(newUserName: String) {
        withRealm { realm ->
            val realmUser = realm.query<User>().find().first()
            realm.write {
                findLatest(realmUser)?.let {
                    it.username = newUserName
                }
            }
        }
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
        }
    }
    suspend fun writeNewSetting(title: String, newValue: Any) {
        withRealm { realm ->
            val realmUser = realm.query<User>().find().first()
            realm.write {
                findLatest(realmUser)?.settings?.let {
                    when (title) {
                        "Language" -> {
                            it.language = (newValue as Language).name
                        }

                        "Units" -> {
                            it.weight = (newValue as Units).name
                            it.distance = newValue.name
                        }

                        "Sound effects" -> {
                            it.soundEffects = newValue as Boolean
                        }

                        "Theme" -> {
                            it.theme = (newValue as Theme).name
                        }

                        "Timer increment value" -> {
                            it.restTimer = (newValue as Int)
                        }

                        "Vibrate upon finish" -> {
                            it.vibration = (newValue as Boolean)
                        }

                        "Sound" -> {
                            it.soundSettings = (newValue as Sound).name
                        }

                        "Show update template" -> {
                            it.updateTemplate = (newValue as Boolean)
                        }

                        "Use samsung watch during workout" -> {
                            it.fit = (newValue as Boolean)
                        }

                    }
                }
            }
            }
        }
}
//TODO(doesUserHaveRealm might not be working)

