package bg.zahov.app.data.remote

import android.util.Log
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.FirestoreFields
import bg.zahov.app.data.model.User
import bg.zahov.app.data.model.Workout
import bg.zahov.app.util.toFirestoreMap
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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

    fun initUser(id: String) {
        userId = id
    }

    suspend fun createFirestore(username: String) = withContext(Dispatchers.IO) {
        firestore.collection(USERS_COLLECTION).document(userId).set(User(username).toFirestoreMap())
    }

    private suspend fun <T> getNonObservableDocData(reference: DocumentReference, mapper: (Map<String, Any>?) -> T): T {
        try {
            return mapper(reference.get().await().data)
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun <T> getDocData(
        reference: DocumentReference,
        mapper: (Map<String, Any>?) -> T,
    ): Flow<T> = channelFlow {
        try {
            val result = reference.get().await()

            trySend(mapper(result.data))
        } catch (e: CancellationException) {
            close()
            throw e
        }
    }

    private suspend fun <T> getCollectionData(
        reference: CollectionReference,
        mapper: (Map<String, Any>?) -> T,
    ): Flow<List<T>> = channelFlow {
        try {
            val snapshots = reference.get().await()

            val results = snapshots.documents.map {
                async {
                    getDocData(it.reference, mapper).first()
                }
            }.awaitAll()

            trySend(results)

        } catch (e: CancellationException) {
            close()
            throw e
        }
    }

    suspend fun getUser(): Flow<User> =
        getDocData(firestore.collection(USERS_COLLECTION).document(userId)) { info ->
            User.fromFirestoreMap(info) ?: throw CriticalDataNullException("Critical data missing!")
        }

    suspend fun getWorkouts(): Flow<List<Workout>> = getCollectionData(
        firestore.collection(USERS_COLLECTION).document(userId).collection(WORKOUTS_SUB_COLLECTION)
    ) { info ->
        Workout.fromFirestoreMap(info) ?: throw CriticalDataNullException("Critical data missing!")
    }

    suspend fun getTemplateWorkouts(): Flow<List<Workout>> = getCollectionData(
        firestore.collection(USERS_COLLECTION).document(userId)
            .collection(TEMPLATE_WORKOUTS_SUB_COLLECTION)
    ) { info ->
        Workout.fromFirestoreMap(info)
            ?: throw CriticalDataNullException("Critical data missing!")
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

    suspend fun getTemplateExercises(): Flow<List<Exercise>> = getCollectionData(
        firestore.collection(USERS_COLLECTION).document(userId).collection(TEMPLATE_EXERCISES)
    ) {
        Exercise.fromFirestoreMap(it)
            ?: throw CriticalDataNullException("Critical data missing!")
    }

    suspend fun addTemplateExercise(newExercise: Exercise) =
        withContext(Dispatchers.IO) {
            firestore.collection(USERS_COLLECTION).document(userId)
                .collection(TEMPLATE_EXERCISES).document(newExercise.name)
                .set(newExercise.toFirestoreMap())
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

    private fun deleteFirestore() {
//        return userDocRef.listCollections().fold(Tasks.forResult(null as Void?)) { task, collectionRef ->
//            task.continueWithTask { _ ->
//                // Delete all documents within the subcollection
//                val deleteSubcollectionTask = deleteCollection(collectionRef)
//
//                // Delete the subcollection itself
//                deleteSubcollectionTask.continueWithTask {
//                    collectionRef.delete()
//                }
//            }
//        }.continueWithTask { _ ->
//            // Delete the main document
//            documentRef.delete()
//        }
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
                .addOnSuccessListener {
                    Log.d("LISTEN", "deleted")
                }
        }
    }

    suspend fun deleteWorkout(workout: Workout) = withContext(Dispatchers.IO) {
        firestore.collection(USERS_COLLECTION).document(userId).collection(WORKOUTS_SUB_COLLECTION)
            .document(workout.id).delete()
    }
}