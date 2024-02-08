package bg.zahov.app.util

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import androidx.core.content.ContextCompat
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.FirestoreFields
import bg.zahov.app.data.model.SelectableExercise
import bg.zahov.app.data.model.Sets
import bg.zahov.app.data.model.User
import bg.zahov.app.data.model.Workout
import bg.zahov.fitness.app.R

fun User.toFirestoreMap(): Map<String, Any?> {
    return mapOf(FirestoreFields.USER_NAME to name)
}

fun Workout.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        FirestoreFields.WORKOUT_NAME to name,
        FirestoreFields.WORKOUT_DURATION to duration,
        FirestoreFields.WORKOUT_DATE to date,
        FirestoreFields.WORKOUT_IS_TEMPLATE to isTemplate,
        FirestoreFields.WORKOUT_EXERCISES to exercises.map { it.toFirestoreMap() },
        FirestoreFields.WORKOUT_IDS to ids.map { it }
    )
}

fun Exercise.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        FirestoreFields.EXERCISE_NAME to name,
        FirestoreFields.EXERCISE_BODY_PART to bodyPart.toString(),
        FirestoreFields.EXERCISE_CATEGORY to category.toString(),
        FirestoreFields.EXERCISE_IS_TEMPLATE to isTemplate,
        FirestoreFields.EXERCISE_SETS to sets.map { it.toFirestoreMap() }
    )
}

fun Sets.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        FirestoreFields.SETS_TYPE to type,
        FirestoreFields.SETS_FIRST_METRIC to firstMetric,
        FirestoreFields.SETS_SECOND_METRIC to secondMetric
    )
}

//fun Settings.toFirestoreMap(): Map<String, Any?> {
//    return mapOf(
//
//        FirestoreFields.SETTINGS_LANGUAGE to language,
//        FirestoreFields.SETTINGS_UNITS to units,
//        FirestoreFields.SETTINGS_SOUND_EFFECTS to soundEffects,
//        FirestoreFields.SETTINGS_THEME to theme,
//        FirestoreFields.SETTINGS_REST_TIMER to restTimer,
//        FirestoreFields.SETTINGS_VIBRATION to vibration,
//        FirestoreFields.SETTINGS_SOUND_SETTINGS to soundSettings,
//        FirestoreFields.SETTINGS_UPDATE_TEMPLATE to updateTemplate,
//        FirestoreFields.SETTINGS_FIT to fit,
//        FirestoreFields.SETTINGS_AUTOMATIC_SYNC to automaticSync
//    )
//}

fun String.isEmail() = Regex("^\\S+@\\S+\\.\\S+$").matches(this)
fun Exercise.toSelectable() = SelectableExercise(this)
fun List<Exercise>.toSelectableList() = this.map { it.toSelectable() }

fun SelectableExercise.toExercise(): Exercise {
    return Exercise(
        name = this.exercise.name,
        bodyPart = this.exercise.bodyPart,
        category = this.exercise.category,
        isTemplate = this.exercise.isTemplate,
        sets = this.exercise.sets
    )
}

fun List<SelectableExercise>.toExerciseList(): List<Exercise> = this.map { it.toExercise() }
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

