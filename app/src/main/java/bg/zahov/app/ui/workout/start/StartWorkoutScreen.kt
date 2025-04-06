package bg.zahov.app.ui.workout.start

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.provider.toFormattedString
import bg.zahov.app.ui.theme.FitnessTheme
import bg.zahov.fitness.app.R


@Composable
fun StartWorkoutScreen(
    startWorkoutViewModel: StartWorkoutViewModel = hiltViewModel(),
    onEditWorkout: (String) -> Unit,
    onAddTemplateWorkout: () -> Unit,
) {
    val uiState by startWorkoutViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    StartWorkoutContent(
        workouts = uiState.workouts,
        onWorkoutStart = { startWorkoutViewModel.startWorkout(it) },
        onAddTemplateWorkout = onAddTemplateWorkout,
        onStartEmptyWorkout = { startWorkoutViewModel.startWorkout() },
        onEditWorkout = { onEditWorkout(it) },
        onDeleteWorkout = { startWorkoutViewModel.deleteTemplateWorkout(it) },
        onDuplicateWorkout = {
            startWorkoutViewModel.addDuplicateTemplateWorkout(
                it,
                context.getString(R.string.duplicate_workout_template)
            )
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StartWorkoutContent(
    workouts: List<StartWorkout>,
    onWorkoutStart: (StartWorkout) -> Unit,
    onAddTemplateWorkout: () -> Unit,
    onStartEmptyWorkout: () -> Unit,
    onEditWorkout: (String) -> Unit,
    onDeleteWorkout: (StartWorkout) -> Unit,
    onDuplicateWorkout: (StartWorkout) -> Unit,
) {
    FitnessTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.padding(top = 32.dp, start = 16.dp),
                text = stringResource(R.string.quick_start_text),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .testTag("StartEmptyWorkoutButton"),
                onClick = onStartEmptyWorkout
            ) {
                Text(
                    text = stringResource(R.string.start_empty_workout_text),
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp),
                    text = stringResource(R.string.my_templates_text),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                val iconButtonString = stringResource(R.string.add_template)
                IconButton(
                    modifier = Modifier.semantics { contentDescription = iconButtonString },
                    onClick = onAddTemplateWorkout
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_plus),
                        tint = Color.Unspecified,
                        contentDescription = null
                    )
                }
            }

            LazyColumn(Modifier.fillMaxSize()) {
                items(items = workouts, key = { it.id }) {

                    Workout(
                        modifier = Modifier.animateItem(),
                        workoutName = it.name,
                        workoutDate = it.date.toFormattedString(),
                        exercises = it.exercises,
                        onWorkoutStart = { onWorkoutStart(it) },
                        onEdit = { onEditWorkout(it.id) },
                        onDelete = { onDeleteWorkout(it) },
                        onDuplicate = { onDuplicateWorkout(it) })
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Workout(
    modifier: Modifier,
    workoutName: String, workoutDate: String, exercises: List<StartWorkoutExercise> = listOf(),
    onWorkoutStart: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
) {
    var showDetails by rememberSaveable {
        mutableStateOf(false)
    }

    SharedTransitionLayout(modifier = modifier) {
        AnimatedContent(
            targetState = showDetails, label = ""
        ) { targetState ->
            if (!targetState) {
                WorkoutContent(
                    animatedVisibilityScope = this@AnimatedContent,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    modifier = modifier,
                    workoutName = workoutName,
                    exercises = exercises,
                    onShowDetails = {
                        showDetails = true
                    },
                    workoutDate = workoutDate,
                    onWorkoutStart = onWorkoutStart,
                    onEdit = onEdit,
                    onDelete = onDelete,
                    onDuplicate = onDuplicate,
                )
            } else {
                WorkoutDetails(
                    onBack = { showDetails = false },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedContent,
                    workoutName = workoutName,
                    workoutDate = workoutDate,
                    exercises = exercises,
                    onWorkoutStart = onWorkoutStart,
                    onEdit = onEdit,
                    onDelete = onDelete,
                    onDuplicate = onDuplicate
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WorkoutContent(
    onShowDetails: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier,
    workoutName: String, workoutDate: String, exercises: List<StartWorkoutExercise> = listOf(),
    onWorkoutStart: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
) {
    var isDropDownExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    with(sharedTransitionScope) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clickable { onShowDetails() }
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.secondary,
                    RoundedCornerShape(4.dp)
                )
                .padding(12.dp)
                .sharedElement(
                    rememberSharedContentState(key = workoutName),
                    animatedVisibilityScope = animatedVisibilityScope
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = workoutName,
                    modifier = Modifier
                        .weight(1f),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis
                )

                DropDown(
                    isDropDownExpanded = isDropDownExpanded,
                    onExpand = { isDropDownExpanded = true },
                    onClose = { isDropDownExpanded = false },
                    onStart = onWorkoutStart,
                    onEdit = onEdit,
                    onDuplicate = onDuplicate,
                    onDelete = onDelete
                )
            }

            Text(
                text = stringResource(R.string.last_performed, workoutDate),
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )


            exercises.forEach {
                Text(
                    text = it.name,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WorkoutDetails(
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    workoutName: String, workoutDate: String, exercises: List<StartWorkoutExercise> = listOf(),
    onWorkoutStart: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isDropDownExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    with(sharedTransitionScope) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clickable {
                    onBack()
                }
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.secondary,
                    RoundedCornerShape(4.dp)
                )
                .padding(12.dp)
                .sharedElement(
                    rememberSharedContentState(key = workoutName),
                    animatedVisibilityScope = animatedVisibilityScope
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = workoutName,
                    modifier = Modifier
                        .weight(1f),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis
                )

                DropDown(
                    isDropDownExpanded = isDropDownExpanded,
                    onExpand = { isDropDownExpanded = true },
                    onClose = { isDropDownExpanded = false },
                    onStart = onWorkoutStart,
                    onEdit = onEdit,
                    onDuplicate = onDuplicate,
                    onDelete = onDelete
                )
            }

            Text(
                text = stringResource(R.string.last_performed, workoutDate),
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            exercises.forEach {
                Exercise(
                    exerciseName = it.exercise,
                    bodyPart = it.bodyPart,
                    category = it.category,
                )
            }
        }
    }
}

@Composable
fun Exercise(exerciseName: String, bodyPart: BodyPart, category: Category) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Icon(
            modifier = Modifier.size(64.dp),
            painter = painterResource(
                when (bodyPart) {
                    BodyPart.Core -> R.drawable.ic_abs
                    BodyPart.Arms -> R.drawable.ic_arms
                    BodyPart.Back -> R.drawable.ic_back
                    BodyPart.Chest -> R.drawable.ic_chest
                    BodyPart.Legs -> R.drawable.ic_legs
                    BodyPart.Shoulders -> R.drawable.ic_shoulders
                    else -> R.drawable.ic_olympic
                }
            ),
            contentDescription = stringResource(R.string.muslce_part_description),
            tint = Color.Unspecified
        )
        Column(
            modifier = Modifier.padding(start = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = exerciseName,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = when (category) {
                    Category.Barbell -> stringResource(id = R.string.barbell_category_text)
                    Category.Dumbbell -> stringResource(id = R.string.dumbbell_category_text)
                    Category.Machine -> stringResource(id = R.string.machine_category_text)
                    Category.AdditionalWeight -> stringResource(id = R.string.additional_weight_category_text)
                    Category.Cable -> stringResource(id = R.string.cable_category_text)
                    Category.None -> stringResource(id = R.string.none_category_text)
                    Category.AssistedWeight -> stringResource(id = R.string.assisted_weight_category_text)
                    Category.RepsOnly -> stringResource(id = R.string.reps_only_category_text)
                    Category.Cardio -> stringResource(id = R.string.cardio_category_text)
                    Category.Timed -> stringResource(id = R.string.timed_category_text)
                },
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun DropDown(
    isDropDownExpanded: Boolean,
    onExpand: () -> Unit,
    onClose: () -> Unit,
    onStart: () -> Unit,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .clickable {
                    onExpand()
                }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_settings_dots),
                contentDescription = "",
                tint = Color.Unspecified,
                modifier = Modifier.clickable {
                    onExpand()
                }
            )
        }
        DropdownMenu(
            expanded = isDropDownExpanded,
            onDismissRequest = onClose
        ) {
            MenuItem.entries.toList().forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(item.stringResource),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    onClick = {
                        when (item) {
                            MenuItem.EDIT -> onEdit()
                            MenuItem.DELETE -> onDelete()
                            MenuItem.DUPLICATE -> onDuplicate()
                            MenuItem.START -> onStart()
                        }
                        onClose()
                    })
            }
        }
    }
}

/**
 * Enum representing the different menu items
 *
 * @param stringResource corresponding string for the label
 */
enum class MenuItem(val stringResource: Int) {
    EDIT(R.string.edit_text), DELETE(R.string.delete), DUPLICATE(R.string.duplicate), START(R.string.start_workout)
}