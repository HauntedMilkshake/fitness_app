package bg.zahov.app.data.interfaces

import java.time.LocalDateTime

interface RestProvider {
    suspend fun startRest(startTime: Long, elapsedTime: Long = 0)
    suspend fun stopRest()
    suspend fun addTime(timeToAdd: Long)
    suspend fun removeTime(timeToRemove: Long)
    fun isRestActive(): Boolean
    fun getRestStartDate(): LocalDateTime
}