package bg.zahov.app.realm_db

import android.util.Log
import bg.zahov.app.data.Language
import bg.zahov.app.data.Sound
import bg.zahov.app.data.Theme
import bg.zahov.app.data.Units
import bg.zahov.app.utils.FirestoreExerciseAdapter
import bg.zahov.app.utils.FirestoreSettingsAdapter
import bg.zahov.app.utils.FirestoreUserAdapter
import bg.zahov.app.utils.FirestoreWorkoutAdapter
import bg.zahov.app.utils.toFirestoreMap
import com.google.firebase.firestore.FirebaseFirestore
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.asFlow
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ObjectChange
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RealmManager(userId: String) {
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

    private val userAdapter =  FirestoreUserAdapter()
    private val workoutAdapter =  FirestoreWorkoutAdapter()
    private val exerciseAdapter = FirestoreExerciseAdapter()
    private val settingsAdapter = FirestoreSettingsAdapter()


    private val uid = userId
    private val realmConfig by lazy {
        getConfig(uid).build()
    }


    suspend fun syncFromFirestore() {

        val userDocument = firestoreInstance.collection("users").document(uid).get().await()

        val rUser = userAdapter.adapt(userDocument.data!!)

        val settingsDocument = userDocument.reference.collection("settings").document("userSettings").get().await()
        val settings = settingsAdapter.adapt(settingsDocument.data!!)

        val workoutsCollection = userDocument.reference.collection("workouts").get().await()
        val workouts = workoutsCollection.documents.mapNotNull { workoutDocument ->
            val workout = workoutAdapter.adapt(workoutDocument.data!!)
            workout
        }

        val exercisesCollection = userDocument.reference.collection("exercises").get().await()
        val exercises = exercisesCollection.documents.mapNotNull { exerciseDocument ->
            val exercise = exerciseAdapter.adapt(exerciseDocument.data!!)
            exercise
        }

        CoroutineScope(Dispatchers.Main).launch {
            createRealm(rUser, workouts, exercises, settings)
        }
    }

    fun syncToFirestore(user: User?,  workouts: List<Workout?>?, exercises: List<Exercise?>?, settings: Settings?, ) {
        val userDocRef = firestoreInstance.collection("users").document(uid)

        user?.let{
            userDocRef.set(user.toFirestoreMap())
        }

        settings?.let{
            userDocRef.collection("settings").document("userSettings").set(settings.toFirestoreMap())
        }

        workouts?.let {
            it.filterNotNull().forEach { workout ->
                userDocRef.collection("workouts").add(workout.toFirestoreMap())
            }
        }

        exercises?.let {
            it.filterNotNull().forEach { exercise ->
                userDocRef.collection("exercises").add(exercise.toFirestoreMap())
            }
        }
    }

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

    suspend fun createRealm(user: User, workouts: List<Workout?>?, exercises: List<Exercise?>?, settings: Settings) {
        withContext(Dispatchers.IO) {
            try {
                val realmInstance = Realm.open(realmConfig)
                realmInstance.write {
                    copyToRealm(user)
                    copyToRealm(settings)

                    workouts?.forEach { workout ->
                        workout?.let { copyToRealm(it) }
                    }

                    exercises?.forEach { exercise ->
                        exercise?.let { copyToRealm(it) }
                    }
                }
            } catch (e: Exception) {
                Log.e("Realm start error", e.toString())
            } finally {
                realmInstance?.close()
            }
        }
    }

    private suspend fun <T> withRealm(block: suspend (Realm) -> T): T {
        return withContext(Dispatchers.IO) {
            val realm = Realm.open(realmConfig)
            block(realm)
        }
    }

    suspend fun getUser(): Flow<ObjectChange<User>> {
        return withRealm { realm ->
            realm.query<User>().first().find()!!.asFlow()
        }
    }

    suspend fun getSettings(): Flow<ObjectChange<Settings>> {
        return withRealm { realm ->
            realm.query<Settings>().find().first().asFlow()
        }
    }
    suspend fun getSettingsSync(): Settings {
        return withRealm{ realm ->
            realm.query<Settings>().find().first()
        }
    }
    suspend fun getUserSync(): User {
        return withRealm{ realm ->
            realm.query<User>().find().first()
        }
    }
    suspend fun getTemplateExercisesSync():  List<Exercise>{
        return withRealm{ realm ->
            realm.query<Exercise>("isTemplate == true").find()
        }
    }
    suspend fun getWorkoutsSync(): List<Workout> {
        return withRealm { realm ->
            realm.query<Workout>().find()
        }
    }
    suspend fun getTemplateExercises(): Flow<ResultsChange<Exercise>> {
        return withRealm { realm ->
            realm.query<Exercise>("isTemplate == true").find().asFlow()
        }
    }

    suspend fun getPerformedWorkouts(): Flow<ResultsChange<Workout>> {
        return withRealm { realm ->
            realm.query<Workout>("isTemplate == false").find().asFlow()
        }
    }
    suspend fun getAllWorkouts(): Flow<ResultsChange<Workout>> {
        return withRealm {realm ->
            realm.query<Workout>().find().asFlow()
        }
    }
    suspend fun getTemplateWorkouts(): Flow<ResultsChange<Workout>> {
        return withRealm {realm ->
            realm.query<Workout>("isTemplate == true").find().asFlow()
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
            realm.write {
               copyToRealm(newExercise)
            }
        }
    }

    suspend fun resetSettings() {
        withRealm { realm ->
            val settings = realm.query<Settings>().find().first()
            realm.write {
                findLatest(settings)?.apply {
                    language= Language.English.name
                    weight = Units.Metric.name
                    distance = Units.Metric.name
                    soundEffects = true
                    theme = Theme.Dark.name
                    restTimer = 30
                    vibration = true
                    soundSettings = Sound.SOUND_1.name
                    updateTemplate = true
                    fit = false
                    automaticSync = true
                }
            }
        }
    }

    suspend fun updateSetting(title: String, newValue: Any) {
        withRealm { realm ->
            val settings = realm.query<Settings>().find().first()
            realm.write {
                findLatest(settings)?.let {
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

                        "Automatic between device sync" -> {
                            it.automaticSync = (newValue as Boolean)
                        }

                    }
                }
            }
        }
    }
}

