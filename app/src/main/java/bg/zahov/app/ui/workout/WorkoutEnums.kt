package bg.zahov.app.ui.workout

import bg.zahov.app.data.model.Sets
import bg.zahov.fitness.app.R

/**
 * Enum class representing the available set menu items for a workout.
 * Each enum constant corresponds to a specific set type.
 *
 * @property stringResource The string resource ID associated with the set type name.
 */
enum class SetMenuItem(val stringResource: Int) {
    WARMUP(R.string.warmup_set), DROP_SET(R.string.drop_set), FAILURE(R.string.failure_set)
}


/**
 * Enum class representing the available menu items for an exercise.
 * Each enum constant corresponds to a specific action that can be performed on an exercise.
 *
 * @property stringResource The string resource ID associated with the exercise menu item.
 */
enum class ExerciseMenuItem(val stringResource: Int) {
    ADD_NOTE(R.string.add_note), REPLACE(R.string.replace_exercise), REMOVE(R.string.remove_exercise)
}

/**
 * Enum class representing the different item types that can exist in a workout.
 * This is used to distinguish between [SetMenuItem] and [ExerciseMenuItem] in [DropDown].
 */
enum class ItemType {
    EXERCISE, SET
}

/**
 * Enum class representing the first and second input fields respectively.
 * This is used to distinguish whether to write the value in [Sets.firstMetric] or [Sets.secondMetric].
 */
enum class SetField {
    WEIGHT, REPETITIONS
}