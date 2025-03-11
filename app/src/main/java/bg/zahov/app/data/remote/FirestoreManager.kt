package bg.zahov.app.data.remote

import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.FirestoreFields
import bg.zahov.app.data.model.Measurement
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.data.model.Measurements
import bg.zahov.app.data.model.User
import bg.zahov.app.data.model.Workout
import bg.zahov.app.util.toFirestoreMap
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

class FirestoreManager @Inject constructor(private val firestore: FirebaseFirestore) {
    companion object {
        const val USERS_COLLECTION = FirestoreFields.USERS
        const val TEMPLATE_EXERCISES = FirestoreFields.USER_TEMPLATE_EXERCISES
        const val WORKOUTS_SUB_COLLECTION = FirestoreFields.USER_WORKOUTS
        const val TEMPLATE_WORKOUTS_SUB_COLLECTION = FirestoreFields.USER_TEMPLATE_WORKOUTS
        const val MEASUREMENTS_COLLECTION = FirestoreFields.MEASUREMENTS_COLLECTION
    }

    private lateinit var userId: String

    fun initUser(id: String) {
        userId = id
    }

    suspend fun createFirestore(username: String, userId: String) = withContext(Dispatchers.IO) {
        firestore.collection(USERS_COLLECTION).document(userId).set(User(username).toFirestoreMap())
        initUser(userId)
    }

    private suspend fun <T> getNonObservableDocData(
        reference: DocumentReference,
        mapper: (Map<String, Any>?) -> T,
    ): T {
        val snapshot = reference.get().await()
        return mapper(snapshot.data)
    }

    //papa bless callbackFlow :) 🤲
    private fun <T> observeCollection(
        reference: CollectionReference,
        mapper: (Map<String, Any>?) -> T,
    ): Flow<List<T>> = callbackFlow {
        val listener = reference.addSnapshotListener { value, error ->
            error?.let {
                return@addSnapshotListener
            }
            CoroutineScope(Dispatchers.IO).launch {
                send(value?.mapNotNull { mapper(it.data) }.orEmpty())
            }
        }
        awaitClose { listener.remove() }
    }

    private fun <T> observeDocument(
        reference: DocumentReference,
        mapper: (Map<String, Any>?) -> T,
    ): Flow<T> = callbackFlow {
        val listener = reference.addSnapshotListener { value, error ->
            error?.let {
                return@addSnapshotListener
            }
            CoroutineScope(Dispatchers.IO).launch {
                value?.let {
                    send(mapper(it.data))
                }
            }
        }
        awaitClose { listener.remove() }
    }

    fun getUser(): Flow<User> =
        observeDocument(firestore.collection(USERS_COLLECTION).document(userId)) { info ->
            User.fromFirestoreMap(info)
        }

    fun getWorkouts(): Flow<List<Workout>> = observeCollection(
        firestore.collection(USERS_COLLECTION).document(userId).collection(WORKOUTS_SUB_COLLECTION)
    ) { info ->
        Workout.fromFirestoreMap(info)
    }

    fun getTemplateWorkouts(): Flow<List<Workout>> = observeCollection(
        firestore.collection(USERS_COLLECTION).document(userId)
            .collection(TEMPLATE_WORKOUTS_SUB_COLLECTION)
    ) { info ->
        Workout.fromFirestoreMap(info)
    }

    fun getWorkoutById(id: String) = observeDocument(
        firestore.collection(
            USERS_COLLECTION
        ).document(userId).collection(WORKOUTS_SUB_COLLECTION).document(id)
    ) { info ->
        Workout.fromFirestoreMap(info)
    }

    fun getTemplateWorkoutByName(name: String) = observeDocument(
        firestore.collection(USERS_COLLECTION).document(userId).collection(
            TEMPLATE_WORKOUTS_SUB_COLLECTION
        ).document(name)
    ) {
        Workout.fromFirestoreMap(it)
    }

    fun getTemplateExercises(): Flow<List<Exercise>> = observeCollection(
        firestore.collection(USERS_COLLECTION).document(userId).collection(TEMPLATE_EXERCISES)
    ) {
        Exercise.fromFirestoreMap(it)
    }

    suspend fun addTemplateExercise(newExercise: Exercise) =
        withContext(Dispatchers.IO) {
            firestore.collection(USERS_COLLECTION).document(userId)
                .collection(TEMPLATE_EXERCISES).document(newExercise.name)
                .set(newExercise.toFirestoreMap())
        }

    suspend fun upsertMeasurement(type: MeasurementType, measurement: Measurement) {
        withContext(Dispatchers.IO) {
            val newMap = getLatestMeasurementsByType(type)
            val newList = newMap.measurements[type].orEmpty().toMutableList()

            val existingIndex =
                newList.indexOfFirst { it.date.year == measurement.date.year && it.date.month == measurement.date.month && it.date.dayOfMonth == measurement.date.dayOfMonth }

            if (existingIndex != -1) {
                newList[existingIndex] = measurement
            } else {
                newList.add(measurement)
            }

            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(MEASUREMENTS_COLLECTION)
                .document(type.key)
                .set(newList.toFirestoreMap())
        }
    }


    suspend fun getMeasurement(type: MeasurementType): Measurements = getNonObservableDocData(
        firestore.collection(
            USERS_COLLECTION
        ).document(userId).collection(MEASUREMENTS_COLLECTION).document(type.key)
    ) {
        Measurements.fromFirestoreMap(it, type)
    }

    suspend fun addTemplateWorkout(newWorkoutTemplate: Workout) =
        withContext(Dispatchers.IO) {
            firestore.collection(USERS_COLLECTION).document(userId)
                .collection(TEMPLATE_WORKOUTS_SUB_COLLECTION)
                .document(newWorkoutTemplate.id).set(newWorkoutTemplate.toFirestoreMap())
        }

    suspend fun getPastWorkoutById(id: String): Workout = getNonObservableDocData(
        firestore.collection(USERS_COLLECTION).document(userId).collection(WORKOUTS_SUB_COLLECTION)
            .document(id)
    ) {
        Workout.fromFirestoreMap(it)
    }

    private suspend fun getLatestMeasurementsByType(type: MeasurementType): Measurements =
        getNonObservableDocData(
            firestore.collection(
                USERS_COLLECTION
            ).document(userId).collection(MEASUREMENTS_COLLECTION).document(type.key)
        ) {
            Measurements.fromFirestoreMap(it, type)
        }

    private suspend fun getLatestWorkoutById(id: String): Workout = getNonObservableDocData(
        firestore.collection(
            USERS_COLLECTION
        ).document(userId).collection(TEMPLATE_WORKOUTS_SUB_COLLECTION).document(id)
    ) {
        Workout.fromFirestoreMap(it)
    }

    suspend fun updateUsername(newUsername: String) =
        withContext(Dispatchers.IO) {
            firestore.collection(USERS_COLLECTION).document(userId)
                .update(FirestoreFields.USER_NAME, newUsername)
        }

    suspend fun addWorkoutToHistory(newWorkout: Workout) =
        withContext(Dispatchers.IO) {
            firestore.collection(USERS_COLLECTION).document(userId)
                .collection(WORKOUTS_SUB_COLLECTION).document(newWorkout.id)
                .set(newWorkout.toFirestoreMap())
        }

    suspend fun updateExerciseInBatch(exercises: List<Exercise>) {
        withContext(Dispatchers.IO) {
            val batch = firestore.batch()
            exercises.forEach {
                batch.set(
                    firestore.collection(USERS_COLLECTION).document(userId).collection(
                        TEMPLATE_EXERCISES
                    ).document(it.name), it.toFirestoreMap()
                )
            }
            batch.commit()
        }
    }

    suspend fun deleteTemplateWorkouts(workout: Workout) {
        withContext(Dispatchers.IO) {
            firestore.collection(USERS_COLLECTION).document(userId)
                .collection(TEMPLATE_WORKOUTS_SUB_COLLECTION).document(workout.id).delete()
        }
    }

    suspend fun updateTemplateWorkout(
        workoutId: String,
        newDate: LocalDateTime,
        newExercises: List<Exercise>,
    ) {
        withContext(Dispatchers.IO) {
            val workoutToUpdate = getLatestWorkoutById(workoutId)
            workoutToUpdate.date = newDate
            workoutToUpdate.exercises.forEach {
                val foundExercise = newExercises.find { find -> find.name == it.name }
                if (foundExercise != null) {
                    it.sets = foundExercise.sets
                }
            }
            firestore.collection(USERS_COLLECTION).document(userId).collection(
                TEMPLATE_WORKOUTS_SUB_COLLECTION
            ).document(workoutId).set(workoutToUpdate.toFirestoreMap())
        }
    }

    suspend fun deleteWorkout(workout: Workout) = withContext(Dispatchers.IO) {
        firestore.collection(USERS_COLLECTION).document(userId).collection(WORKOUTS_SUB_COLLECTION)
            .document(workout.id).delete()
    }
}
