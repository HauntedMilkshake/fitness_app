package bg.zahov.app.data.provider

import androidx.compose.animation.core.rememberTransition
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.local.RealmWorkoutState
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.provider.model.HistoryWorkout
import bg.zahov.app.data.repository.WorkoutRepositoryImpl
import bg.zahov.app.ui.exercise.ExercisesWrapper
import bg.zahov.app.ui.exercise.info.history.ExerciseHistoryInfo
import bg.zahov.app.util.getOneRepMaxes
import bg.zahov.app.util.timeToString
import bg.zahov.app.util.toExerciseWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class WorkoutProviderImpl : WorkoutProvider {
    companion object {

        private var instance: WorkoutProviderImpl? = null
        fun getInstance() = instance ?: WorkoutProviderImpl().also { instance = it }
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

    override suspend fun getExerciseByName(name: String): Exercise? {
        var foundExercise: Exercise? = null
        workoutRepo.getTemplateExercises()
            .collect{templates->
                templates.find { it.name == name }.let { foundExercise = it }
                return@collect
            }
        return foundExercise
    }
    override suspend fun getExercisesByWrapper(exercises: List<ExercisesWrapper>): List<Exercise> {
        val foundExercises: MutableList<Exercise> = mutableListOf()

        workoutRepo.getTemplateExercises()
            .collect { templates ->
                exercises.forEach { exercise ->
                    templates.find { exercise.name == it.name }?.let { foundExercises.add(it) }
                }
                return@collect
            }

        return foundExercises.toList()
    }

    override suspend fun getWrappedExercises(): Flow<List<ExercisesWrapper>> =
        workoutRepo.getTemplateExercises()
            .mapNotNull { exercise -> exercise.map { it.toExerciseWrapper() } }

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

    override suspend fun setClickedTemplateExercise(item: ExercisesWrapper) {
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
            getExerciseByName(item.name).let {
                _clickedExercise.emit(it)
            }
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

    override suspend fun getCurrentMonthWorkouts(): Flow<List<Workout>> {
        return workoutRepo.getPastWorkouts()
            .mapNotNull { workouts -> workouts.filter { workout -> workout.date.month == LocalDate.now().month } }

    }


    override suspend fun getHistoryWorkouts(): Flow<List<HistoryWorkout>> =
        getPastWorkouts().mapNotNull { workouts -> workouts.map { it.toHistoryWorkout() } }

    override suspend fun clearWorkoutState() {
        workoutRepo.clearWorkoutState()
    }
}


/**
 * Converts a [Workout] object to a [HistoryWorkout] object.
 *
 * This extension function transforms a [Workout] instance into a [HistoryWorkout] instance by extracting
 * relevant data and formatting it appropriately.
 *
 * @return A [HistoryWorkout] instance populated with data from this [Workout].
 */
fun Workout.toHistoryWorkout(): HistoryWorkout {
    return HistoryWorkout(
        id = this.id,
        name = this.name,
        duration = this.duration?.timeToString() ?: "00:00:00",
        volume = (this.volume ?: 0.0).toString(),
        date = this.date.toFormattedString(),
        exercises = this.exercises.map { "${if (it.sets.isNotEmpty()) "${it.sets.size} x " else ""}${it.name} " },
        bestSets = this.exercises.map {
            "${it.bestSet.firstMetric ?: 0} x ${it.bestSet.secondMetric ?: 0}"
        },
        personalRecords = this.personalRecords.toString()
    )
}

fun LocalDateTime.toFormattedString(): String =
    this.format(DateTimeFormatter.ofPattern("HH:mm, d MMMM", Locale.getDefault()))