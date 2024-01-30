package bg.zahov.app.data.remote

import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.FirestoreFields
import bg.zahov.app.data.model.User
import bg.zahov.app.data.model.Workout
import bg.zahov.app.util.toFirestoreMap
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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

    private var userDocRef: DocumentReference? = null

    private var workoutsCollectionRef: CollectionReference? = null

    private var templateExercisesCollectionRef: CollectionReference? = null


    fun initUser(id: String) {
        userId = id
        userDocRef = userId?.let { firestore.collection(USERS_COLLECTION).document(it) }
        workoutsCollectionRef = userDocRef?.collection(WORKOUTS_SUB_COLLECTION)
        templateExercisesCollectionRef = userDocRef?.collection(TEMPLATE_EXERCISES)
    }

    fun resetUser() {
        userId = null
        userDocRef = null
        workoutsCollectionRef = null
        templateExercisesCollectionRef = null
    }

    suspend fun createFirestore(username: String) {
        withContext(Dispatchers.Default) {
            userDocRef?.set(User(username).toFirestoreMap())
        }
    }

    private suspend fun <T> getDocData(
        reference: DocumentReference,
        mapper: (Map<String, Any>) -> T,
    ): Flow<T> = channelFlow {
//        reference.get()
//            .addOnSuccessListener { snapshot ->
//                snapshot.data?.let { data ->
//                    trySend(mapper(data))
//                    //CLOSE AND THROW ON THIS LINE
//                } ?: close()
//            }
//            .addOnFailureListener {
////                    throw smth
//                close(it)
//            }
//        awaitClose { close() }

        val result = reference.get().await()

        result.data?.let {
            trySend(mapper(it))
        }

    }

    private suspend fun <T> getCollectionData(
        reference: CollectionReference,
        mapper: (Map<String, Any>) -> T,
    ): Flow<List<T>> = channelFlow {

        //I DON'T LIKE THIS
//        reference.get()
//            .addOnSuccessListener {
//                CoroutineScope(Dispatchers.Default).launch {
//                    val results = it.documents.map {
//                        async(Dispatchers.Default) {
//                            getDocData(it.reference, mapper).first()
//                        }
//                    }.awaitAll()
//
//                    trySend(results)
//                }
//            }

        val snapshopts = reference.get().await()

        val results = snapshopts.documents.map {
            async {
                getDocData(it.reference, mapper).first()
            }
        }.awaitAll()

        trySend(results)
    }


    suspend fun getUser(): Flow<User>? =
        userDocRef?.let { getDocData(it) { info -> User.fromFirestoreMap(info) } }

    suspend fun getWorkouts(): Flow<List<Workout>>? =
        workoutsCollectionRef?.let {
            getCollectionData(it) { info ->
                Workout.fromFirestoreMap(
                    info
                )
            }
        }

    suspend fun getTemplateWorkouts(): Flow<List<Workout>>? = getWorkouts()?.map { it.filter { it.isTemplate} }
    suspend fun getTemplateExercises(): Flow<List<Exercise>>? =
        templateExercisesCollectionRef?.let {
            getCollectionData(it) { info ->
                Exercise.fromFirestoreMap(info)
            }
        }

    suspend fun addTemplateExercise(newExercise: Exercise) {
        withContext(Dispatchers.Default) {
            templateExercisesCollectionRef?.add(newExercise)
        }
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

    suspend fun updateUsername(newUsername: String) {
        userDocRef?.update(FirestoreFields.USER_NAME, newUsername)
    }
}