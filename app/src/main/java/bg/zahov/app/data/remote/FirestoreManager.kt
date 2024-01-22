package bg.zahov.app.data.remote

import android.util.Log
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

class FirestoreManager {
    companion object {
        const val USERS_COLLECTION = "users"
        const val TEMPLATE_EXERCISES = "templateExercises"
        const val WORKOUTS_SUB_COLLECTION = "workouts"

        @Volatile
        private var instance: FirestoreManager? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: FirestoreManager().also { instance = it }
        }
    }

    private var userId: String? = null

    private val firestore = FirebaseFirestore.getInstance()

    private val userDocRef by lazy {
        userId?.let { firestore.collection(USERS_COLLECTION).document(it) }
    }

    private val workoutsCollectionRef by lazy {
        userDocRef?.collection(WORKOUTS_SUB_COLLECTION)
    }

    private val templateExercisesCollectionRef by lazy {
        userDocRef?.collection(TEMPLATE_EXERCISES)
    }

    fun initUser(id: String) {
        Log.d("SIGNUP INIT USER", id)
        userId = id
    }

    suspend fun createFirestore(username: String) {
        try {
            Log.d("SIGNUP CREATE", username)
            userDocRef?.set(User(username).toFirestoreMap(), SetOptions.merge())?.await()
        } catch (e: FirebaseFirestoreException) {
            throw FirebaseFirestoreException(e.message ?: "unknown", e.code)
        }
    }

    private suspend fun <T> getDocData(
        reference: DocumentReference,
        mapper: (Map<String, Any>) -> T,
    ): Flow<T> = callbackFlow {
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
        awaitClose { close() }

    }

    private suspend fun <T> getCollectionData(
        reference: CollectionReference,
        mapper: (Map<String, Any>) -> T,
    ): Flow<List<T>> = callbackFlow {
        reference.get()
            .addOnSuccessListener {

                val data: MutableList<T> = mutableListOf()

                it.documents.forEach {
                    suspend {
                        getDocData(it.reference, mapper).collect { workout ->
                            data.add(workout)
                        }
                    }
                }

                trySend(data)

            }
            .addOnFailureListener {
                close(it)
            }
        awaitClose { close() }
    }


    suspend fun getUser(): Flow<User>? =
        userDocRef?.let { getDocData(it) { info -> User.fromFirestoreMap(info) } }

    suspend fun getWorkouts(): Flow<List<Workout>>? = workoutsCollectionRef?.let { getCollectionData(it) { info -> Workout.fromFirestoreMap(info) } }
    suspend fun getTemplateExercises(): Flow<List<Exercise>>? = templateExercisesCollectionRef?.let { getCollectionData(it) { info -> Exercise.fromFirestoreMap(info) } }
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