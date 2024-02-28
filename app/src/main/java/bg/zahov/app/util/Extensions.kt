package bg.zahov.app.util

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import bg.zahov.app.data.model.ClickableSet
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.FirestoreFields
import bg.zahov.app.data.model.InteractableExerciseWrapper
import bg.zahov.app.data.model.Sets
import bg.zahov.app.data.model.User
import bg.zahov.app.data.model.Workout
import bg.zahov.fitness.app.R
import com.google.common.hash.Hashing
import com.google.firebase.Timestamp
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.UUID

fun User.toFirestoreMap(): Map<String, Any?> {
    return mapOf(FirestoreFields.USER_NAME to name)
}
fun Timestamp.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochSecond(this.seconds, this.nanoseconds.toLong()), ZoneOffset.UTC)
}

fun LocalDateTime.toTimestamp(): Timestamp {
    return Timestamp(Date.from(this.toInstant(ZoneOffset.UTC)))
}
fun Workout.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        FirestoreFields.WORKOUT_ID to id,
        FirestoreFields.WORKOUT_NAME to name,
        FirestoreFields.WORKOUT_DURATION to duration,
        FirestoreFields.WORKOUT_DATE to date.toTimestamp(),
        FirestoreFields.WORKOUT_IS_TEMPLATE to isTemplate,
        FirestoreFields.WORKOUT_EXERCISES to exercises.map { it.toFirestoreMap() },
        FirestoreFields.WORKOUT_NOTE to note
    )
}

fun Exercise.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        FirestoreFields.EXERCISE_NAME to name,
        FirestoreFields.EXERCISE_BODY_PART to bodyPart.toString(),
        FirestoreFields.EXERCISE_CATEGORY to category.toString(),
        FirestoreFields.EXERCISE_IS_TEMPLATE to isTemplate,
        FirestoreFields.EXERCISE_SETS to sets.map { it.toFirestoreMap() },
        FirestoreFields.EXERCISE_NOTE to note
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

//TODO(Are these functions really needed)

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

//fun View.applyScaleAnimation() {
//    val duration = 140L
//    val highlightColor = Color.WHITE
//
//    val originalColor = this.background?.let { it as? ColorDrawable }?.color ?: Color.TRANSPARENT
//
//    val colorAnimator = ObjectAnimator.ofObject(
//        this,
//        "backgroundColor",
//        ArgbEvaluator(),
//        originalColor,
//        highlightColor
//    ).apply {
//        this.duration = duration
//    }
//
//    val revertColorAnimator = ObjectAnimator.ofObject(
//        this,
//        "backgroundColor",
//        ArgbEvaluator(),
//        highlightColor,
//        originalColor
//    ).apply {
//        this.duration = duration
//    }
//
//    val animatorSet = AnimatorSet().apply {
//        playSequentially(colorAnimator, revertColorAnimator)
//    }
//
//    this.setOnClickListener {
//        animatorSet.start()
//    }
//}

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

fun hashString(input: String) =
    Hashing.sha256().hashString(input, StandardCharsets.UTF_8).toString()

fun generateRandomId(): String = UUID.randomUUID().toString().take(16)

fun currDateToString(): String = LocalDate.now().format(
    DateTimeFormatter.ofPattern(
        "dd-MM-yyyy",
        Locale.getDefault()
    )
)

fun Long.timeToString(): String = String.format(
    "%02d:%02d:%02d",
    (this / (1000 * 60 * 60)) % 24,
    (this / (1000 * 60)) % 60,
    (this / 1000) % 60
)

fun String.parseTimeStringToLong(): Long {
    val parts = this.split(":")
    if (parts.size !in 1..3) {
        Log.d("SIZE OF PARSE", parts.size.toString())
        throw IllegalArgumentException("Invalid time string format")
    }

    val hours = parts[0].toLong()
    val minutes = if (parts.size >= 2) parts[1].toLong() else 0
    val seconds = if (parts.size == 3) parts[2].toLong() else 0

    return ((hours * 60 * 60) + (minutes * 60) + seconds) * 1000
}

fun InteractableExerciseWrapper.toExercise() = Exercise(
    name = this.name,
    bodyPart = this.bodyPart,
    category = this.category,
    isTemplate = this.isTemplate,
    sets = this.sets.map { it.set },
    note = this.note
)

fun Exercise.toInteractableExerciseWrapper() = InteractableExerciseWrapper(
    name = this.name,
    bodyPart = this.bodyPart,
    category = this.category,
    isTemplate = this.isTemplate,
    sets = this.sets.map { ClickableSet(it, false) },
    note = this.note
)

fun Date.toTimeStamp() = Timestamp(this)

