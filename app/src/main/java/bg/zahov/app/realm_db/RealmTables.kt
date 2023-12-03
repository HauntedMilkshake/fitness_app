package bg.zahov.app.realm_db

import bg.zahov.app.data.Language
import bg.zahov.app.data.Sound
import bg.zahov.app.data.Theme
import bg.zahov.app.data.Units
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject

class User: RealmObject {
    var username: String? = null
    var numberOfWorkouts: Int? = null
    var workouts: RealmList<Workout> = realmListOf()
    var customExercises: RealmList<Exercise> = realmListOf()
    var settings: Settings? = null
}
class Workout: RealmObject{
    var duration: Double? = null
    var totalVolume: Double? = null
    var numberOfPrs: Int? = null
    var workoutName: String? = null
    var date: String? = null
    var exercises: RealmList<Exercise> = realmListOf()
    var count: Int? = null
}
class Exercise: RealmObject{
    var bodyPart: String? = null
    var category: String? = null
    var exerciseName: String? = null
    var sets: RealmList<Sets> = realmListOf()
}
//depends on Exercise category
class Sets: RealmObject{
    var firstMetric: Int? = null
    var secondMetric: Int? = null
}
class Settings: RealmObject{
    var language: String? = Language.English.name
    var weight: String? = Units.Metric.name
    var distance: String? = Units.Metric.name
    var soundEffects: Boolean? = true
    var theme: String? = Theme.Dark.name
    var restTimer: Int? = 30
    var vibration: Boolean? = true
    var soundSettings: String? = Sound.SOUND_1.name
    var updateTemplate: Boolean? = true
//    var sync: Boolean = true
    var fit: Boolean? = false

}