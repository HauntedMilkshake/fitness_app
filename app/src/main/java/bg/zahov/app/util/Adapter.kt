package bg.zahov.app.util

interface Adapter<T, K> {
    fun adapt(t: T): K?
}