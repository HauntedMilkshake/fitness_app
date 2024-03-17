package bg.zahov.app.data.provider

import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.repository.WorkoutRepositoryImpl
import bg.zahov.app.ui.exercise.info.history.ExerciseHistoryInfo
import bg.zahov.app.ui.exercise.info.history.ExerciseHistoryViewModel
import bg.zahov.app.util.getOneRepMaxes
import bg.zahov.app.util.toFormattedString
import kotlinx.coroutines.flow.Flow

class WorkoutProviderImpl : WorkoutProvider {
    companion object {

        @Volatile
        private var instance: WorkoutProviderImpl? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: WorkoutProviderImpl().also { instance = it }
        }
    }

    private var lastWorkoutPerformed: Workout? = null
    private var clickedExercise: Exercise? = null
    private val workoutRepo = WorkoutRepositoryImpl.getInstance()

    fun getLastWorkout(): Workout? = lastWorkoutPerformed
    override suspend fun getTemplateWorkouts(): Flow<List<Workout>> =
        workoutRepo.getTemplateWorkouts()

    override suspend fun getPastWorkouts(): Flow<List<Workout>> = workoutRepo.getPastWorkouts()

    override suspend fun addTemplateWorkout(newWorkout: Workout) =
        workoutRepo.addTemplateWorkout(newWorkout)

    override suspend fun getTemplateExercises(): Flow<List<Exercise>> =
        workoutRepo.getTemplateExercises()

    override suspend fun addTemplateExercise(newExercise: Exercise) =
        workoutRepo.addTemplateExercise(newExercise)

    override suspend fun addWorkoutToHistory(newWorkout: Workout) {
        lastWorkoutPerformed = newWorkout
        workoutRepo.addWorkoutToHistory(newWorkout)
    }

    override suspend fun deleteTemplateWorkout(workout: Workout) {
        workoutRepo.deleteTemplateWorkout(workout)
    }

    override suspend fun deleteWorkout(workout: Workout) {
        workoutRepo.deleteWorkout(workout)
    }

    override suspend fun getWorkoutById(id: String) = workoutRepo.getWorkoutById(id)
    override suspend fun updateExercises(exercises: List<Exercise>) {
        workoutRepo.updateExercises(exercises)
    }

    override suspend fun getTemplateWorkoutByName(name: String): Flow<Workout> =
        workoutRepo.getWorkoutByName(name)

    override suspend fun getPastWorkoutById(id: String): Workout =
        workoutRepo.getPastWorkoutById(id)

    override fun setClickedTemplateExercise(item: Exercise) {
        clickedExercise = item
    }

    override fun getClickedTemplateExercise(): Exercise = requireNotNull(clickedExercise)

    override suspend fun getExerciseHistory(): List<ExerciseHistoryInfo> {
        var resultsList = listOf<ExerciseHistoryInfo>()
        getPastWorkouts().collect { workout ->
            resultsList = workout.mapNotNull {
                it.exercises.find { workoutExercise -> workoutExercise.name == clickedExercise?.name }
                    ?.let { previousExercise ->
                        ExerciseHistoryInfo(
                            workoutId = it.id,
                            workoutName = it.name,
                            lastPerformed = it.date.toFormattedString(),
                            sets = previousExercise.sets,
                            oneRepMaxes = previousExercise.getOneRepMaxes()
                        )
                    }
            }
        }
        return resultsList
    }
}