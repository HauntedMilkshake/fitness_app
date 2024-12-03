package bg.zahov.app.data.provider

import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.local.RealmWorkoutState
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.state.ExerciseData
import bg.zahov.app.data.model.state.ExerciseHistoryInfo
import bg.zahov.app.data.provider.model.HistoryWorkout
import bg.zahov.app.data.repository.WorkoutRepositoryImpl
import bg.zahov.app.ui.workout.start.StartWorkout
import bg.zahov.app.util.getOneRepMaxes
import bg.zahov.app.util.timeToString
import bg.zahov.app.util.toExerciseData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest
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
    private val errorHandler = ServiceErrorHandlerImpl.getInstance()

    /**
     * Returns the last performed workout of the user [lastWorkoutPerformed] if any that is
     * initialized in [addWorkoutToHistory]
     */
    override fun getLastWorkout(): HistoryWorkout? {
        return lastWorkoutPerformed?.toHistoryWorkout()
    }

    override suspend fun getTemplateWorkouts(): Flow<List<Workout>> =
        workoutRepo.getTemplateWorkouts()

    override suspend fun getPastWorkouts(): Flow<List<Workout>> = workoutRepo.getPastWorkouts()

    override suspend fun addTemplateWorkout(newWorkout: Workout) =
        workoutRepo.addTemplateWorkout(newWorkout)

    /**
     * Fetches the list of template exercises from the repository.
     *
     * This function makes a call to the `workoutRepo` to retrieve a list of exercises.
     *
     * @return A [Flow] emitting a [List] of [Exercise] objects. The flow will emit the list of exercises from the repository,
     */
    override suspend fun getTemplateExercises(): Flow<List<Exercise>> =
        workoutRepo.getTemplateExercises()

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getExerciseByName(name: String): Flow<Exercise?> =
        workoutRepo.getTemplateExercises().mapLatest { templates ->
            templates.find { it.name == name }
        }


    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getExercisesByNames(name: List<String>): Flow<List<Exercise>> =
        workoutRepo.getTemplateExercises().mapLatest { templates ->
            name.mapNotNull { name -> templates.find { it.name == name } }
        }


    override suspend fun getWrappedExercises(): Flow<List<ExerciseData>> =
        workoutRepo.getTemplateExercises()
            .mapNotNull { exercise -> exercise.map { it.toExerciseData() } }

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

    override suspend fun setClickedTemplateExercise(item: ExerciseData) {
        getPastWorkouts().collect { workout ->
            val resultsList = workout.mapNotNull { workout1 ->
                workout1.exercises.find { workoutExercise -> workoutExercise.name == item.name }
                    ?.let { previousExercise ->
                        ExerciseHistoryInfo(
                            workoutName = workout1.name,
                            lastPerformed = workout1.date.toFormattedString(),
                            sets = previousExercise.sets,
                            setsPerformed = previousExercise.sets.joinToString(separator = "\n") {
                                "${it.secondMetric ?: 0} x ${it.firstMetric}"
                            },
                            oneRepMaxes = previousExercise.getOneRepMaxes(),
                            date = workout1.date
                        )
                    }
            }.sortedBy { it.date }
            _exerciseHistory.value = resultsList
            getExerciseByName(item.name).collect {
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

    /**
     * Retrieves a list of start workouts from the template workouts.
     *
     * This function fetches the template workouts by calling [getTemplateWorkouts] and converts each workout into a
     * [StartWorkout] object.
     *
     * @return A [Flow] emitting a [List] of [Workout] mapped to [StartWorkout] objects. If the data retrieval is successful, the flow will emit
     *         a list of [StartWorkout] objects created from the template workouts.
     */
    override suspend fun getStartWorkouts(): Flow<List<StartWorkout>> =
        getTemplateWorkouts().mapNotNull { workouts -> workouts.map { it.toStartWorkout() } }

    /**
     * Retrieves a flow of workouts from the current month along with their count.
     *
     * This function filters the workouts to include only those that occurred in the
     * current month.
     *
     * @return A [Flow] emitting a [Pair] of a list of [Workout] objects and the
     * number of workouts in the current month.
     */
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
 * Converts a `Workout` object to a `StartWorkout` object.
 */
fun Workout.toStartWorkout(): StartWorkout = StartWorkout(
    id = this.id,
    name = this.name,
    date = this.date,
    exercises = this.exercises.map { if (it.sets.isNotEmpty()) "${it.sets.size} x " else "" + it.name },
    note = this.note ?: "",
    personalRecords = this.personalRecords.toString()
)

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
        exercises = this.exercises.map {
            val prefix = if (it.sets.isNotEmpty()) "${it.sets.size} x " else ""
            prefix + it.name
        },
        bestSets = this.exercises.map {
            "${it.bestSet.firstMetric ?: 0} x ${it.bestSet.secondMetric ?: 0}"
        },
        personalRecords = this.personalRecords.toString()
    )
}

fun LocalDateTime.toFormattedString(): String =
    this.format(DateTimeFormatter.ofPattern("HH:mm, d MMMM", Locale.getDefault()))