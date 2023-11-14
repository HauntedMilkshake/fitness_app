package com.example.fitness_app

import android.app.Application
import com.example.fitness_app.realm_db.Exercise
import com.example.fitness_app.realm_db.Sets
import com.example.fitness_app.realm_db.User
import com.example.fitness_app.realm_db.Workout
import com.google.firebase.Firebase
import com.google.firebase.initialize
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
//        val config = RealmConfiguration.create(schema = setOf(User::class, Workout::class, Exercise::class, Sets::class))
//        val realm = Realm.open(config)
    }
}