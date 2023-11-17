package com.example.app

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.initialize

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
//        val config = RealmConfiguration.create(schema = setOf(User::class, Workout::class, Exercise::class, Sets::class))
//        val realm = Realm.open(config)
    }
}