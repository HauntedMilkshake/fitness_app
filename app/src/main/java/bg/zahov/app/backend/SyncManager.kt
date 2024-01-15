package bg.zahov.app.backend

import android.util.Log
import bg.zahov.app.utils.FirestoreExerciseAdapter
import bg.zahov.app.utils.FirestoreSettingsAdapter
import bg.zahov.app.utils.FirestoreUserAdapter
import bg.zahov.app.utils.FirestoreWorkoutAdapter
import bg.zahov.app.utils.equalTo
import bg.zahov.app.utils.equalsTo
import bg.zahov.app.utils.getExerciseDifference
import bg.zahov.app.utils.getWorkoutDifference
import bg.zahov.app.utils.toFirestoreMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.options
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


//TODO(WHEN USER LOGS IN MIGHT NOT HAVE WIFI TO CREATE FIRESTORE)
class SyncManager(private var userId: String, private var realm: RealmManager) {
    companion object {
        @Volatile
        private var instance: SyncManager? = null
        fun getInstance(userId: String, realm: RealmManager) =
            instance ?: synchronized(this) {
                instance ?: SyncManager(userId, realm).also { instance = it }
            }
    }

    private val firestore = FirebaseFirestore.getInstance()

    private val userAdapter = FirestoreUserAdapter()
    private val workoutAdapter = FirestoreWorkoutAdapter()
    private val exerciseAdapter = FirestoreExerciseAdapter()
    private val settingsAdapter = FirestoreSettingsAdapter()

    private var userCache: User? = null
    private var settingsCache: Settings? = null
    private var exerciseCache: MutableList<Exercise?> = mutableListOf()
    private var workoutCache: MutableList<Workout?> = mutableListOf()

    //TODO(Fix this not to be here)
    private var currSync: Boolean = true

//    init {
//        CoroutineScope(Dispatchers.Main).launch {
//            initCaches()
//            Log.d("SYNC", "INIT SYNC MANAGER -> ${userId}")
//        }
//    }

    suspend fun createFirestore(user: User, settings: Settings) {
        Log.d("SYNC", "Creating user with $userId")
        withContext(Dispatchers.IO) {
            val userDocRef = firestore.collection("users").document(userId)
            userDocRef.set(user.toFirestoreMap())
            userDocRef.collection("settings").document("userSettings")
                .set(settings.toFirestoreMap())
        }
    }

    suspend fun syncFromFirestore() {

        Log.d("SYNC", "IN FIRESTORE")
        Log.d("SYNC", "username before sign in ${userCache?.username}")
        realm.createRealm(userCache!!, workoutCache, exerciseCache, settingsCache!!)
    }

    //1st values of pairs represent the ones we need to upsert
    //2nd values of pairs are the ones we need to delete
    //null values represent no changes from last sync
    private suspend fun syncToFirestore(
        user: User?,
        workouts: Pair<List<Workout>?, List<String>?>?,
        exercises: Pair<List<Exercise>?, List<String>?>?,
        settings: Settings?
    ) {
        withContext(Dispatchers.IO){

            val userDocRef = firestore.collection("users").document(userId)
            Log.d("SYNC", "In syncToFirestore")

            firestore.runBatch { batch ->
                user?.let {
                    Log.d("SYNC", "syncing user...")
                    batch.set(userDocRef, it.toFirestoreMap())
                } ?: Log.d("SYNC", "failed to sync user")

                settings?.let {
                    Log.d("SYNC", "syncing settings...")
                    batch.set(
                        userDocRef.collection("settings").document("userSettings"),
                        it.toFirestoreMap()
                    )
                } ?: Log.d("SYNC", "failed to sync settings")

                workouts?.let {
                    it.first?.forEach { workout ->
                        batch.set(
                            userDocRef.collection("workouts").document(workout._id.toHexString()),
                            workout.toFirestoreMap()
                        )
                        Log.d("SYNC", "upserting new workouts...")
                    } ?: Log.d("SYNC", "failed to add workouts ")

                    it.second?.forEach { id ->
                        batch.delete(userDocRef.collection("workouts").document(id))
                        Log.d("SYNC", "deleting workouts...")
                    } ?: Log.d("SYNC", "failed to delete workouts")
                } ?: Log.d("SYNC", "failed to sync workouts")

                exercises?.let {
                    it.first?.forEach { exercise ->
                        batch.set(
                            userDocRef.collection("exercises").document(exercise._id.toHexString()),
                            exercise.toFirestoreMap()
                        )
                        Log.d("SYNC", "adding new exercises")
                    } ?: Log.d("SYNC", "failed to add new exercises")

                    it.second?.forEach { id ->
                        batch.delete(userDocRef.collection("exercises").document(id))
                        Log.d("SYNC", "deleting exercises...")
                    } ?: Log.d("SYNC", "failed to delete exercises")
                } ?: Log.d("SYNC", "failed to sync exercises")
            }
        }
    }

    fun deleteFirebaseUser(auth: FirebaseAuth) {
        Log.d("DELETE", userId)
        auth.currentUser?.delete()
//            Firebase.functions.getHttpsCallable("recursiveDelete").call(hashMapOf("path" to "users/$userId"))
//                .addOnSuccessListener {
//                    Log.d("DELETE", "YAY")
//                }
//                .addOnFailureListener {
//                    Log.d("DELETE", it.message ?: "no message")
//                }
    }

    private suspend fun getChangedUserOrNull(): User? {
        val newUser = realm.getUserSync()

        return if (userCache == null || !( userCache!!.equalsTo(newUser)) ) {
            userCache = newUser
            userCache
        } else {
            null
        }
    }

    private suspend fun getChangedSettingsOrNull(): Settings? {

        val newSettings = realm.getSettingsSync()

        return if (settingsCache == null || !(settingsCache!!.equalTo(newSettings)) ) {
            settingsCache = newSettings
            settingsCache?.automaticSync = currSync
            newSettings
        } else {
            null
        }
    }

    //working only with exercises marked as *template*
    //first - exercises to upsert
    //second - exercises to delete
    private suspend fun getChangedExercisesOrNull(): Pair<List<Exercise>?, List<String>?>? {
        val currTemplateExercises = realm.getTemplateExercisesSync()
        val changedExercises: MutableList<Exercise> = mutableListOf()

        val currSet = currTemplateExercises?.toSet() ?: emptySet()

        val cacheSet = exerciseCache.toSet()
        cacheSet.forEach {
            Log.d("Old exercises", it?.exerciseName ?: "no name")
        }

        //exercises present in current ones but not in the cached ones
        val newExercisesSet = cacheSet.getExerciseDifference(currSet)
        newExercisesSet.forEach {
            Log.d("New exercises", it?.exerciseName ?: "no name")
        }

        //exercises present in the cache but not in the new ones
        val updatedExercises: List<Exercise> =
            newExercisesSet.getExerciseDifference(cacheSet).mapNotNull { exercise ->
                currSet.find {
                    it._id == exercise?._id && !(it.equalsTo(exercise))
                }
            }

        //everything that is the cache but not in the current exercises
        val deletedExercisesIds: List<String> =
            currSet.getExerciseDifference(cacheSet).mapNotNull { it?._id?.toHexString() }

        changedExercises.apply {
            addAll(newExercisesSet.filterNotNull())
            addAll(updatedExercises)
        }
        if(currTemplateExercises != null){
            exerciseCache = currTemplateExercises.toMutableList()
        }

        return if (changedExercises.isEmpty() && deletedExercisesIds.isEmpty()) {
            null
        } else {
            return Pair(
                if (changedExercises.isEmpty()) null else changedExercises,
                if (deletedExercisesIds.isEmpty()) null else deletedExercisesIds
            )
        }
    }

    //First - workouts to upsert
    //Second - workouts to delete
    private suspend fun getChangedWorkoutsOrNull(): Pair<List<Workout>?, List<String>?>? {
        val currWorkouts = realm.getPastWorkoutsSync()
        val currWorkoutTemplates = realm.getTemplateWorkoutsSync()
        val cacheWorkouts = workoutCache.filter { it?.isTemplate == false }
        val cacheWorkoutTemplates = workoutCache.filter { it?.isTemplate == true }

        val workoutsToUpsert: MutableList<Workout> = mutableListOf()
        val workoutsToDelete: MutableList<String> = mutableListOf()

        val currWorkoutTemplatesSet = currWorkoutTemplates?.toSet() ?: emptySet()
        val cacheWorkoutTemplatesSet = cacheWorkoutTemplates.toSet()
        val currWorkoutsSet = currWorkouts?.toSet() ?: emptySet()
        val cacheWorkoutSets = cacheWorkouts.toSet()

        val newTemplatesSet = cacheWorkoutTemplatesSet.getWorkoutDifference(currWorkoutTemplatesSet)

        val updatedTemplates: List<Workout> =
            (newTemplatesSet.getWorkoutDifference(currWorkoutTemplatesSet)).mapNotNull { workout ->
                currWorkoutTemplatesSet.find {
                    it._id == workout?._id && !(it.equalsTo(workout))
                }
            }

        val newWorkoutsSet = cacheWorkoutSets.getWorkoutDifference(currWorkoutsSet)

        val deletedWorkoutsIds =
            currWorkoutsSet.getWorkoutDifference(cacheWorkoutSets).map { it?._id?.toHexString() }
        val deletedWorkoutTemplatesIds =
            currWorkoutTemplatesSet.getWorkoutDifference(cacheWorkoutTemplatesSet)
                .map { it?._id?.toHexString() }


        workoutsToUpsert.apply {
            addAll(newWorkoutsSet.filterNotNull())
            addAll(newTemplatesSet.filterNotNull())
            addAll(updatedTemplates)
        }

        workoutsToDelete.apply {
            addAll(deletedWorkoutsIds.filterNotNull())
            addAll(deletedWorkoutTemplatesIds.filterNotNull())
        }

        if (workoutCache.isEmpty()) {
            if(currWorkouts != null) workoutCache = currWorkouts.toMutableList()
            currWorkoutTemplates?.let { workoutCache.addAll(it) }
        } else {
            workoutCache.let {
                currWorkouts?.let { it1 -> it.addAll(it1) }
                currWorkoutTemplates?.let { it1 -> it.addAll(it1) }
            }
        }

        return if (workoutsToUpsert.isEmpty() && workoutsToDelete.isEmpty()) {
            null
        } else {
            Pair(
                if (workoutsToUpsert.isEmpty()) null else workoutsToUpsert,
                if (workoutsToDelete.isEmpty()) null else workoutsToDelete
            )
        }

    }

    private suspend fun getUserFromLastSync(): User {
        val userDocument = firestore.collection("users").document(userId).get().await()
        return userAdapter.adapt(userDocument.data)
    }

    private suspend fun getSettingsFromLastSync(): Settings {
        val settingsDocument = firestore.collection("users").document(userId).collection("settings")
            .document("userSettings").get().await()
        return settingsAdapter.adapt(settingsDocument.data)
    }

    private suspend fun getExercisesFromLastSync(): List<Exercise?> {
        val exercisesCollection =
            firestore.collection("users").document(userId).collection("exercises").get().await()
        return exercisesCollection.documents.mapNotNull { exerciseDocument ->
            exerciseAdapter.adapt(
                exerciseDocument.data
            )
        }
    }

    private suspend fun getWorkoutsFromLastSync(): List<Workout?> {
        val workoutsCollection =
            firestore.collection("users").document(userId).collection("workouts").get().await()
        return workoutsCollection.documents.mapNotNull { workoutDocument ->
            workoutAdapter.adapt(
                workoutDocument.data
            )
        }
    }

    //this function retrieves the information from the last sync whenever the app is restarted
    //TODO(if the user doesn't have internet we might not update call this function)
    suspend fun initCaches() {
        userCache = getUserFromLastSync()
        Log.d("SYNC", userCache?.username ?: "no user")
        settingsCache = getSettingsFromLastSync()
        workoutCache = getWorkoutsFromLastSync().toMutableList()
        exerciseCache = getExercisesFromLastSync().toMutableList()
    }

    //right now after automaticSync is set to false we will have 1 extra sync cycle executed
    suspend fun initPeriodicSync() {
        if (currSync) {
            syncToFirestore(
                getChangedUserOrNull(),
                getChangedWorkoutsOrNull(),
                getChangedExercisesOrNull(),
                getChangedSettingsOrNull()
            )
        }
    }

    fun updateUser(newId: String) {
        Log.d("SYNC", "UPDATE USER SYNC MANAGER -> $newId")
        userId = newId
    }
}