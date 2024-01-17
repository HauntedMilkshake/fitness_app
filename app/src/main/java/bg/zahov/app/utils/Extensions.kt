package bg.zahov.app.utils

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import androidx.core.content.ContextCompat
import bg.zahov.app.data.Filter
import bg.zahov.app.data.SelectableExercise
import bg.zahov.app.backend.Exercise
import bg.zahov.app.backend.Sets
import bg.zahov.app.backend.Settings
import bg.zahov.app.backend.User
import bg.zahov.app.backend.Workout
import bg.zahov.fitness.app.R
import io.realm.kotlin.types.RealmList

// FIXME check method name conventions for methods that return a Boolean value
fun String.isAValidEmail() = Regex("^\\S+@\\S+\\.\\S+$").matches(this)

// FIXME You own these types, don't make extension functions for them.
//  For proper equals implementation either override hashCode() and equals() (you can generate them from the IDE)
//  or make the classes data classes (looks like the latter is not an option since Realm requires an empty constructor)
fun User.equalsTo(otherUser: User?): Boolean {
    return otherUser?.let{
        return this.username == it.username && this.numberOfWorkouts == it.numberOfWorkouts
    } ?: false
}

fun Settings.equalTo(newSettings: Settings?): Boolean {
    return newSettings?.let{
        this.language == it.language &&
        this.units == it.units &&
        this.soundEffects == it.soundEffects &&
        this.theme == it.theme &&
        this.restTimer == it.restTimer &&
        this.vibration == it.vibration &&
        this.soundSettings == it.soundSettings &&
        this.updateTemplate == it.updateTemplate &&
        this.fit == it.fit &&
        this.automaticSync == it.automaticSync
    } ?: false
}

fun Workout.equalsTo(newWorkout: Workout): Boolean {

    this.isTemplate?.let {
        return exerciseIds.toList() == newWorkout.exerciseIds.toList()
    }

    return this.duration == newWorkout.duration &&
            this.totalVolume == newWorkout.totalVolume &&
            this.numberOfPrs == newWorkout.numberOfPrs &&
            this.workoutName == newWorkout.workoutName &&
            this.date == newWorkout.date &&
            this.count == newWorkout.count &&
            this.isTemplate == newWorkout.isTemplate &&
            this.exercises.any { currExercises ->
                newWorkout.exercises.any { newExercises ->
                    currExercises.equalsTo(newExercises)
                }
            }
}

fun Exercise.equalsTo(exercise: Exercise?): Boolean {
    return exercise?.let {e ->
        this.isTemplate?.let {
            this.bodyPart == e.bodyPart &&
            this.category == e.category &&
            this.exerciseName == e.exerciseName
        }
        this.bodyPart == e.bodyPart &&
        this.category == e.category &&
        this.exerciseName == e.exerciseName &&
        this.sets.all { currSets ->
                    e.sets.let{
                        it.any { set->
                            !(currSets.equalsTo(set))
                        }
                    }
                }
    } ?: false
}

fun Sets.equalsTo(newSet: Sets?): Boolean {
    return newSet?.let{
         this.firstMetric == it.firstMetric &&
                this.secondMetric == it.secondMetric
    } ?: false
}

fun Set<Exercise?>.getExerciseDifference(other: Set<Exercise?>): Set<Exercise?> {
    val idToExerciseMap = this.associateBy { it?._id?.toHexString() }
    return other.filter { it?._id?.toHexString() !in idToExerciseMap }.toSet()
}

fun Set<Workout?>.getWorkoutDifference(other: Set<Workout?>): Set<Workout?> {
    val idToWorkoutMap = this.associateBy { it?._id?.toHexString() }
    return other.filter { it?._id?.toHexString() !in idToWorkoutMap }.toSet()
}

inline fun <reified T> RealmList<T>.toFirestoreMap(): List<Map<String, Any?>> {
    return map { item ->
        when (item) {
            is Exercise -> (item as Exercise).toFirestoreMap()
            is Sets -> (item as Sets).toFirestoreMap()
            else -> throw IllegalArgumentException("Unsupported type in RealmList")
        }
    }
}

fun Exercise.toSelectable() = SelectableExercise(this)

fun List<Exercise>.toSelectableList() = this.map { it.toSelectable() }
fun SelectableExercise.toExercise(): Exercise {
    return Exercise().apply {
        _id = exercise._id
        bodyPart = exercise.bodyPart
        category = exercise.category
        exerciseName = exercise.exerciseName
        isTemplate = exercise.isTemplate
        sets = exercise.sets
    }
}

fun List<SelectableExercise>.toExerciseList(): List<Exercise> = this.map { it.toExercise() }

fun User.toFirestoreMap(): Map<String, Any?> {
    return hashMapOf(
        "username" to username,
        "numberOfWorkouts" to numberOfWorkouts
    )
}

fun Exercise.toFirestoreMap(): Map<String, Any?> {
    return hashMapOf(
        "_id" to _id.toHexString(),
        "bodyPart" to bodyPart,
        "category" to category,
        "exerciseName" to exerciseName,
        "isTemplate" to isTemplate,
        "sets" to sets.toFirestoreMap()
    )
}

fun Settings.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "language" to this.language,
        "units" to this.units,
        "soundEffects" to this.soundEffects,
        "theme" to this.theme,
        "restTimer" to this.restTimer,
        "vibration" to this.vibration,
        "soundSettings" to this.soundSettings,
        "updateTemplate" to this.updateTemplate,
        "automaticSync" to this.automaticSync,
        "fit" to this.fit
    )
}

fun Workout.toFirestoreMap(): Map<String, Any?> {
    return hashMapOf(
        "_id" to _id.toHexString(),
        "duration" to duration,
        "totalVolume" to totalVolume,
        "numberOfPrs" to numberOfPrs,
        "workoutName" to workoutName,
        "date" to date,
        "exerciseIds" to exerciseIds.map { it },
        "exercises" to exercises.toFirestoreMap(),
        "count" to count,
        "isTemplate" to isTemplate
    )
}

fun Sets.toFirestoreMap(): Map<String, Any?> {
    return hashMapOf(
        "firstMetric" to firstMetric,
        "secondMetric" to secondMetric
    )
}

fun View.applyScaleAnimation() {
    val scaleAnimation = ObjectAnimator.ofPropertyValuesHolder(
        this,
        PropertyValuesHolder.ofFloat("scaleX", 1.4f),
        PropertyValuesHolder.ofFloat("scaleY", 1.4f)
    )
    scaleAnimation.duration = 140
    scaleAnimation.repeatCount = 1
    scaleAnimation.repeatMode = ObjectAnimator.REVERSE

    scaleAnimation.start()
}

fun Filter.equalsTo(filter: Filter): Boolean {
    return this.name == filter.name
}

fun View.applySelectAnimation(
    isSelected: Boolean,
    selectedColorResId: Int,
    unselectedColorResId: Int,
    duration: Long = 300,
) {
    val targetColor = ContextCompat.getColor(
        context,
        if (isSelected) selectedColorResId else unselectedColorResId
    )

    val animator = ObjectAnimator.ofInt(this, "backgroundColor", R.color.background, targetColor)
    animator.setEvaluator(ArgbEvaluator())
    animator.duration = duration
    animator.addUpdateListener { animation -> setBackgroundColor(animation.animatedValue as Int) }
    animator.start()
}

