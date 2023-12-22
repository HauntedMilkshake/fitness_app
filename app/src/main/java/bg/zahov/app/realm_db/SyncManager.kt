package bg.zahov.app.realm_db

import android.util.Log
import bg.zahov.app.utils.FirestoreExerciseAdapter
import bg.zahov.app.utils.FirestoreSettingsAdapter
import bg.zahov.app.utils.FirestoreUserAdapter
import bg.zahov.app.utils.FirestoreWorkoutAdapter
import bg.zahov.app.utils.difference
import bg.zahov.app.utils.equalTo
import bg.zahov.app.utils.equalsTo
import bg.zahov.app.utils.toFirestoreMap
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class SyncManager(private val uid: String) {
    companion object {
        @Volatile
        private var instance: SyncManager? = null
        fun getInstance(userId: String) =
            instance ?: synchronized(this) {
                instance ?: SyncManager(userId).also { instance = it }
            }
    }
    private val firestore = FirebaseFirestore.getInstance()

    private val userAdapter =  FirestoreUserAdapter()
    private val workoutAdapter =  FirestoreWorkoutAdapter()
    private val exerciseAdapter = FirestoreExerciseAdapter()
    private val settingsAdapter = FirestoreSettingsAdapter()

    //caches represent the information from the last sync
    private var userCache = User()
    private var settingsCache = Settings()
    private var exerciseCache: MutableList<Exercise>? = null
    private var workoutCache: MutableList<Workout>? = null

    private var currSync: Boolean = true

    private val realm: RealmManager = RealmManager.getInstance(uid)

    init {
        CoroutineScope(Dispatchers.Default).launch {
            initCaches()
        }
    }

    //1st values of pairs represent the ones we need to update
    //2nd values of pairs are the ones we need to add
    //empty values represent we need to delete (if there is something to delete)
    //null values represent no changes from last sync
    //new values are added on the principle I explained on lines 120, 121
    private fun syncToFirestore(user: User?,  workouts: Pair<Pair<List<Workout>?, List<Workout>?>?, List<Workout>?>?, exercises: Pair<List<Exercise>?, List<Exercise>?>?, settings: Settings?) {
        val userDocRef = firestore.collection("users").document(uid)
        Log.d("SYNC", "In syncToFirestore")
        user?.let{
            Log.d("SYNC", "syncing user...")
            userDocRef.set(it.toFirestoreMap())
        } ?: Log.d("SYNC", "failed to sync user")

        settings?.let{
            Log.d("SYNC", "syncing settings...")
            userDocRef.collection("settings").document("userSettings").set(it.toFirestoreMap())
        } ?: Log.d("SYNC", "failed to sync settings")


        workouts?.let { wrapper ->
            wrapper.first?.let { templates ->
                templates.first?.forEach { workout ->
                    userDocRef.collection("workouts").document(workout.workoutName!!).set(workout.toFirestoreMap())
                }
                templates.second?.forEach {workout ->
                    userDocRef.collection("workouts").add(workout.toFirestoreMap())

                }
            }
            wrapper.second?.let{ pastWorkouts ->
                pastWorkouts.forEach {
                    userDocRef.collection("workouts").add(it.toFirestoreMap())
                }
            }
        } ?: Log.d("SYNC", "failed to sync workouts")


        //check when we have the size of the first list to 0 to delete all entries
        exercises?.let {
            it.first?.forEach {exercise ->
                Log.d("SYNC", "replacing template exercises...")
                userDocRef.collection("exercises").document(exercise.exerciseName!!).set(exercise.toFirestoreMap())
            }
            it.second?.forEach {exercise ->
                Log.d("SYNC", "adding new template exercises...")
                userDocRef.collection("exercises").add(exercise.toFirestoreMap())
            }
        } ?: Log.d("SYNC", "failed to sync exercises")

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

    private suspend fun getUserFromLastSync(): User {
        val userDocument = firestore.collection("users").document(uid).get().await()
        return userAdapter.adapt(userDocument.data!!)
    }

    private suspend fun getSettingsFromLastSync(): Settings {
        val settingsDocument = firestore.collection("users").document(uid).collection("settings").document("userSettings").get().await()
        return settingsAdapter.adapt(settingsDocument.data!!)
    }

    private suspend fun getWorkoutsFromLastSync(): List<Workout>? {
        val workoutsCollection = firestore.collection("users").document(uid).collection("workouts").get().await()

        return if (workoutsCollection.isEmpty) {
            null
        } else {
            workoutsCollection.documents.mapNotNull { workoutDocument ->
                workoutAdapter.adapt(workoutDocument.data!!)
            }
        }
    }
    private suspend fun getExercisesFromLastSync(): List<Exercise>? {
        val exercisesCollection = firestore.collection("users").document(uid).collection("exercises").get().await()

        return if (exercisesCollection.isEmpty) {
            null
        } else {
            exercisesCollection.documents.mapNotNull { exerciseDocument ->
                exerciseAdapter.adapt(exerciseDocument.data!!)
            }
        }
    }


    fun createFirestore(user: User, settings: Settings){
        val userDocRef = firestore.collection("users").document(uid)

        userDocRef.set(user.toFirestoreMap())

        userDocRef.collection("settings").document("userSettings").set(settings.toFirestoreMap())

    }

    //this function retrieves the information from the last sync whenever the app is restarted
    //TODO(if the user doesn't have internet we might not update call this function)
    private suspend fun initCaches(){
        userCache = getUserFromLastSync()
        settingsCache = getSettingsFromLastSync()
        workoutCache = getWorkoutsFromLastSync()?.toMutableList()
        exerciseCache = getExercisesFromLastSync()?.toMutableList()
    }
    private suspend fun getChangedUserOrNull(): User? {

        val newUser = realm.getUserSync()

        return if (!userCache.equalsTo(newUser)) {
            userCache = newUser
            newUser
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
    private suspend fun getChangedExercisesOrNull(): Pair<List<Exercise>?, List<Exercise>?>? {
        val currTemplateExercises = realm.getTemplateExercisesSync()
        val newExercises: Pair<List<Exercise>?, List<Exercise>?>?

        if (currTemplateExercises.isEmpty() && exerciseCache.isNullOrEmpty()) {
            exerciseCache = null
            return null
        }

        if (currTemplateExercises.isEmpty()) {
            exerciseCache = mutableListOf()
            return Pair(listOf(), null)
        }

        if (exerciseCache.isNullOrEmpty()) {
            exerciseCache = currTemplateExercises.toMutableList()
            return Pair(null, exerciseCache)
        }

        val currSet = currTemplateExercises.toSet()

        val cacheSet = exerciseCache!!.toSet()

        var newExercisesSet: Set<Exercise>? = currSet.difference(cacheSet)

        var updatedExercisesSet: List<Exercise>? = currSet.difference(newExercisesSet!!).mapNotNull { exercise ->
            currSet.find{
                it._id == exercise._id && !it.equalsTo(exercise)
            }
        }

        if (newExercisesSet.isNullOrEmpty()) {
            newExercisesSet = null
        }

        if (updatedExercisesSet.isNullOrEmpty()) {
            updatedExercisesSet = null
        }

        exerciseCache = currTemplateExercises.toMutableList()
        newExercises = Pair(updatedExercisesSet, newExercisesSet?.toList())

        return newExercises
    }


    //First item of the surrounding contains a pair which has the template workouts. The first are the ones we need to update, the second are the ones we need to add
    //Second item of the surrounding pair are past workouts
    private suspend fun getChangedWorkoutsOrNull(): Pair<Pair<List<Workout>?, List<Workout>?>?, List<Workout>?>? {
        val allPastWorkouts = realm.getPastWorkoutsSync()
        val allWorkoutTemplates = realm.getTemplateWorkoutsSync()

        if(allPastWorkouts.isEmpty() && allWorkoutTemplates.isEmpty() && workoutCache.isNullOrEmpty()){
            workoutCache = null
            return null
        }

        if(allPastWorkouts.isEmpty() && allWorkoutTemplates.isEmpty()) {
            workoutCache = mutableListOf()
            return Pair(Pair(listOf(), listOf()), listOf())
        }

        if(workoutCache.isNullOrEmpty()){
            workoutCache = (allPastWorkouts + allWorkoutTemplates).toMutableList()
            return Pair(Pair(null, allWorkoutTemplates), allPastWorkouts)
        }
        val lastSyncTemplates = workoutCache!!.filter{it.isTemplate == true}.toSet()
        val newTemplatesSet =  lastSyncTemplates - allWorkoutTemplates.toSet()
        val newTemplates = (lastSyncTemplates - allWorkoutTemplates.toSet()).toList()
        val updatedTemplates = (allWorkoutTemplates.toSet() - newTemplatesSet).filter { workout -> lastSyncTemplates.any {
            it._id == workout._id && !workout.equalsTo(it)
        }}
        workoutCache = allPastWorkouts.toMutableList()
        val newWorkouts = (allPastWorkouts - workoutCache!!)
        return Pair(Pair(updatedTemplates, newTemplates), newWorkouts)
    }

    //right now after automaticSync is set to false we will have 1 extra sync cycle will be executed
    suspend fun initPeriodicSync(){
        if(currSync){
            syncToFirestore(getChangedUserOrNull(), getChangedWorkoutsOrNull(), getChangedExercisesOrNull(), getChangedSettingsOrNull())
        }
    }
}