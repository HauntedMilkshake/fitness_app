package bg.zahov.app.data.provider

import bg.zahov.app.data.interfaces.WorkoutTopBarHandler
import kotlinx.coroutines.flow.MutableStateFlow

class WorkoutTopBarProvider : WorkoutTopBarHandler {
    companion object {
        private var instance: WorkoutTopBarProvider? = null

        fun getInstance() = instance ?: WorkoutTopBarProvider().also { instance = it }
    }

    override val shouldFinish = MutableStateFlow(false)
    override fun tryToFinish() {
        shouldFinish.value = true
    }

    override fun completeFinishAttempt() {
        shouldFinish.value = false
    }
}