package bg.zahov.app.data.remote

import android.util.Log
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
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class FirestoreManager {
    companion object {
        const val USERS_COLLECTION = FirestoreFields.USERS
        const val TEMPLATE_EXERCISES = FirestoreFields.USER_TEMPLATE_EXERCISES
        const val WORKOUTS_SUB_COLLECTION = FirestoreFields.USER_WORKOUTS
        const val TEMPLATE_WORKOUTS_SUB_COLLECTION = FirestoreFields.USER_TEMPLATE_WORKOUTS
        const val MEASUREMENTS_COLLECTION = FirestoreFields.MEASUREMENTS_COLLECTION

        @Volatile
        private var instance: FirestoreManager? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: FirestoreManager().also { instance = it }
        }
    }

    private lateinit var userId: String

    private val firestore = FirebaseFirestore.getInstance()


    //    private val listenerOptions =
//        SnapshotListenOptions.Builder().setMetadataChanges(MetadataChanges.INCLUDE)
//            .setSource(ListenSource.CACHE).build()
    private val _history = MutableSharedFlow<List<Workout>>()
    private val history: SharedFlow<List<Workout>>
        get() = _history
    private val _templateWorkout = MutableSharedFlow<List<Workout>>()
    private val templateWorkout: SharedFlow<List<Workout>>
        get() = _templateWorkout
    private val _templateExercises = MutableSharedFlow<List<Exercise>>()
    private val templateExercises: SharedFlow<List<Exercise>>
        get() = _templateExercises
    private var historyListener: ListenerRegistration? = null
    private var templateWorkoutListener: ListenerRegistration? = null
    private var measurementsListener: ListenerRegistration? = null
    private var templateExercisesListener: ListenerRegistration? = null
    fun initUser(id: String) {
        userId = id
    }

    suspend fun createFirestore(username: String, userId: String) = withContext(Dispatchers.IO) {
        firestore.collection(USERS_COLLECTION).document(userId).set(User(username).toFirestoreMap())
        initUser(userId)
    }

    //legit
    private suspend fun <T> getNonObservableDocData(
        reference: DocumentReference,
        mapper: (Map<String, Any>?) -> T,
    ): T {
        val snapshot = reference.get().await()
        return mapper(snapshot.data)
    }

    //fucking garbage
    private suspend fun <T> getDocData(
        reference: DocumentReference,
        mapper: (Map<String, Any>?) -> T,
    ): Flow<T> = channelFlow {
        try {
            val result = reference.get().await()
            result.data?.let {
                trySend(mapper(it))
            }
        } catch (e: CancellationException) {
            close()
            throw e
        }
    }

    //legit
    private suspend fun <T> getNonObservableCollectionData(
        reference: CollectionReference,
        mapper: (Map<String, Any>?) -> T,
    ): List<T> = withContext(Dispatchers.IO) {
        try {
            val snapshots = reference.get().await()

            val results = snapshots.documents.map {
                async {
                    getNonObservableDocData(it.reference, mapper)
                }
            }.awaitAll()

            results
        } catch (e: CancellationException) {
            throw e
        }
    }

    //less and more garbage at the same time
    private suspend fun <T> getCollectionDataLISTENERS(
        reference: CollectionReference,
        emitterFlow: MutableSharedFlow<List<T>>,
        collectorFlow: SharedFlow<List<T>>,
        listenerIdentifier: Listeners,
        mapper: (Map<String, Any>?) -> T,
    ): Flow<List<T>> {
        try {
            var ref = getListenerReference(listenerIdentifier)
            if (ref == null) {
                ref = reference.addSnapshotListener { value, error ->
                    error?.let {
                        //throw or something to handle this
                        return@addSnapshotListener
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        emitterFlow.emit(value?.mapNotNull { mapper(it.data) }.orEmpty())
                    }
                }
//                assignListenerReference(listenerIdentifier, ref)
            }
            Log.d("history listener", historyListener.toString())

            return collectorFlow
        } catch (e: CancellationException) {
            throw e
        }
    }

    private fun getListenerReference(identifier: Listeners) = when (identifier) {
        Listeners.History -> historyListener
        Listeners.TemplateWorkouts -> templateWorkoutListener
        Listeners.TemplateExercises -> templateExercisesListener
        Listeners.Measurements -> measurementsListener
    }

    private fun assignListenerReference(identifier: Listeners, ref: ListenerRegistration) =
        when (identifier) {
            Listeners.History -> historyListener = ref
            Listeners.TemplateWorkouts -> templateWorkoutListener = ref
            Listeners.TemplateExercises -> templateExercisesListener = ref
            Listeners.Measurements -> measurementsListener = ref
        }

    suspend fun getUser(): Flow<User> =
        getDocData(firestore.collection(USERS_COLLECTION).document(userId)) { info ->
            User.fromFirestoreMap(info)
        }

    //    suspend fun getWorkouts(): Flow<List<Workout>> = getCollectionDataLISTENERS(
//        firestore.collection(USERS_COLLECTION).document(userId).collection(WORKOUTS_SUB_COLLECTION)
//    ) { info ->
//        Workout.fromFirestoreMap(info)
//    }
    suspend fun getWorkouts(): Flow<List<Workout>> = getCollectionDataLISTENERS(
        firestore.collection(USERS_COLLECTION).document(userId).collection(WORKOUTS_SUB_COLLECTION),
        _history,
        history,
        Listeners.History
    ) { info ->
        Workout.fromFirestoreMap(info)
    }

    suspend fun getTemplateWorkouts(): Flow<List<Workout>> = getCollectionDataLISTENERS(
        firestore.collection(USERS_COLLECTION).document(userId)
            .collection(TEMPLATE_WORKOUTS_SUB_COLLECTION),
        _templateWorkout,
        templateWorkout,
        Listeners.TemplateWorkouts
    ) { info ->
        Workout.fromFirestoreMap(info)
    }

    //TODO(we could make the request work if the initial result is null to search for the template it belongs to so it isn't null
    suspend fun getWorkoutById(id: String): Flow<Workout> = getDocData(
        firestore.collection(
            USERS_COLLECTION
        ).document(userId).collection(WORKOUTS_SUB_COLLECTION).document(id)
    ) { info ->
        Workout.fromFirestoreMap(info)
    }

    suspend fun getTemplateWorkoutByName(name: String): Flow<Workout> = getDocData(
        firestore.collection(USERS_COLLECTION).document(userId).collection(
            TEMPLATE_WORKOUTS_SUB_COLLECTION
        ).document(name)
    ) {
        Workout.fromFirestoreMap(it)
    }

    suspend fun getTemplateExercises(): Flow<List<Exercise>> = getCollectionDataLISTENERS(
        firestore.collection(USERS_COLLECTION).document(userId).collection(TEMPLATE_EXERCISES),
        _templateExercises,
        templateExercises,
        Listeners.TemplateExercises
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


    suspend fun getMeasurement(type: MeasurementType): Flow<Measurements> = getDocData(
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

    suspend fun updateTemplateWorkout(workoutId: String, newDate: LocalDateTime, newExercises: List<Exercise>) {
        withContext(Dispatchers.IO) {
            val workoutToUpdate = getLatestWorkoutById(workoutId)
            workoutToUpdate.date = newDate
            workoutToUpdate.exercises.forEach {
                val foundExercise = newExercises.find { find -> find.name == it.name}
                if(foundExercise != null) {
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

    private fun unsubscribeListeners() {
        historyListener?.remove()
        historyListener = null
        templateWorkoutListener?.remove()
        templateWorkoutListener = null
        templateExercisesListener?.remove()
        templateExercisesListener = null
        measurementsListener?.remove()
        measurementsListener = null
    }
}

enum class Listeners {
    History, TemplateWorkouts, TemplateExercises, Measurements
}