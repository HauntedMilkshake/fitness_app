package com.example.fitness_app.realm_db

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class User: RealmObject {
    @PrimaryKey
    var userId: String? = null
    var username: String? = null
    var numberOfWorkouts: Int? = null
    var workouts: RealmList<Workout> = realmListOf()
}
class Workout: RealmObject{
    var duration: Double? = null
    var totalVolume: Double? = null
    var numberOfPrs: Int? = null
    var workoutName: String? = null
    var exercises: RealmList<Exercise> = realmListOf()
}
class Exercise: RealmObject{
    var type: String? = null
    var exerciseName: String? = null
    var sets: RealmList<Sets> = realmListOf()
}
//depends on enum type
class Sets: RealmObject{
    var firstMetric: Int? = null
    var secondMetric: Int? = null
}