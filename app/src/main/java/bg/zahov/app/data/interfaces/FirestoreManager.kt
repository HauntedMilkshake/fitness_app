package bg.zahov.app.data.interfaces

import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Measurement
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.data.model.Measurements
import bg.zahov.app.data.model.User
import bg.zahov.app.data.model.Workout
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface FirestoreManager {

    fun initUser(id: String)

    suspend fun createFirestore(username: String, userId: String)

    fun getUser(): Flow<User>

    fun getWorkouts(): Flow<List<Workout>>

    fun getTemplateWorkouts(): Flow<List<Workout>>

    fun getWorkoutById(id: String): Flow<Workout>

    fun getTemplateWorkoutByName(name: String): Flow<Workout>

    fun getTemplateExercises(): Flow<List<Exercise>>

    suspend fun addTemplateExercise(newExercise: Exercise)

    suspend fun upsertMeasurement(type: MeasurementType, measurement: Measurement)

    suspend fun getMeasurement(type: MeasurementType): Measurements

    suspend fun addTemplateWorkout(newWorkoutTemplate: Workout)

    suspend fun getPastWorkoutById(id: String): Workout

    suspend fun updateUsername(newUsername: String): Task<Void>

    suspend fun addWorkoutToHistory(newWorkout: Workout)

    suspend fun updateExerciseInBatch(exercises: List<Exercise>)

    suspend fun deleteTemplateWorkouts(workout: Workout)

    suspend fun updateTemplateWorkout(
        workoutId: String,
        newDate: LocalDateTime,
        newExercises: List<Exercise>
    )

    suspend fun deleteWorkout(workout: Workout)
}