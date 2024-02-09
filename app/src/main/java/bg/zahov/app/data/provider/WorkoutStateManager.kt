package bg.zahov.app.data.provider

import android.util.Log
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.WorkoutState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class WorkoutStateManager {
    companion object {

        @Volatile
        private var instance: WorkoutStateManager? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: WorkoutStateManager().also { instance = it }
        }
    }

    private var state: WorkoutState = WorkoutState.INACTIVE

    //MIGHT BE A GOOD IDEA TO KEEP TRACK OF THE WORKOUT HERE( ie save the in progress workout here if the vm doesn't do it already)
    private var template: Workout? = null

    suspend fun setWorkoutState(newState: WorkoutState) = withContext(Dispatchers.Default) {
        Log.d("STATE", newState.name)
        state = newState
    }

    suspend fun setTemplate(workout: Workout) = withContext(Dispatchers.Default) {
        template = workout
    }

    suspend fun getState() = flow {
        emit(state)
    }

    suspend fun getTemplate() = withContext(Dispatchers.Default) {
        template
    }
}