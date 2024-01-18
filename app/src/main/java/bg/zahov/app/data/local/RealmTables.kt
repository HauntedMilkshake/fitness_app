package bg.zahov.app.data.local

import bg.zahov.app.data.model.Language
import bg.zahov.app.data.model.Sound
import bg.zahov.app.data.model.Theme
import bg.zahov.app.data.model.Units
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

// FIXME Realm is an object database so maybe revise the file name
//  One big advantage of Kotlin is that there is a distinction between nullable and non-nullable objects
//  Whenever designing model classes prefer to have non-nullable fields and consider carefully whether
//  it makes sense to have a null value
//  Although Realm's documentation encourages you to use these objects directly in your project, doing that
//  will lead to tight coupling between your choice of storage implementation and your business logic. It's
//  better to keep the usage of these objects limited to the scope of the Realm storage infrastructure and
//  use separate model classes (your domain objects) for your business logic and map between types when
//  persisted data is served to clients (your ViewModels by a repository)

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
    var type: String? = null
    var firstMetric: Int? = null
    var secondMetric: Int? = null
}

class Settings : RealmObject {
    // FIXME you never want to use Enum.name for enum value serialization. If you look at the docs
    //  this is the enum value name exactly as declared in the code and this name will change whenever
    //  you refactor and rename that value. The same goes for Enum.ordinal - this is the 0-based position of
    //  the value as it appears in the enum declaration. Instead you should add a key value like so:
//    enum class Fruit(val key: String) {
//        Apple("apple"),
//        Banana("banana");
//        // And a conversion method for convenience:
//        companion object {
//            fun fromKey(key: String) = values().firstOrNull { it.key == key }
//        }
//    }

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