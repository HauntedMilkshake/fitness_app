package bg.zahov.app.data.local

import bg.zahov.app.data.model.Language
import bg.zahov.app.data.model.Sound
import bg.zahov.app.data.model.Theme
import bg.zahov.app.data.model.Units
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.exceptions.RealmException
import io.realm.kotlin.ext.asFlow
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ObjectChange
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class RealmManager {
    companion object {
        @Volatile
        private var instance: RealmManager? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: RealmManager().also { instance = it } }
    }

    private val realmMutex = Mutex()
    private var realmInstance: Realm? = null
    private val realmConfig by lazy {
        getConfig().build()
    }

    private fun getConfig(): RealmConfiguration.Builder {
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
        config.name("lednafiliiki,studknakutiikibrrrrr.realm")
        return config
    }

    private suspend fun openRealm() : Realm {
        return realmMutex.withLock {
            if (realmInstance == null || realmInstance?.isClosed() == true) {
                realmInstance = Realm.open(realmConfig)
            }
            realmInstance!!
        }
    }

    //the idea behind the nullable lists was to make them optional
    suspend fun createRealm(user: User, settings: Settings, exercises: List<Exercise> = emptyList(), workouts: List<Workout> = emptyList()) = withContext(Dispatchers.IO) {
        try {
            //" ...every time you log in production, a puppy dies."
            realmInstance?.write {
                copyToRealm(user)
                copyToRealm(settings)

                if(exercises.isNotEmpty()) {
                    exercises.forEach {
                        copyToRealm(it)
                    }
                }

                if(workouts.isNotEmpty()) {
                    workouts.forEach {
                        copyToRealm(it)
                    }
                }
            }
        } catch (e: Exception) {
            //TODO(Smart handling)
            when(e) {
                is IllegalArgumentException -> {}
                is RealmException -> {}
                else -> {}
            }
        }
    }

    suspend fun deleteRealm() {
        withRealm {
            it.write {
                deleteAll()
            }
        }
    }

    //TODO(Try catch)
    private suspend fun <T> withRealm(block: suspend (Realm) -> T): T {
        return withContext(Dispatchers.IO) {
            block(openRealm())
        }
    }

    suspend fun getUser(): Flow<ObjectChange<User>> = withRealm { realm ->
                realm.query<User>().first().find().asFlow() ?: //TODO()
    }

    suspend fun getSettings(): Flow<ObjectChange<Settings>> = withRealm { realm ->
            realm.query<Settings>().first().find().asFlow() ?: //TODO()
    }

    suspend fun getExercises(isTemplate: Boolean): Flow<ResultsChange<Exercise>> = withRealm { realm ->
            realm.query<Exercise>("isTemplate == $isTemplate").find().asFlow() ?: //TODO(
    }

    suspend fun getWorkouts(isTemplate: Boolean): Flow<ResultsChange<Workout>> =  withRealm { realm ->
            realm.query<Workout>("isTemplate == $isTemplate").find().asFlow()
    }

    suspend fun changeUserName(newUserName: String) = withRealm { realm ->
        val realmUser = getUserSync()
        realm.write {
            realmUser?.let { coldUser ->
                findLatest(coldUser)?.let { hotUser ->
                    hotUser.apply {
                        username = newUserName
                    }
                }
            }
        }
    }

    suspend fun addExercise(newExercise: Exercise) = withRealm { realm ->
        realm.write {
            copyToRealm(newExercise)
        }
    }

    suspend fun addWorkout(newWorkout: Workout) = withRealm { realm ->
        realm.write {
            copyToRealm(newWorkout)
        }
    }

    suspend fun resetSettings() = withRealm { realm ->
            val settings = getSettingsSync()
            realm.write {
                settings?.let { coldSettings ->
                    findLatest(coldSettings)?.apply {
                        language = Language.English.name
                        units = Units.Metric.name
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

    // FIXME please don't use when statements with raw strings, use String constants instead
    //  These checks bases on title look extremely fishy, though
    suspend fun updateSetting(title: String, newValue: Any) = withRealm { realm ->
            val settings = getSettingsSync()
            realm.write {
                settings?.let { coldSettings ->
                    findLatest(coldSettings)?.let {
                        when (title) {
                            "Language" -> {
                                it.language = (newValue as Language).name
                            }

                            "Units" -> {
                                it.units = (newValue as Units).name
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

    suspend fun getUserSync(): User? = withRealm { realm ->
        realm.query<User>().first().find()
    }

    suspend fun getSettingsSync(): Settings? = withRealm { realm ->
        realm.query<Settings>().first().find()
    }

    suspend fun getExercisesSync(isTemplate: Boolean): List<Exercise> = withRealm { realm ->
        realm.query<Exercise>("isTemplate == $isTemplate").find()
    }

    suspend fun getWorkoutsSync(): List<Workout> = withRealm { realm ->
        realm.query<Workout>().find()
    }
}

