package bg.zahov.app.realm_db

import bg.zahov.app.data.Language
import bg.zahov.app.data.Sound
import bg.zahov.app.data.Theme
import bg.zahov.app.data.Units
import bg.zahov.app.utils.equalsTo
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

//Because each user has his own file there is no need for relationships other than for optimizations
class User : RealmObject {
    var username: String? = null
    var numberOfWorkouts: Int? = null
}

//If a workout is "template" then we need to store exercises that can react to edits, so therefore we use exerciseIds
//This ensures that if we update a template exercise all of the workouts that have it would get updated as well
//otherwise we would like to have the historically accurate data
class Workout : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var duration: Double? = null
    var totalVolume: Double? = null
    var numberOfPrs: Int? = null
    var workoutName: String? = null
    var date: String? = null
    var exerciseIds: RealmList<String> = realmListOf()
    var exercises: RealmList<Exercise> = realmListOf()
    var count: Int? = null
    var isTemplate: Boolean? = null
}

//If an exercise is "template" sets is empty and it has an id so that we can generally edit all exercise templates
//otherwise it is an exercise performed during a workout we need its history
class Exercise : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var bodyPart: String? = null
    var category: String? = null
    var exerciseName: String? = null
    var isTemplate: Boolean? = null
    var sets: RealmList<Sets> = realmListOf()

}

//Metrics determined by category
//Barbell, Dumbbell, Machine, Cable ( first metric - reps, second - kg)
//additional/assisted weight (first metric +/- weight, second - reps)
//reps only - first metric = null
//cardio/timed - both have time but emphasize different things
class Sets : RealmObject {
    var firstMetric: Int? = null
    var secondMetric: Int? = null
}

class Settings : RealmObject {
    var language: String = Language.English.name
    var units: String = Units.Metric.name
    var soundEffects: Boolean = true
    var theme: String = Theme.Dark.name
    var restTimer: Int = 30
    var vibration: Boolean = true
    var soundSettings: String = Sound.SOUND_1.name
    var updateTemplate: Boolean = true
    var fit: Boolean = false
    var automaticSync: Boolean = true
}