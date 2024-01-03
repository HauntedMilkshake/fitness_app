package bg.zahov.app.utils

import bg.zahov.app.common.Adapter
import bg.zahov.app.data.Language
import bg.zahov.app.data.Sound
import bg.zahov.app.data.Theme
import bg.zahov.app.data.Units
import bg.zahov.app.realm_db.Settings

class FirestoreSettingsAdapter : Adapter<Map<String, Any>?, Settings> {
    override fun adapt(t: Map<String, Any>?): Settings {
        return Settings().apply {
            t?.let{
                language = it["language"] as? String ?: Language.English.name
                units = it["units"] as? String ?: Units.Metric.name
                soundEffects = it["soundEffects"] as? Boolean ?: true
                theme = it["theme"] as? String ?: Theme.Dark.name
                restTimer = (it["restTimer"] as? Long)?.toInt() ?: 30
                vibration = it["vibration"] as? Boolean ?: true
                soundSettings = it["soundSettings"] as? String ?: Sound.SOUND_1.name
                updateTemplate = it["updateTemplate"] as? Boolean ?: true
                automaticSync = it["automaticSync"] as? Boolean ?: true
                fit = it["fit"] as? Boolean ?: false
            }
        }
    }
}
