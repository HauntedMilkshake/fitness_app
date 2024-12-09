package bg.zahov.app.util

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.view.View
import bg.zahov.app.data.local.RealmDefaultSetting.DEFAULT_SETTING
import bg.zahov.app.data.local.RealmExercise
import bg.zahov.app.data.local.RealmSets
import bg.zahov.app.data.local.RealmTimePattern
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.FirestoreFields
import bg.zahov.app.data.model.Measurement
import bg.zahov.app.data.model.SetType
import bg.zahov.app.data.model.Sets
import bg.zahov.app.data.model.User
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.state.ExerciseData
import bg.zahov.app.ui.exercise.ExerciseAdapterWrapper
import bg.zahov.fitness.app.R
import com.google.firebase.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.UUID

fun User.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        FirestoreFields.USER_NAME to name
    )
}

fun Workout.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        FirestoreFields.WORKOUT_ID to id,
        FirestoreFields.WORKOUT_NAME to name,
        FirestoreFields.WORKOUT_DURATION to duration,
        FirestoreFields.WORKOUT_VOLUME to volume,
        FirestoreFields.WORKOUT_DATE to date.toTimestamp(),
        FirestoreFields.WORKOUT_IS_TEMPLATE to isTemplate,
        FirestoreFields.WORKOUT_EXERCISES to exercises.map { it.toFirestoreMap() },
        FirestoreFields.WORKOUT_NOTE to note,
        FirestoreFields.WORKOUT_PERSONAL_RECORD to personalRecords
    )
}

fun Exercise.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        FirestoreFields.EXERCISE_NAME to name,
        FirestoreFields.EXERCISE_BODY_PART to bodyPart.toString(),
        FirestoreFields.EXERCISE_CATEGORY to category.toString(),
        FirestoreFields.EXERCISE_IS_TEMPLATE to isTemplate,
        FirestoreFields.EXERCISE_SETS to sets.map { it.toFirestoreMap() },
        FirestoreFields.EXERCISE_BEST_SET to bestSet,
        FirestoreFields.EXERCISE_NOTE to note
    )
}

fun Sets.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        FirestoreFields.SETS_TYPE to type.key,
        FirestoreFields.SETS_FIRST_METRIC to firstMetric,
        FirestoreFields.SETS_SECOND_METRIC to secondMetric
    )
}

fun List<Measurement>.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        FirestoreFields.MEASUREMENTS_COLLECTION to this.map { it.toFirestoreMap() }
    )
}

fun Measurement.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        FirestoreFields.MEASUREMENT_VALUE to value,
        FirestoreFields.MEASUREMENT_DATE to date.toTimestamp()
    )
}

fun Timestamp.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(
        Instant.ofEpochSecond(this.seconds, this.nanoseconds.toLong()),
        ZoneId.systemDefault()
    )
}

fun LocalDateTime.toTimestamp(): Timestamp {
    return Timestamp(Date.from(this.toInstant(ZoneOffset.UTC)))
}

fun String.isEmail() = Regex("^\\S+@\\S+\\.\\S+$").matches(this)

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

fun hashString(input: String) = input
//    Hashing.sha256().hashString(input, StandardCharsets.UTF_8).toString()

fun generateRandomId(): String = UUID.randomUUID().toString().take(16)

@SuppressLint("DefaultLocale")
fun Long.timeToString(): String = String.format(
    "%02d:%02d:%02d",
    (this / (1000 * 60 * 60)) % 24,
    (this / (1000 * 60)) % 60,
    (this / 1000) % 60
)

fun String.parseTimeStringToLong(): Long {
    val parts = this.split(":")
    if (parts.size !in 1..3) {
        throw IllegalArgumentException("Invalid time string format")
    }

    val hours = parts[0].toLong()
    val minutes = if (parts.size >= 2) parts[1].toLong() else 0
    val seconds = if (parts.size == 3) parts[2].toLong() else 0

    return ((hours * 60 * 60) + (minutes * 60) + seconds) * 1000
}

fun Exercise.toExerciseData() =
    ExerciseData(
        name = this.name,
        bodyPart = this.bodyPart,
        category = this.category
    )
fun Exercise.toExerciseAdapterWrapper() =
    ExerciseAdapterWrapper(
        name = this.name,
        bodyPart = this.bodyPart.toString(),
        category = this.category.toString(),
        imageResource = R.drawable.ic_exercise,
        backgroundResource = R.color.background
    )


fun Exercise.toRealmExercise(): RealmExercise {
    val realmExercise = RealmExercise()
    realmExercise.name = this.name
    realmExercise.bodyPart = this.bodyPart.body
    realmExercise.category = this.category.key
    realmExercise.isTemplate = this.isTemplate
    realmExercise.sets.addAll(this.sets.map { it.toRealmSets() })
    realmExercise.bestSet = this.bestSet.toRealmSets()
    realmExercise.note = this.note
    return realmExercise
}

fun Sets.toRealmSets(): RealmSets {
    val realmSets = RealmSets()
    realmSets.type = this.type.key
    realmSets.firstMetric = this.firstMetric ?: 0.0
    realmSets.secondMetric = this.secondMetric ?: 0
    return realmSets
}

fun RealmExercise.toExercise(): Exercise? {
    val bodyPart = BodyPart.entries.firstOrNull { it.body == this.bodyPart }
    val category = Category.entries.firstOrNull { it.key.equals(this.category, true) }

    return if (this.name != DEFAULT_SETTING && bodyPart != null && category != null) {
        Exercise(
            name = this.name,
            bodyPart = bodyPart,
            category = category,
            isTemplate = this.isTemplate,
            sets = this.sets.mapNotNull { it.toSets() }.toMutableList(),
            note = this.note
        )
    } else {
        null
    }
}


fun RealmSets.toSets(): Sets? {
    return SetType.entries.firstOrNull { it.key == this.type }?.let { setType ->
        Sets(
            type = setType,
            firstMetric = this.firstMetric,
            secondMetric = this.secondMetric
        )
    }
}

fun LocalDateTime.toFormattedString(): String =
    this.format(DateTimeFormatter.ofPattern("HH:mm, d MMMM", Locale.ENGLISH))

fun LocalDateTime.toRealmString(): String {
    return this.format(DateTimeFormatter.ofPattern(RealmTimePattern.REALM_TIME_PATTERN))
}

fun String.toLocalDateTimeRlm(): LocalDateTime {
    return LocalDateTime.parse(this, DateTimeFormatter.ofPattern(RealmTimePattern.REALM_TIME_PATTERN))
}

fun String.filterIntegerInput(): Int {
    if (this.startsWith('0') && this.length > 1) {
        this.dropWhile { it == '0' }
    }
    if (this.contains(",")) {
        this.dropLast(this.length - this.indexOf(","))
    }
    return this.toIntOrNull() ?: 0
}