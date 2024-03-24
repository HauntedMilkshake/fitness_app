package bg.zahov.app.data.local

import bg.zahov.app.data.model.Language
import bg.zahov.app.data.model.LanguageKeys
import bg.zahov.app.data.model.Sound
import bg.zahov.app.data.model.SoundKeys
import bg.zahov.app.data.model.Theme
import bg.zahov.app.data.model.ThemeKeys
import bg.zahov.app.data.model.Units
import bg.zahov.app.data.model.UnitsKeys
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.asFlow
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ObjectChange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

class RealmManager {
    companion object {
        const val LANGUAGE_SETTING = "Language"
        const val UNIT_SETTING = "Units"
        const val SOUND_EFFECTS_SETTING = "Sound effect"
        const val THEME_SETTING = "Theme"
        const val REST_TIMER_SETTING = "Rest timer"
        const val VIBRATION_SETTING = "Vibrate upon finish"
        const val SOUND_SETTING = "Sound"
        const val UPDATE_TEMPLATE_SETTING = "Show update template"
        const val FIT_SETTING = "Use samsung watch during workout"
        const val AUTOMATIC_SYNC_SETTING = "Automatic between device sync"

        @Volatile
        private var instance: RealmManager? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: RealmManager().also { instance = it }
            }
    }

    private var realmInstance: Realm? = null
    private val realmConfig by lazy {
        getConfig().build()
    }

    fun doesRealmExist(): Boolean = File(realmConfig.path).exists()

    private fun getConfig(): RealmConfiguration.Builder {
        val config = RealmConfiguration.Builder(
            setOf(
                Settings::class,
                RealmWorkoutState::class,
                RealmExercise::class,
                RealmSets::class
            )
        )
        config.schemaVersion(1)
        config.deleteRealmIfMigrationNeeded()
        config.name("lednafiliiki,studknakutiikibrrrrr.realm")
        return config
    }

    private suspend fun openRealm(): Realm {
        return withContext(Dispatchers.IO) {
            if (realmInstance == null || realmInstance?.isClosed() == true) {
                try {
                    realmInstance = Realm.open(realmConfig)
                } catch (e: IllegalArgumentException) {
                    throw e
                }
                realmInstance!!
            } else {
                realmInstance!!
            }
        }
    }

    suspend fun createRealm() = withContext(Dispatchers.IO) {
        openRealm()
        realmInstance?.write {
            try {
                copyToRealm(Settings())
            } catch (e: IllegalArgumentException) {
                throw e
            } finally {
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

    suspend fun getWorkoutState() = withRealm { realm ->
        realm.query<RealmWorkoutState>().first().find()
    }

    suspend fun addWorkoutState(workout: RealmWorkoutState) = withRealm { realm ->
        realm.write {
            copyToRealm(workout)
        }
    }
    suspend fun addSettings() = withRealm {realm ->
        if(realm.query<Settings>().first().find() == null){
            realm.write {
                copyToRealm(Settings())
            }
        }
    }

    suspend fun clearWorkoutState() = withRealm { realm ->
        realm.write {
            val sets = query<RealmSets>().find()
            if (sets.isNotEmpty()) sets.forEach { set -> delete(set) }
            val exercises = query<RealmExercise>().find()
            if (exercises.isNotEmpty()) exercises.forEach { exercise -> delete(exercise) }
            val workoutState = query<RealmWorkoutState>().find()
            if (workoutState.isNotEmpty()) workoutState.forEach { workoutState ->
                if (workoutState.id != "default") delete(
                    workoutState
                )
            }
        }
    }

    suspend fun getSettings(): Flow<ObjectChange<Settings>> = withRealm { realm ->
        realm.query<Settings>().find().first().asFlow()
    }

    private suspend fun <T> withRealm(block: suspend (Realm) -> T): T {
        val realm = openRealm()
        return try {
            block(realm)
        } finally {
//            realm.close()
        }
    }

    suspend fun resetSettings() = withRealm { realm ->
        var settings: Settings? = null

        getSettings().collect {
            settings = it.obj
        }

        realm.write {
            settings?.let { coldSettings ->
                findLatest(coldSettings)?.apply {
                    language = Language.fromKey(LanguageKeys.ENGLISH)
                    units = Units.fromKey(UnitsKeys.METRIC)
                    soundEffects = true
                    theme = Theme.fromKey(ThemeKeys.DARK)
                    restTimer = 30
                    vibration = true
                    soundSettings = Sound.fromKey(SoundKeys.SOUND_1)
                    updateTemplate = true
                    fit = false
                    automaticSync = true
                }
            }
        }
    }

    suspend fun updateSetting(title: String, newValue: Any) = withRealm { realm ->
        var settings: Settings? = null

        getSettings().collect {
            settings = it.obj
        }

        realm.write {
            settings?.let { coldSettings ->
                findLatest(coldSettings)?.let {
                    when (title) {
                        LANGUAGE_SETTING -> {
                            it.language = Language.fromKey((newValue as String))
                        }

                        UNIT_SETTING -> {
                            it.units = Units.fromKey((newValue as String))
                        }

                        SOUND_EFFECTS_SETTING -> {
                            it.soundEffects = (newValue as Boolean)
                        }

                        THEME_SETTING -> {
                            it.theme = Theme.fromKey((newValue as String))
                        }

                        REST_TIMER_SETTING -> {
                            it.restTimer = (newValue as Int)
                        }

                        VIBRATION_SETTING -> {
                            it.vibration = (newValue as Boolean)
                        }

                        SOUND_SETTING -> {
                            it.soundSettings = Sound.fromKey((newValue as String))
                        }

                        UPDATE_TEMPLATE_SETTING -> {
                            it.updateTemplate = (newValue as Boolean)
                        }

                        FIT_SETTING -> {
                            it.fit = (newValue as Boolean)
                        }

                        AUTOMATIC_SYNC_SETTING -> {
                            it.automaticSync = (newValue as Boolean)
                        }
                    }
                }
            }
        }
    }
}

