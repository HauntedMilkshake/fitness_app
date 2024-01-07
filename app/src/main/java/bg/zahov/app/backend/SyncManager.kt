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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SyncManager(private val uid: String, private val realm: RealmManager) {
    companion object {
        @Volatile
        private var instance: SyncManager? = null
        fun getInstance(userId: String, realm: RealmManager) =
            instance ?: synchronized(this) {
                instance ?: SyncManager(userId, realm).also { instance = it }
            }
    }
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val userAdapter =  FirestoreUserAdapter()
    private val workoutAdapter =  FirestoreWorkoutAdapter()
    private val exerciseAdapter = FirestoreExerciseAdapter()
    private val settingsAdapter = FirestoreSettingsAdapter()

    private var userCache = User()
    private var settingsCache = Settings()
    private var exerciseCache: MutableList<Exercise?> = mutableListOf()
    private var workoutCache: MutableList<Workout?> = mutableListOf()

    private var currSync: Boolean = true

    init {
        CoroutineScope(Dispatchers.Default).launch {
            initCaches()
        }
    }

    suspend fun createFirestore(user: User, settings: Settings){
        withContext(Dispatchers.IO){
            val userDocRef = firestore.collection("users").document(uid)
            userDocRef.set(user.toFirestoreMap())
            userDocRef.collection("settings").document("userSettings").set(settings.toFirestoreMap())
        }
    }

    suspend fun syncFromFirestore() {

        val userDocument = firestore.collection("users").document(uid).get().await()

        val rUser = userAdapter.adapt(userDocument.data!!)

        val settingsDocument = userDocument.reference.collection("settings").document("userSettings").get().await()
        val settings = settingsAdapter.adapt(settingsDocument.data!!)

        val workoutsCollection = userDocument.reference.collection("workouts").get().await()
        val workouts = workoutsCollection.documents.mapNotNull { workoutDocument ->
            val workout = workoutAdapter.adapt(workoutDocument.data!!)
            workout
        }

        val exercisesCollection = userDocument.reference.collection("exercises").get().await()
        val exercises = exercisesCollection.documents.mapNotNull { exerciseDocument ->
            val exercise = exerciseAdapter.adapt(exerciseDocument.data!!)
            exercise
        }

        runBlocking {
            realm.createRealm(rUser, workouts, exercises, settings)
        }
    }

    //1st values of pairs represent the ones we need to upsert
    //2nd values of pairs are the ones we need to delete
    //null values represent no changes from last sync
    private fun syncToFirestore(
        user: User?,
        workouts: Pair<List<Workout>?, List<String>?>?,
        exercises: Pair<List<Exercise>?, List<String>?>?,
        settings: Settings?
    ) {
        val userDocRef = firestore.collection("users").document(uid)
        Log.d("SYNC", "In syncToFirestore")

        firestore.runBatch { batch ->
            user?.let {
                Log.d("SYNC", "syncing user...")
                batch.set(userDocRef, it.toFirestoreMap())
            } ?: Log.d("SYNC", "failed to sync user")

            settings?.let {
                Log.d("SYNC", "syncing settings...")
                batch.set(userDocRef.collection("settings").document("userSettings"), it.toFirestoreMap())
            } ?: Log.d("SYNC", "failed to sync settings")

            workouts?.let {
                it.first?.forEach { workout ->
                    batch.set(userDocRef.collection("workouts").document(workout._id.toHexString()), workout.toFirestoreMap())
                    Log.d("SYNC", "upserting new workouts...")
                } ?: Log.d("SYNC", "failed to add workouts ")

                it.second?.forEach { id ->
                    batch.delete(userDocRef.collection("workouts").document(id))
                    Log.d("SYNC", "deleting workouts...")
                } ?: Log.d("SYNC", "failed to delete workouts")
            } ?: Log.d("SYNC", "failed to sync workouts")

            exercises?.let {
                it.first?.forEach { exercise ->
                    batch.set(userDocRef.collection("exercises").document(exercise._id.toHexString()), exercise.toFirestoreMap())
                    Log.d("SYNC", "adding new exercises")
                } ?: Log.d("SYNC", "failed to add new exercises")

                it.second?.forEach { id ->
                    batch.delete(userDocRef.collection("exercises").document(id))
                    Log.d("SYNC", "deleting exercises...")
                } ?: Log.d("SYNC", "failed to delete exercises")
            } ?: Log.d("SYNC", "failed to sync exercises")
        }
    }
    fun deleteFirebaseUser(){
        auth.currentUser?.delete()
        firestore.runBatch{
            it.delete(firestore.collection("users").document(uid))
        }
    }

    private suspend fun getChangedUserOrNull(): User? {

        val newUser = realm.getUserSync()

        return if (!userCache.equalsTo(newUser)) {
            userCache = newUser
            userCache
        } else {
            null
        }
    }

    private suspend fun getChangedSettingsOrNull(): Settings? {

        val newSettings = realm.getSettingsSync()

        return if (!settingsCache.equalTo(newSettings)) {
            settingsCache = newSettings
            settingsCache.automaticSync = currSync
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

        val currSet = currTemplateExercises.toSet()

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
        val updatedExercises: List<Exercise> = newExercisesSet.getExerciseDifference(cacheSet).mapNotNull { exercise ->
            currSet.find{
                it._id == exercise?._id && !(it.equalsTo(exercise))
            }
        }

        //everything that is the cache but not in the current exercises
        val deletedExercisesIds: List<String> = currSet.getExerciseDifference(cacheSet).mapNotNull { it?._id?.toHexString() }

        changedExercises.apply {
            addAll(newExercisesSet.filterNotNull())
            addAll(updatedExercises)
        }
        exerciseCache = currTemplateExercises.toMutableList()

        return if (changedExercises.isEmpty() && deletedExercisesIds.isEmpty()) {
            null
        }else{
            return Pair( if (changedExercises.isEmpty()) null else changedExercises, if (deletedExercisesIds.isEmpty()) null else deletedExercisesIds)
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

        val currWorkoutTemplatesSet = currWorkoutTemplates.toSet()
        val cacheWorkoutTemplatesSet = cacheWorkoutTemplates.toSet()
        val currWorkoutsSet = currWorkouts.toSet()
        val cacheWorkoutSets = cacheWorkouts.toSet()

        val newTemplatesSet = cacheWorkoutTemplatesSet.getWorkoutDifference(currWorkoutTemplatesSet)

        val updatedTemplates: List<Workout> = (newTemplatesSet.getWorkoutDifference(currWorkoutTemplatesSet)).mapNotNull { workout ->
            currWorkoutTemplatesSet.find {
                it._id == workout?._id && !(it.equalsTo(workout))
            }
        }

        val newWorkoutsSet = cacheWorkoutSets.getWorkoutDifference(currWorkoutsSet)

        val deletedWorkoutsIds = currWorkoutsSet.getWorkoutDifference(cacheWorkoutSets).map { it?._id?.toHexString() }
        val deletedWorkoutTemplatesIds = currWorkoutTemplatesSet.getWorkoutDifference(cacheWorkoutTemplatesSet).map { it?._id?.toHexString() }


        workoutsToUpsert.apply{
            addAll(newWorkoutsSet.filterNotNull())
            addAll(newTemplatesSet.filterNotNull())
            addAll(updatedTemplates)
        }

        workoutsToDelete.apply {
            addAll(deletedWorkoutsIds.filterNotNull())
            addAll(deletedWorkoutTemplatesIds.filterNotNull())
        }

        if(workoutCache.isEmpty()){
            workoutCache = currWorkouts.toMutableList()
            workoutCache.addAll(currWorkoutTemplates)
        }else{
            workoutCache.let{
                it.addAll(currWorkouts)
                it.addAll(currWorkoutTemplates)
            }
        }

        return if(workoutsToUpsert.isEmpty() && workoutsToDelete.isEmpty()){
            null
        }else{
            Pair(if (workoutsToUpsert.isEmpty()) null else workoutsToUpsert, if (workoutsToDelete.isEmpty()) null else workoutsToDelete)
        }

    }

    private suspend fun getUserFromLastSync(): User {
        val userDocument = firestore.collection("users").document(uid).get().await()
        return userAdapter.adapt(userDocument.data)
    }

    private suspend fun getSettingsFromLastSync(): Settings {
        val settingsDocument = firestore.collection("users").document(uid).collection("settings").document("userSettings").get().await()
        return settingsAdapter.adapt(settingsDocument.data)
    }

    private suspend fun getExercisesFromLastSync(): List<Exercise?> {
        val exercisesCollection = firestore.collection("users").document(uid).collection("exercises").get().await()
        return exercisesCollection.documents.mapNotNull { exerciseDocument -> exerciseAdapter.adapt(exerciseDocument.data) }
    }

    private suspend fun getWorkoutsFromLastSync(): List<Workout?> {
        val workoutsCollection = firestore.collection("users").document(uid).collection("workouts").get().await()
        return workoutsCollection.documents.mapNotNull { workoutDocument -> workoutAdapter.adapt(workoutDocument.data) }
    }

    //this function retrieves the information from the last sync whenever the app is restarted
    //TODO(if the user doesn't have internet we might not update call this function)
    private suspend fun initCaches(){
        userCache = getUserFromLastSync()
        settingsCache = getSettingsFromLastSync()
        workoutCache = getWorkoutsFromLastSync().toMutableList()
        exerciseCache = getExercisesFromLastSync().toMutableList()
    }

    //right now after automaticSync is set to false we will have 1 extra sync cycle executed
    suspend fun initPeriodicSync(){
        if(currSync){
            syncToFirestore(getChangedUserOrNull(), getChangedWorkoutsOrNull(), getChangedExercisesOrNull(), getChangedSettingsOrNull())
        }
    }

}