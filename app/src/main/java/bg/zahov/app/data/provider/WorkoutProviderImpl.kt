package bg.zahov.app.data.provider

import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.local.RealmWorkoutState
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.repository.WorkoutRepositoryImpl
import bg.zahov.app.ui.exercise.info.history.ExerciseHistoryInfo
import bg.zahov.app.util.getOneRepMaxes
import bg.zahov.app.util.toFormattedString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull
import java.time.LocalDateTime

class WorkoutProviderImpl : WorkoutProvider {
    companion object {

        @Volatile
        private var instance: WorkoutProviderImpl? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: WorkoutProviderImpl().also { instance = it }
        }
    }

    private var lastWorkoutPerformed: Workout? = null
    private val _clickedExercise = MutableStateFlow<Exercise?>(null)
    private val clickedExercise: Flow<Exercise?>
        get() = _clickedExercise

    private val _exerciseHistory = MutableStateFlow<List<ExerciseHistoryInfo>>(listOf())
    private val exerciseHistory: Flow<List<ExerciseHistoryInfo>>
        get() = _exerciseHistory
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

    override suspend fun setClickedTemplateExercise(item: Exercise) {
        getPastWorkouts().collect { workout ->
            val resultsList = workout.mapNotNull {
                it.exercises.find { workoutExercise -> workoutExercise.name == item.name }
                    ?.let { previousExercise ->
                        ExerciseHistoryInfo(
                            workoutId = it.id,
                            workoutName = it.name,
                            lastPerformed = it.date.toFormattedString(),
                            sets = previousExercise.sets,
                            oneRepMaxes = previousExercise.getOneRepMaxes(),
                            date = it.date
                        )
                    }
            }.sortedBy { it.date }
            _exerciseHistory.value = resultsList
            _clickedExercise.emit(item)
        }
    }

    override suspend fun getClickedTemplateExercise() = clickedExercise.mapNotNull { it }

    override suspend fun getExerciseHistory(): Flow<List<ExerciseHistoryInfo>> = exerciseHistory


    override suspend fun getPreviousWorkoutState(): RealmWorkoutState? =
        workoutRepo.getPastWorkoutState()

    override suspend fun addWorkoutState(realmWorkoutState: RealmWorkoutState) {
        workoutRepo.addWorkoutState(realmWorkoutState)
    }

    override suspend fun updateTemplateWorkout(
        workoutId: String,
        date: LocalDateTime,
        newExercises: List<Exercise>,
    ) {
        workoutRepo.updateTemplateWorkout(workoutId, date, newExercises)
    }

    override suspend fun clearWorkoutState() {
        workoutRepo.clearWorkoutState()
    }
}
