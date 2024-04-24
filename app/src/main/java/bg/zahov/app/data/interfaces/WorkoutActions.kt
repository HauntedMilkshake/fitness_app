package bg.zahov.app.data.interfaces

interface WorkoutActions {
    suspend fun <T> startWorkout(workout: T? = null, lastTime: Long? = null, isResuming: Boolean = false)
    suspend fun finishWorkout()
    suspend fun minimizeWorkout()
    suspend fun cancel()
    suspend fun clear()
}