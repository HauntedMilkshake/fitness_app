package bg.zahov.app.data.mock

import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Measurement
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.data.model.Measurements
import bg.zahov.app.data.model.User
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.remote.FirestoreManager
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.dagger.Module
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime
import javax.inject.Inject

@Module
class MockFirestoreManager @Inject constructor(firestore: FirebaseFirestore) :
    FirestoreManager(firestore) {

    private val mockUser = User("test_user")
    private val mockWorkout = Workout(
        id = "1",
        name = "Test Workout",
        date = LocalDateTime.now(),
        exercises = listOf(Exercise("Push-up"))
    )
    private val mockExercise = Exercise("Push-up")
    private val mockMeasurement = Measurement(value = 10.00, date = LocalDateTime.now())
    private val mockMeasurements =
        Measurements(mapOf(MeasurementType.Weight to listOf(mockMeasurement)))

    override fun initUser(id: String) {
        // Mock initUser logic, no-op in the mock
    }

    override suspend fun createFirestore(username: String, userId: String) {}

    override fun getUser(): Flow<User> = flowOf(mockUser)

    override fun getWorkouts(): Flow<List<Workout>> = flowOf(listOf(mockWorkout))

    override fun getTemplateWorkouts(): Flow<List<Workout>> = flowOf(listOf(mockWorkout))

    override fun getWorkoutById(id: String): Flow<Workout> = flowOf(mockWorkout)

    override fun getTemplateWorkoutByName(name: String): Flow<Workout> = flowOf(mockWorkout)

    override fun getTemplateExercises(): Flow<List<Exercise>> = flowOf(listOf(mockExercise))

    override suspend fun addTemplateExercise(newExercise: Exercise) {
        // Mock the addition of a new exercise, no-op
    }

    override suspend fun upsertMeasurement(type: MeasurementType, measurement: Measurement) {
        // Mock upsert logic, no-op
    }

    override suspend fun getMeasurement(type: MeasurementType): Measurements = mockMeasurements

    override suspend fun addTemplateWorkout(newWorkoutTemplate: Workout) {
        // Mock the addition of a new workout template, no-op
    }

    override suspend fun getPastWorkoutById(id: String): Workout = mockWorkout

    override suspend fun updateUsername(newUsername: String): Task<Void> = Tasks.forResult(null)

    override suspend fun addWorkoutToHistory(newWorkout: Workout) {
        // Mock adding workout to history, no-op
    }

    override suspend fun updateExerciseInBatch(exercises: List<Exercise>) {
        // Mock batch update of exercises, no-op
    }

    override suspend fun deleteTemplateWorkouts(workout: Workout) {
        // Mock deletion of workout, no-op
    }

    override suspend fun updateTemplateWorkout(
        workoutId: String, newDate: LocalDateTime, newExercises: List<Exercise>
    ) {
        // Mock update of template workout, no-op
    }

    override suspend fun deleteWorkout(workout: Workout) {
        // Mock deletion of workout, no-op
    }
}