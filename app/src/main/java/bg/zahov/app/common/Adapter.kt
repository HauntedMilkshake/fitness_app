package bg.zahov.app.common

interface Adapter<T, K> {
    fun adapt(t: T): K?
}