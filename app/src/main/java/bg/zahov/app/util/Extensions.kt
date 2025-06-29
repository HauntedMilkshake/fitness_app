package bg.zahov.app.util

import android.annotation.SuppressLint
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.FirestoreFields
import bg.zahov.app.data.model.Measurement
import bg.zahov.app.data.model.Sets
import bg.zahov.app.data.model.User
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.state.ExerciseData
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

fun LocalDateTime.toFormattedString(): String =
    this.format(DateTimeFormatter.ofPattern("HH:mm, d MMMM", Locale.ENGLISH))

fun String.filterIntegerInput(): Int {
    if (this.startsWith('0') && this.length > 1) {
        this.dropWhile { it == '0' }
    }
    if (this.contains(",")) {
        this.dropLast(this.length - this.indexOf(","))
    }
    return this.toIntOrNull() ?: 0
}