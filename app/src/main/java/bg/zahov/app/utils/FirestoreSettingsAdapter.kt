package bg.zahov.app.utils

import bg.zahov.app.common.Adapter
import bg.zahov.app.data.Language
import bg.zahov.app.data.Sound
import bg.zahov.app.data.Theme
import bg.zahov.app.data.Units
import bg.zahov.app.realm_db.Settings

class FirestoreSettingsAdapter : Adapter<Map<String, Any>, Settings> {
    override fun adapt(t: Map<String, Any>): Settings {
        return Settings().apply {
            language = t["language"] as? String ?: Language.English.name
            weight = t["weight"] as? String ?: Units.Metric.name
            distance = t["distance"] as? String ?: Units.Metric.name
            soundEffects = t["soundEffects"] as? Boolean ?: true
            theme = t["theme"] as? String ?: Theme.Dark.name
            restTimer = (t["restTimer"] as? Long)?.toInt() ?: 30
            vibration = t["vibration"] as? Boolean ?: true
            soundSettings = t["soundSettings"] as? String ?: Sound.SOUND_1.name
            updateTemplate = t["updateTemplate"] as? Boolean ?: true
            fit = t["fit"] as? Boolean ?: false
        }
    }
}
