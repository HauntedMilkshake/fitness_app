package bg.zahov.app.backend

import android.util.Log
import bg.zahov.app.data.Language
import bg.zahov.app.data.Sound
import bg.zahov.app.data.Theme
import bg.zahov.app.data.Units
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.asFlow
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ObjectChange
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

class RealmManager() {
    companion object {
        @Volatile
        private var instance: RealmManager? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: RealmManager().also { instance = it }
            }
    }
    private  var realmInstance: Realm? = null
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
        config.name("talicegay,maksoshtepoveche,ivonaimnogo.realm")
        return config
    }

    private fun openRealm(){
        if(realmInstance == null || realmInstance?.isClosed() == true) {
            realmInstance = Realm.open(realmConfig)
        }
    }
    private fun closeRealm(){
        realmInstance?.close()
    }

    suspend fun createRealm(user: User, workouts: List<Workout?>?, exercises: List<Exercise?>?, settings: Settings) {
        withContext(Dispatchers.Main) {
            try {

                if(!doesUserHaveRealm()){
                    openRealm()
                }

                realmInstance?.write {
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

    suspend fun deleteRealm(){
        withRealm {
            it?.write {
                deleteAll()
            }
        }
    }

    fun doesUserHaveRealm() = File(realmConfig.path).exists()

    private suspend fun <T> withRealm(block: suspend (Realm?) -> T): T {
        return withContext(Dispatchers.IO) {
            openRealm()
            block(realmInstance)
        }
    }

    suspend fun getUser(): Flow<ObjectChange<User>>? {
        return withRealm { realm ->
            withContext(NonCancellable){
                realm?.query<User>()?.first()?.find()?.asFlow()
            }
        }
    }

    suspend fun getUserSync(): User? {
        return withRealm{ realm ->
            realm?.query<User>()?.find()?.first()
        }
    }

    suspend fun getSettings(): Flow<ObjectChange<Settings>>? {
        return withRealm { realm ->
            realm?.query<Settings>()?.find()?.first()?.asFlow()
        }
    }
    suspend fun getTemplateExercises(): Flow<ResultsChange<Exercise>>? {
        return withRealm { realm ->
            realm?.query<Exercise>("isTemplate == true")?.find()?.asFlow()
        }
    }

    suspend fun getPerformedWorkouts(): Flow<ResultsChange<Workout>>? {
        return withRealm { realm ->
            realm?.query<Workout>("isTemplate == false")?.find()?.asFlow()
        }
    }

    suspend fun getAllWorkouts(): Flow<ResultsChange<Workout>>? {
        return withRealm {realm ->
            realm?.query<Workout>()?.find()?.asFlow()
        }
    }

    suspend fun getTemplateWorkouts(): Flow<ResultsChange<Workout>>? {
        return withRealm {realm ->
            realm?.query<Workout>("isTemplate == true")?.find()?.asFlow()
        }
    }

    suspend fun changeUserName(newUserName: String) {
        withRealm { realm ->
            val realmUser = getUserSync()
            realm?.write {
                realmUser?.let{coldUser->
                    findLatest(coldUser)?.let{hotUser ->
                        hotUser.apply {
                            username = newUserName
                        }
                    }
                }
            }
        }
    }

    suspend fun addExercise(newExercise: Exercise) {
        withRealm { realm ->
            realm?.write {
               copyToRealm(newExercise)
            }
        }
    }

    suspend fun addWorkout(newWorkout: Workout){
        withRealm { realm ->
            realm?.write {
                copyToRealm(newWorkout)
            }
        }
    }

    suspend fun resetSettings() {
        withRealm { realm ->
            val settings = getSettingsSync()
            realm?.write {
                settings?.let{ coldSettings ->
                    findLatest(coldSettings)?.apply {
                        language= Language.English.name
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
    }

    suspend fun updateSetting(title: String, newValue: Any) {
        withRealm { realm ->
            val settings = getSettingsSync()
            realm?.write {
                settings?.let{coldSettings ->
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
    }

    suspend fun getSettingsSync(): Settings? {
        return withRealm{ realm ->
            realm?.query<Settings>()?.find()?.first()
        }
    }

    suspend fun getTemplateExercisesSync(): List<Exercise>? {
        return withRealm{ realm ->
            realm?.query<Exercise>("isTemplate == true")?.find()
        }
    }
    suspend fun getPastWorkoutsSync(): List<Workout>? {
        return withRealm { realm ->
            realm?.query<Workout>("isTemplate == false")?.find()
        }
    }
    suspend fun getTemplateWorkoutsSync(): List<Workout>? {
        return withRealm { realm ->
            realm?.query<Workout>("isTemplate == true")?.find()
        }
    }
}

