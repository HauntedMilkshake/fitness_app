package bg.zahov.app.data.local

import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.Language
import bg.zahov.app.data.model.LanguageKeys
import bg.zahov.app.data.model.SetType
import bg.zahov.app.data.model.Sound
import bg.zahov.app.data.model.SoundKeys
import bg.zahov.app.data.model.Theme
import bg.zahov.app.data.model.ThemeKeys
import bg.zahov.app.data.model.Units
import bg.zahov.app.data.model.UnitsKeys
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject

class Settings : RealmObject {
    var language: String = Language.fromKey(LanguageKeys.ENGLISH)
    var units: String = Units.fromKey(UnitsKeys.METRIC)
    var soundEffects: Boolean = true
    var theme: String = Theme.fromKey(ThemeKeys.DARK)
    var restTimer: Int = 30
    var vibration: Boolean = true
    var soundSettings: String = Sound.fromKey(SoundKeys.SOUND_1)
    var updateTemplate: Boolean = true
    var enableWatch: Boolean = false
    var automaticSync: Boolean = true
}

class RealmWorkoutState : RealmObject {
    var id: String = "default"
    var name: String = "default"
    var duration: Long = 0L
    var volume: Double = 0.0
    var date: String = ""
    var isTemplate: Boolean = false
    var exercises: RealmList<RealmExercise> = realmListOf()
    var note: String? = null
    var personalRecords = 0
    var restTimerStart: String = ""
    var restTimerEnd: String = ""
    var timeOfStop: String = ""
}

class RealmExercise : RealmObject {
    var name: String = "default"
    var bodyPart: String = BodyPart.Other.key
    var category: String = Category.None.key
    var isTemplate: Boolean = false
    var sets: RealmList<RealmSets> = realmListOf()
    var bestSet: RealmSets? = null
    var note: String? = null
}

class RealmSets : RealmObject {
    var type: String = SetType.DEFAULT.key
    var firstMetric: Double = 0.0
    var secondMetric: Int = 0
}

object RealmTimePattern {
    const val realmTimePattern = "yyyy-MM-dd HH:mm:ss"
}