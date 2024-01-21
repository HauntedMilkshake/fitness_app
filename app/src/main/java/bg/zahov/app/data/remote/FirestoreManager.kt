package bg.zahov.app.data.remote

import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.User
import bg.zahov.app.data.model.Workout
import bg.zahov.app.util.toFirestoreMap
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreManager(private var userId: String) {
    companion object {
        const val USERS_COLLECTION = "users"
        const val TEMPLATE_EXERCISES = "templateExercises"
        const val WORKOUTS_SUB_COLLECTION = "workouts"

        @Volatile
        private var instances: MutableMap<String, FirestoreManager> = mutableMapOf()
        fun getInstance(userId: String) = synchronized(this) {
            instances.getOrPut(userId) {
                FirestoreManager(userId)
            }
        }
    }

    private val firestore = FirebaseFirestore.getInstance()

    private val userDocRef by lazy {
        firestore.collection(USERS_COLLECTION).document(userId)
    }

    private val workoutsCollectionRef by lazy {
        userDocRef.collection(WORKOUTS_SUB_COLLECTION)
    }

    private val templateExercisesCollectionRef by lazy {
        userDocRef.collection(TEMPLATE_EXERCISES)
    }

    suspend fun createFirestore(username: String) {
        try {
            userDocRef.set(User(username).toFirestoreMap(), SetOptions.merge()).await()
        } catch (e: FirebaseFirestoreException) {
            throw FirebaseFirestoreException(e.message ?: "unknown", e.code)
        }
    }
    private suspend fun <T> getData(
        reference: Any,
        mapper: (Map<String, Any>) -> T
    ): Flow<T> = callbackFlow {
        when(reference) {
            is CollectionReference -> {
                reference.get()
                    .addOnSuccessListener { result ->
                        result.documents.forEach { document ->
                            document.data?.let {
                                trySend(mapper(it))
                            } ?: close()
                        }
                    }
                    .addOnFailureListener {
                        close(it)
                    }
            }

            is DocumentReference -> {
                reference.get()
                    .addOnSuccessListener { snapshot ->
                        snapshot.data?.let { data ->
                            trySend(mapper(data))
                            //CLOSE AND THROW ON THIS LINE
                        } ?: close()
                    }
                    .addOnFailureListener {
//                    throw smth
                        close(it)
                    }
            }
            else -> {}//Why you using this with an improper reference ?
        }
        awaitClose { close() }
    }

    suspend fun getUser(): Flow<User> = getData(userDocRef) {
        User.fromFirestoreMap(it)
    }
    suspend fun getWorkouts(): Flow<Workout> = getData(workoutsCollectionRef) {
        Workout.fromFirestoreMap(it)
    }
    suspend fun getTemplateExercises(): Flow<Exercise> = getData(templateExercisesCollectionRef) {
        Exercise.fromFirestoreMap(it)
    }
    private fun deleteFirestore() {
        // Get all subcollections of the document
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
}