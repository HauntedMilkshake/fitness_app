package bg.zahov.app.data.interfaces

import kotlinx.coroutines.flow.StateFlow

interface WorkoutActionHandler {
    val shouldFinish: StateFlow<Boolean>
    fun tryToFinish()
    fun completeFinishAttempt()
}