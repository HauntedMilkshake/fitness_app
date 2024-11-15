package bg.zahov.app.data.local

import bg.zahov.app.data.local.RealmDefaultSetting.DEFAULT_SETTING
import bg.zahov.app.data.model.*
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.asFlow
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ObjectChange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import bg.zahov.app.data.model.state.TypeSettings

/**
 * Manages the Realm database instance, providing methods for accessing and manipulating user settings,
 * workout states, and associated entities.
 *
 * This singleton class initializes and configures the database schema, handles database operations,
 * and provides utilities for settings and workout management.
 *
 * @property realmConfig Lazy-loaded [RealmConfiguration] for the database schema and settings.
 */
class RealmManager {
    companion object {
        private var instance: RealmManager? = null

        /**
         * Provides a thread-safe singleton instance of [RealmManager].
         *
         * @return The singleton instance of [RealmManager].
         */
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: RealmManager().also { instance = it }

            }

    }

    private var realmInstance: Realm? = null
    private val realmConfig by lazy { getConfig().build() }

    /**
     * Checks if the Realm database file exists.
     *
     * @return `true` if the database file exists; `false` otherwise.
     */
    fun doesRealmExist(): Boolean = File(realmConfig.path).exists()

    /**
     * Creates and configures a [RealmConfiguration.Builder] with the specified schema and options.
     *
     * @return A configured [RealmConfiguration.Builder] instance.
     */
    private fun getConfig(): RealmConfiguration.Builder {
        val config = RealmConfiguration.Builder(
            setOf(Settings::class, RealmWorkoutState::class, RealmExercise::class, RealmSets::class)
        )
        config.schemaVersion(1)
        config.deleteRealmIfMigrationNeeded()
        config.name("lednafiliiki,studknakutiikibrrrrr.realm")
        return config
    }

    /**
     * Opens the Realm instance with the configured settings if not already open.
     *
     * @return An open [Realm] instance.
     */
    private suspend fun openRealm(): Realm {
        return withContext(Dispatchers.IO) {
            realmInstance?.takeIf { !it.isClosed() } ?: run {
                try {
                    Realm.open(realmConfig).also { realmInstance = it }
                } catch (e: IllegalArgumentException) {
                    throw e
                }
            }
        }
    }

    /**
     * Initializes the Realm database and creates default settings if necessary.
     */
    suspend fun createRealm() = withContext(Dispatchers.IO) {
        openRealm()
        realmInstance?.write {
            try {
                copyToRealm(Settings())
            } catch (e: IllegalArgumentException) {
                throw e
            }
        }
    }

    /**
     * Deletes all data from the Realm database.
     */
    suspend fun deleteRealm() {
        withRealm { it.write { deleteAll() } }
    }

    /**
     * Retrieves the current workout state.
     *
     * @return The current [RealmWorkoutState], if present.
     */
    suspend fun getWorkoutState() = withRealm { realm ->
        realm.query<RealmWorkoutState>().first().find()
    }

    /**
     * Adds or updates the workout state in the database.
     *
     * @param workout The [RealmWorkoutState] object to be added or updated.
     */
    suspend fun addWorkoutState(workout: RealmWorkoutState) = withRealm { realm ->
        realm.write { copyToRealm(workout) }
    }

    /**
     * Creates default settings if none exist in the database.
     */
    suspend fun addSettings() = withRealm { realm ->
        if (realm.query<Settings>().first().find() == null) {
            realm.write { copyToRealm(Settings()) }
        }
    }

    /**
     * Clears the current workout state, removing associated exercises and sets.
     */
    suspend fun clearWorkoutState() = withRealm { realm ->
        realm.write {
            query<RealmSets>().find().forEach { delete(it) }
            query<RealmExercise>().find().forEach { delete(it) }
            query<RealmWorkoutState>().find().forEach {
                if (it.id != DEFAULT_SETTING) delete(it)
            }
        }
    }

    /**
     * Retrieves the settings as a [Flow], observing changes in real-time.
     *
     * @return A [Flow] of [ObjectChange] events for the [Settings] object.
     */
    suspend fun getSettings(): Flow<ObjectChange<Settings>> = withRealm { realm ->
        realm.query<Settings>().find().first().asFlow()
    }

    /**
     * Retrieves the current settings once, without observing changes.
     *
     * @return The current [Settings] object.
     */
    private suspend fun getSettingsOnce(): Settings = withRealm { realm ->
        realm.query<Settings>().find().first()
    }

    /**
     * Resets all settings fields to their default values.
     */
    suspend fun resetSettings() = withRealm { realm ->
        val settings = getSettingsOnce()
        realm.write {
            findLatest(settings)?.apply {
                language = Language.English.key
                units = Units.METRIC.key
                soundEffects = true
                theme = Theme.Dark.key
                restTimer = 30
                vibration = true
                soundSettings = Sound.SOUND_2.key
                updateTemplate = true
                enableWatch = false
                automaticSync = true
            }
        }
    }

    /**
     * Updates a specified setting based on the title and new value provided.
     *
     * @param title The title of the setting to update.
     * @param newValue The new value for the setting.
     */
    suspend fun updateSetting(title: TypeSettings, newValue: Any) = withRealm { realm ->
        val settings = getSettingsOnce()
        realm.write {
            findLatest(settings)?.let {
                when (title) {
                    TypeSettings.LANGUAGE_SETTING -> if (newValue is String)
                        it.language = newValue

                    TypeSettings.UNIT_SETTING -> if (newValue is String)
                        it.units = newValue

                    TypeSettings.SOUND_EFFECTS_SETTING -> if (newValue is Boolean)
                        it.soundEffects = newValue

                    TypeSettings.THEME_SETTING -> if (newValue is String)
                        it.theme = newValue

                    TypeSettings.REST_TIMER_SETTING -> if (newValue is Int)
                        it.restTimer = newValue

                    TypeSettings.VIBRATION_SETTING -> if (newValue is Boolean)
                        it.vibration = newValue

                    TypeSettings.SOUND_SETTING -> if (newValue is String)
                        it.soundSettings = newValue

                    TypeSettings.UPDATE_TEMPLATE_SETTING -> if (newValue is Boolean)
                        it.updateTemplate = newValue

                    TypeSettings.WATCH_SETTINGS -> if (newValue is Boolean)
                        it.enableWatch = newValue

                    TypeSettings.AUTOMATIC_SYNC_SETTING -> if (newValue is Boolean)
                        it.automaticSync = newValue
                }
            }
        }
    }

    /**
     * Utility function to execute operations with a guaranteed open [Realm] instance.
     *
     * @param block The block of code to execute with the [Realm] instance.
     * @return The result of the block.
     */
    private suspend fun <T> withRealm(block: suspend (Realm) -> T): T {
        val realm = openRealm()
        return block(realm)
    }
}
