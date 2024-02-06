package bg.zahov.app.data.provider

import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.repository.WorkoutRepositoryImpl
import kotlinx.coroutines.flow.Flow

class WorkoutProviderImpl: WorkoutProvider {
    companion object {

        @Volatile
        private var instance: WorkoutProviderImpl? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: WorkoutProviderImpl().also { instance = it }
        }
    }

    private val workoutRepo = WorkoutRepositoryImpl.getInstance()
    override suspend fun getTemplateWorkouts(): Flow<List<Workout>> = workoutRepo.getTemplateWorkouts()

    override suspend fun getPastWorkouts(): Flow<List<Workout>> = workoutRepo.getPastWorkouts()

    override suspend fun addWorkout(newWorkout: Workout) = workoutRepo.addWorkout(newWorkout)

    override suspend fun getTemplateExercises(): Flow<List<Exercise>> = workoutRepo.getTemplateExercises()

    override suspend fun addTemplateExercise(newExercise: Exercise) = workoutRepo.addTemplateExercise(newExercise)
}