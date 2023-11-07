package com.example.fitness_app.common

interface Adapter<T, K> {
    fun adapt(t: T): K?
}