package bg.zahov.app.realm_db

import android.util.Log
import bg.zahov.app.utils.FirestoreExerciseAdapter
import bg.zahov.app.utils.FirestoreSettingsAdapter
import bg.zahov.app.utils.FirestoreUserAdapter
import bg.zahov.app.utils.FirestoreWorkoutAdapter
import bg.zahov.app.utils.equalTo
import bg.zahov.app.utils.equalsTo
import bg.zahov.app.utils.toFirestoreMap
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SyncManager(private val uid: String) {
    private val firestore = FirebaseFirestore.getInstance()

    private val userAdapter =  FirestoreUserAdapter()
    private val workoutAdapter =  FirestoreWorkoutAdapter()
    private val exerciseAdapter = FirestoreExerciseAdapter()
    private val settingsAdapter = FirestoreSettingsAdapter()

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

    //handle when log in and
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

        CoroutineScope(Dispatchers.Main).launch {
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

    private suspend fun getWorkoutsFromLastSync(): List<Workout> {
        val workoutsCollection = firestore.collection("users").document(uid).collection("workouts").get().await()

        return workoutsCollection.documents.mapNotNull { workoutDocument ->
            workoutAdapter.adapt(workoutDocument.data!!)
        }
    }

    private suspend fun getExercisesFromLastSync(): List<Exercise> {
        val exercisesCollection = firestore.collection("users").document(uid).collection("exercises").get().await()

        return exercisesCollection.documents.mapNotNull { exerciseDocument ->
            exerciseAdapter.adapt(exerciseDocument.data!!)
        }
    }

    suspend fun syncToFirestore(user: User?,  workouts: List<Workout>?, exercises: List<Exercise>?, settings: Settings?) {
        val userDocRef = firestore.collection("users").document(uid)
        Log.d("SYNC", "In syncToFirestore")
        user?.let{
            Log.d("SYNC", "syncing user...")
            userDocRef.set(it.toFirestoreMap())
        } ?: Log.d("SYNC", "failed to sync user")

        settings?.let{
            Log.d("SYNC", "syncing settings...")
            userDocRef.collection("settings").document("userSettings").set(settings.toFirestoreMap())
        } ?: Log.d("SYNC", "failed to sync settings")

        workouts?.let {
            it.forEach { workout ->
                Log.d("SYNC", "syncing workout...")
                if(workout.isTemplate!!){
                    if(workoutCache!!.contains(workout)){
                        Log.d("SYNC", "replacing templates workout...")
                        userDocRef.collection("workouts").document(workout.workoutName!!).set(workout.toFirestoreMap())
                    }else{
                        Log.d("SYNC", "adding new templates")
                        userDocRef.collection("workouts").document(workout.workoutName!!).set(workout.toFirestoreMap())
                    }
                }else{
                    Log.d("SYNC", "adding new workouts...")
                    userDocRef.collection("workouts").add(workout.toFirestoreMap())
                }
            }
        } ?: Log.d("SYNC", "failed to sync workouts")

        exercises?.let {
            it.forEach { exercise ->
                Log.d("SYNC", "syncing exercises...")
                if(exercise.isTemplate!!) {
                    if (exerciseCache!!.contains(exercise)) {
                        Log.d("SYNC", "replacing template exercises...")
                        userDocRef.collection("exercises").document(exercise.exerciseName!!).set(exercise.toFirestoreMap())
                    } else {
                        Log.d("SYNC", "adding new template exercises...")
                        userDocRef.collection("exercises").add(exercise.exerciseName!!)
                    }
                }
            }
        } ?: Log.d("SYNC", "failed to sync exercises")
    }

    private suspend fun initCaches(){
        userCache = getUserFromLastSync()
        Log.d("SYNC", "INIT USER -> ${userCache.username}")
        settingsCache = getSettingsFromLastSync()
        Log.d("SYNC", "INIT USER -> ${settingsCache.language}")
        workoutCache = getWorkoutsFromLastSync().toMutableList()
        Log.d("SYNC", "INIT USER -> $workoutCache")
        exerciseCache = getExercisesFromLastSync().toMutableList()
        Log.d("SYNC", "INIT USER -> $exerciseCache")
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

    private suspend fun getChangedUserOrNull(): User? {

        val newUser = realm.getUserSync()

        return if (!userCache.equalsTo(newUser)) {
            userCache = newUser
            newUser
        } else {
            null
        }
    }

    private suspend fun getChangedWorkoutsOrNull(): List<Workout>? {
        val currWorkouts = realm.getWorkoutsSync()
        var newWorkouts: MutableList<Workout>? = null

        if(currWorkouts.isEmpty() && workoutCache.isNullOrEmpty()){
            workoutCache = null
            return null
        }

        if(currWorkouts.isEmpty()) {
            workoutCache = mutableListOf()
            return workoutCache
        }

        workoutCache!!.forEach { wCache ->
            currWorkouts.forEach { currWorkouts ->
                if (!wCache.equalsTo(currWorkouts)) {
                    newWorkouts = mutableListOf()
                    newWorkouts!!.add(currWorkouts)
                }
            }
        }

        return newWorkouts
    }

    private suspend fun getChangedExercisesOrNull(): List<Exercise>? {
        val currTemplateExercises = realm.getTemplateExercisesSync()
        var newExercises: MutableList<Exercise>? = null

//        Log.d("SYNC", currTemplateExercises.first().exerciseName.toString())
        if(currTemplateExercises.isEmpty() && exerciseCache.isNullOrEmpty()){
            exerciseCache = null
            return null
        }
        if(currTemplateExercises.isEmpty()) {
            exerciseCache = mutableListOf()
            return exerciseCache
        }

        if(exerciseCache == null){
            exerciseCache = currTemplateExercises.toMutableList()
            return exerciseCache
        }
        exerciseCache!!.forEach { cExercise ->
            currTemplateExercises.forEach { currExercise ->
                if (cExercise.equalsTo(currExercise)) {
                    newExercises = mutableListOf()
                    newExercises!!.add(cExercise)
                }
            }
        }
        return newExercises
    }


    suspend fun initPeriodicSync(){
        if(currSync){
            syncToFirestore(getChangedUserOrNull(), getChangedWorkoutsOrNull(), getChangedExercisesOrNull(), getChangedSettingsOrNull())
        }
    }
}