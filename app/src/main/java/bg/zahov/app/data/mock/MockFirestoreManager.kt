package bg.zahov.app.data.mock

import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Measurement
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.data.model.Measurements
import bg.zahov.app.data.model.SetType
import bg.zahov.app.data.model.Sets
import bg.zahov.app.data.model.User
import bg.zahov.app.data.model.Workout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

class MockFirestoreManager {
    companion object {
        @Volatile
        private var instance: MockFirestoreManager? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: MockFirestoreManager().also { instance = it }
        }
    }

    private var userId: String = ""
    private val users = ConcurrentHashMap<String, User>()
    private val workouts = ConcurrentHashMap<String, MutableStateFlow<List<Workout>>>()
    private val templateWorkouts = ConcurrentHashMap<String, MutableStateFlow<List<Workout>>>()
    private val templateExercises = ConcurrentHashMap<String, MutableStateFlow<List<Exercise>>>()
    private val measurements = ConcurrentHashMap<String, MutableStateFlow<Measurements>>()

    init {
        val defaultUserId = "mockUserId"
        userId = defaultUserId
        users[defaultUserId] = User("Test User")

        val defaultWorkouts = listOf(
            Workout(id = "w1", name = "Morning Workout", exercises = emptyList()),
            Workout(id = "w2", name = "Evening Cardio", exercises = emptyList())
        )
        workouts[defaultUserId] = MutableStateFlow(defaultWorkouts)
        templateWorkouts[defaultUserId] = MutableStateFlow(defaultWorkouts)
        templateExercises[defaultUserId] = MutableStateFlow(
            listOf(
                Exercise(
                    name = "Push-ups",
                    sets = mutableListOf(
                        Sets(
                            type = SetType.WARMUP,
                            firstMetric = 2.0,
                            secondMetric = 5
                        )
                    )
                ),
                Exercise(
                    name = "Squats",
                    sets = mutableListOf(
                        Sets(
                            type = SetType.WARMUP,
                            firstMetric = 2.0,
                            secondMetric = 5
                        )
                    )
                )
            )
        )
        measurements[defaultUserId] = MutableStateFlow(
            Measurements(
                mapOf(
                    MeasurementType.Weight to listOf(
                        Measurement(
                            value = 70.0,
                            date = LocalDateTime.now()
                        )
                    )
                )
            )
        )
    }

    fun initUser(id: String) {
        userId = id
        users.putIfAbsent(id, User("Test User"))
        workouts.putIfAbsent(id, MutableStateFlow(emptyList()))
        templateWorkouts.putIfAbsent(id, MutableStateFlow(emptyList()))
        templateExercises.putIfAbsent(id, MutableStateFlow(emptyList()))
        measurements.putIfAbsent(id, MutableStateFlow(Measurements(emptyMap())))
    }

    fun getUser(): Flow<User> = MutableStateFlow(users[userId] ?: User("Unknown"))

    fun getWorkouts(): Flow<List<Workout>> =
        workouts[userId]?.asStateFlow() ?: MutableStateFlow(emptyList())

    fun getTemplateWorkouts(): Flow<List<Workout>> =
        templateWorkouts[userId]?.asStateFlow() ?: MutableStateFlow(emptyList())

    fun getTemplateExercises(): Flow<List<Exercise>> =
        templateExercises[userId]?.asStateFlow() ?: MutableStateFlow(emptyList())

    fun addTemplateExercise(newExercise: Exercise) {
        templateExercises[userId]?.update { it + newExercise }
    }

    fun addTemplateWorkout(newWorkoutTemplate: Workout) {
        templateWorkouts[userId]?.update { it + newWorkoutTemplate }
    }

    fun addWorkoutToHistory(newWorkout: Workout) {
        workouts[userId]?.update { it + newWorkout }
    }

    fun upsertMeasurement(type: MeasurementType, measurement: Measurement) {
        measurements[userId]?.update { current ->
            val newMap = current.measurements.toMutableMap()
            val newList = newMap[type].orEmpty().toMutableList()

            val existingIndex = newList.indexOfFirst { it.date == measurement.date }
            if (existingIndex != -1) newList[existingIndex] = measurement else newList.add(
                measurement
            )

            Measurements(newMap.apply { put(type, newList) })
        }
    }

    fun updateUsername(newUsername: String) {
        users[userId]?.let {
            users[userId] = it.copy(name = newUsername)
        }
    }

    fun deleteTemplateWorkouts(workout: Workout) {
        templateWorkouts[userId]?.update { it.filter { w -> w.id != workout.id } }
    }

    fun deleteWorkout(workout: Workout) {
        workouts[userId]?.update { it.filter { w -> w.id != workout.id } }
    }
}
