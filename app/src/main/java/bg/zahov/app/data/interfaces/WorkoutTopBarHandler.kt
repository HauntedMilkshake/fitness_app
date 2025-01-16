package bg.zahov.app.data.interfaces

import kotlinx.coroutines.flow.StateFlow

interface WorkoutTopBarHandler {
    val shouldFinish: StateFlow<Boolean>
    fun tryToFinish()
    fun completeFinishAttempt()
}