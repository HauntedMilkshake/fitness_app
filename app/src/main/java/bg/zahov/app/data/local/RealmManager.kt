package bg.zahov.app.data.local

import android.util.Log
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    private fun getConfig(): RealmConfiguration.Builder {
        val config = RealmConfiguration.Builder(setOf(Settings::class))
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
        Log.d("REALM", "BEFORE CREATION")
        //try catch
        openRealm()
        realmInstance?.write {
            try {
                Log.d("REALM", "CREATION")
                copyToRealm(Settings())
            } catch (e: IllegalArgumentException) {
                throw e
            }
        }
    }

    suspend fun deleteRealm() {
//        withRealm {
//            it.write {
//                deleteAll()
//            }
//        }
        withContext(Dispatchers.IO) {
            try {
                Realm.deleteRealm(realmConfig)
            } catch (e: IllegalStateException) {
                throw e
            }

        }
    }

    suspend fun getSettings() = withRealm { realm ->
        realm.query<Settings>().find().first().asFlow()
    }

    //TODO(Try catch)
    private suspend fun <T> withRealm(block: suspend (Realm) -> T): T {
        return block(openRealm())
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

        getSettings()?.collect {
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

