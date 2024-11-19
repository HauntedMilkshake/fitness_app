package bg.zahov.app.ui.workout

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bg.zahov.app.ui.theme.FitnessTheme
import bg.zahov.fitness.app.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.SetType
import bg.zahov.app.data.model.Sets
import bg.zahov.app.util.generateRandomId
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime


/**
 * A sealed class representing a workout entry, which can be either an exercise or a set
 *
 * A list of workoutEntry would look something like:
 * List<WorkoutEntry> { Exercise1, Set1ForExercise1, Set2ForExercise1, Exercise2, ... etc }
 * where the arrangement of items is important
 *
 * ! Note that this class is defined here only because this file and the view model would
 * be to large to place in a single commit, however to properly showcase this file we
 * need it to be here. In the pull request for the view model this class will be
 * moved to the appropriate place !
 */
sealed class WorkoutEntry {

    /**
     * A data class representing an exercise in the workout.
     *
     * @property id A unique identifier for the exercise entry, default is generated using [generateRandomId].
     * @property name The name of the exercise.
     * @property firstInputColumnVisibility Determines whether the first input column for the exercise is visible. Default is true.
     * As of now it is not used however for future purposes I decided to leave it here
     * @property note An optional note related to the exercise. Default is an empty string.
     * @property bodyPart The body part targeted by the exercise.
     * @property category The category of the exercise (e.g., strength, endurance, etc.).
     */
    data class ExerciseEntry(
        val id: String = generateRandomId(),
        val name: String,
        val firstInputColumnVisibility: Boolean = true,
        val note: String = "",
        val bodyPart: BodyPart,
        val category: Category
    ) : WorkoutEntry()

    /**
     * A data class representing a set in the exercise.
     *
     * @property id A unique identifier for the set entry, default is generated using [generateRandomId].
     * @property setType The type of set (e.g., default, warm-up, etc.). Default is [SetType.DEFAULT].
     * @property firstInputFieldVisibility Determines whether the first input field for the set is visible. Default is true.
     * @property setNumber The number of the set (e.g., 1, 2, 3...).
     * @property previousResults The results from the previous set, default is "-//-" indicating no prior results.
     * @property set The specific set details (e.g., weight, repetitions, etc.).
     * @property setCompleted Indicates whether the set has been completed. Default is false.
     */
    data class SetEntry(
        val id: String = generateRandomId(),
        val setType: SetType = SetType.DEFAULT,
        val firstInputFieldVisibility: Boolean = true,
        val setNumber: String,
        val previousResults: String = "-//-",
        val set: Sets,
        val setCompleted: Boolean = false
    ) : WorkoutEntry()
}

@Composable
fun WorkoutScreen(
    workoutViewModel: WorkoutViewModel = viewModel(),
    onAddExercise: () -> Unit,
    onReplaceExercise: () -> Unit,
    onBackPressed: () -> Unit
) {

    BackHandler {
        onBackPressed()
    }

    val workoutPrefix = when (LocalDateTime.now().hour) {
        in 6..11 -> stringResource(R.string.morning_workout)
        in 12..16 -> stringResource(R.string.noon_workout)
        in 17..20 -> stringResource(R.string.afternoon_workout)
        else -> stringResource(R.string.night_workout)
    }

    WorkoutScreenContent(
        //placeholder Example for state.name
        name = if("Example".isEmpty()) workoutPrefix + "Example" else "Example",
        note = "",
        exercises = listOf<WorkoutEntry>(),
        onAddExercise = onAddExercise,
        onDeleteSet = {},
        onCancel = {},
        onNoteChange = {},
        onExerciseNoteChange = { pos, note -> },
        onRemoveExercise = {},
        onReplaceExercise = {
            onReplaceExercise() },
        onAddSet = { },
        onInputFieldChanged = { pos, value, type -> },
        onSetTypeChange = { pos, type -> }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WorkoutScreenContent(
    name: String,
    note: String,
    exercises: List<WorkoutEntry>,
    onAddExercise: () -> Unit,
    onCancel: () -> Unit,
    onNoteChange: (String) -> Unit,
    onExerciseNoteChange: (Int, String) -> Unit,
    onRemoveExercise: (Int) -> Unit,
    onReplaceExercise: (Int) -> Unit,
    onDeleteSet: (Int) -> Unit,
    onAddSet: (Int) -> Unit,
    onInputFieldChanged: (Int, String, SetField) -> Unit,
    onSetTypeChange: (Int, SetType) -> Unit,
) {
    /**
     * In order to center the items of 2 independent rows we need to have
     * pre-defined weight values to ensure consistency
     */
    val weightValues by rememberSaveable {
        mutableStateOf(arrayOf(1f, 1f, 2f, 2f))
    }
    FitnessTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            Text(
                text = name,
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            WorkoutScreenInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                value = note,
                onValueChanged = { onNoteChange(it) },
                label = {
                    Text(
                        text = stringResource(R.string.add_note),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            )
            /**
             * This structure ensures that the buttons placed within the Column that surrounds
             * the LazyColumn are always visible
             */
            Column(modifier = Modifier.fillMaxWidth()) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    itemsIndexed(
                        items = exercises,
                        key = { index, item -> if (item is WorkoutEntry.ExerciseEntry) item.id else (item as WorkoutEntry.SetEntry).id }) { index, it ->

                        when (it) {
                            is WorkoutEntry.ExerciseEntry -> {
                                Exercise(
                                    modifier = Modifier.animateItem(fadeOutSpec = spring(stiffness = Spring.StiffnessLow)),
                                    it.name,
                                    note = it.note,
                                    floatArrangement = weightValues,
                                    labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    onExerciseNoteChange = { onExerciseNoteChange(index, it) },
                                    onAddSet = { onAddSet(index) },
                                    onReplaceExercise = { onReplaceExercise(index) },
                                    onRemoveExercise = { onRemoveExercise(index) }
                                )
                            }
                            /**
                             * Replicating a debounce effect because otherwise the
                             * after the threshold of the swipe has been reached
                             * the set just clips out and it made it look very choppy
                             */
                            is WorkoutEntry.SetEntry -> {
                                val scope = rememberCoroutineScope()
                                var isRemoved by remember {
                                    mutableStateOf(false)
                                }

                                val dismissState = rememberSwipeToDismissBoxState(
                                    confirmValueChange = { value ->
                                        if (value == SwipeToDismissBoxValue.EndToStart) {
                                            isRemoved = true
                                        }
                                        true
                                    }
                                )

                                LaunchedEffect(isRemoved) {
                                    scope.launch {
                                        delay(150)
                                        if (isRemoved) {
                                            onDeleteSet(index)
                                        }
                                    }
                                }

                                SwipeToDismissBox(
                                    state = dismissState,
                                    backgroundContent = { /* noop */ },
                                    enableDismissFromStartToEnd = false
                                ) {
                                    WorkoutSet(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .animateItem(),
                                        setIndicator = if (it.setType == SetType.DEFAULT) it.setNumber else it.setType.key,
                                        previous = it.previousResults,
                                        weight = if (it.set.firstMetric == null || it.set.firstMetric == 0.0) "" else it.set.firstMetric.toString(),
                                        reps = if (it.set.secondMetric == null || it.set.secondMetric == 0) "" else it.set.secondMetric.toString(),
                                        floatArrangement = weightValues,
                                        onInputFieldChanged = { value, type ->
                                            onInputFieldChanged(
                                                index,
                                                value,
                                                type
                                            )
                                        },
                                        onChangeSetType = { onSetTypeChange(index, it) }
                                    )
                                }
                            }
                        }
                    }
                }
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    Button(
                        onClick = onAddExercise,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp, top = 16.dp),
                        shape = RoundedCornerShape(4.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.add_exercise),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp, top = 4.dp),
                        onClick = onCancel,
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onError,
                            disabledContentColor = Color.Unspecified,
                            disabledContainerColor = Color.Unspecified
                        ),
                        shape = RoundedCornerShape(4.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

/**
 * generic text field used for the note of the workout and all
 * of the notes of the exercies
 */
@Composable
fun WorkoutScreenInputField(
    modifier: Modifier = Modifier,
    value: String,
    label: @Composable (() -> Unit)? = null,
    onValueChanged: (String) -> Unit
) {
    TextField(
        modifier = modifier,
        value = value,
        onValueChange = { onValueChanged(it) },
        label = label,
        shape = RoundedCornerShape(12.dp),
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.tertiary,
        )
    )
}

/**
 * This structure ensures that each independent item in the row would have
 * an equal weight distributed despite the contents of the box
 */
@Composable
fun ItemBox(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .height(IntrinsicSize.Max)
            .padding(vertical = 2.dp), contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun Exercise(
    modifier: Modifier = Modifier,
    name: String,
    note: String,
    labelColor: Color,
    floatArrangement: Array<Float>,
    onAddSet: () -> Unit,
    onExerciseNoteChange: (String) -> Unit,
    onReplaceExercise: () -> Unit,
    onRemoveExercise: () -> Unit
) {
    var isDropDownExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    var isNoteVisible by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        if (isNoteVisible) {
            WorkoutScreenInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                value = note,
                onValueChanged = {
                    onExerciseNoteChange(it)
                })
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            DropDown(
                isDropDownExpanded = isDropDownExpanded,
                itemType = ItemType.EXERCISE,
                onExpand = { isDropDownExpanded = true },
                onClose = { isDropDownExpanded = false },
                onFirstOption = { isNoteVisible = !isNoteVisible },
                onSecondOption = onReplaceExercise,
                onThirdOption = onRemoveExercise
            )
        }

        Button(
            onClick = onAddSet,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.add_set),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.labelLarge
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 12.dp),
        ) {
            ItemBox(modifier = Modifier.weight(floatArrangement[0])) {
                SetColumnText(
                    columnText = stringResource(R.string.set_column_text),
                    color = labelColor
                )
            }

            ItemBox(modifier = Modifier.weight(floatArrangement[1])) {
                SetColumnText(
                    columnText = stringResource(R.string.previous_column_text),
                    color = labelColor
                )
            }
            ItemBox(modifier = Modifier.weight(floatArrangement[2])) {

                SetColumnText(
                    columnText = stringResource(R.string.weight_text),
                    color = labelColor
                )
            }

            ItemBox(modifier = Modifier.weight(floatArrangement[3])) {
                SetColumnText(
                    columnText = stringResource(R.string.reps_column_text),
                    color = labelColor
                )
            }
        }
    }
}

/**
 * A custom text field
 *
 * allows for customization of the inner padding while still somewhat
 * behaving like the regular text field
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetInputField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit
) {
    val customTextSelectionColors =
        TextSelectionColors(handleColor = Color.Transparent, backgroundColor = Color.Transparent)
    val interactionSource = remember { MutableInteractionSource() }
    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        BasicTextField(
            modifier = modifier.fillMaxHeight(),
            value = value,
            singleLine = true,
            enabled = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onTertiaryContainer),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = {
                onValueChanged(it)
            },
            interactionSource = interactionSource,
        ) { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = TextFieldValue(text = value, selection = TextRange(value.length)).text,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                interactionSource = interactionSource,
                visualTransformation = VisualTransformation.None,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(start = 4.dp)
            )
        }
    }
}
/**
 * a common text element used by the row that needs to have
 * its items centered with the different items in the set
 */
@Composable
fun SetColumnText(
    modifier: Modifier = Modifier,
    columnText: String,
    color: Color = MaterialTheme.colorScheme.onSecondaryContainer
) {
    Text(
        modifier = modifier.padding(bottom = 8.dp),
        text = columnText,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun WorkoutSet(
    modifier: Modifier = Modifier,
    setIndicator: String,
    previous: String,
    weight: String,
    reps: String,
    floatArrangement: Array<Float>,
    onInputFieldChanged: (String, SetField) -> Unit,
    onChangeSetType: (SetType) -> Unit
) {
    var isDropDownExpanded by remember {
        mutableStateOf(false)
    }
    /**
     * This and the last row of [Exercise] are centered
     * using [ItemBox] and [floatArrangement]
     */
    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        ItemBox(modifier = Modifier.weight(floatArrangement[0])) {
            DropDown(
                option = setIndicator,
                isDropDownExpanded = isDropDownExpanded,
                itemType = ItemType.SET,
                onExpand = { isDropDownExpanded = true },
                onClose = { isDropDownExpanded = false },
                onFirstOption = { onChangeSetType(SetType.WARMUP) },
                onSecondOption = { onChangeSetType(SetType.DROP_SET) },
                onThirdOption = { onChangeSetType(SetType.FAILURE) },
            )
        }

        ItemBox(modifier = Modifier.weight(floatArrangement[1])) {
            Text(
                text = previous,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        ItemBox(modifier = Modifier.weight(floatArrangement[2])) {
            SetInputField(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(width = 64.dp),
                value = weight,
                onValueChanged = { onInputFieldChanged(it, SetField.WEIGHT) }
            )
        }

        ItemBox(modifier = Modifier.weight(floatArrangement[3])) {
            SetInputField(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(width = 64.dp),
                value = reps,
                onValueChanged = { onInputFieldChanged(it, SetField.REPETITIONS) }
            )
        }
    }
}


@Composable
fun DropDown(
    modifier: Modifier = Modifier,
    option: String? = null,
    isDropDownExpanded: Boolean,
    itemType: ItemType,
    onExpand: () -> Unit,
    onClose: () -> Unit,
    onFirstOption: () -> Unit,
    onSecondOption: () -> Unit,
    onThirdOption: () -> Unit
) {
    Column(
        modifier = modifier.background(color = Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(color = Color.Transparent)
                .clickable {
                    onExpand()
                }
        ) {
            if (option != null) {
                Text(
                    modifier = Modifier.clickable {
                        onExpand()
                    },
                    text = option,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge
                )
            } else {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.ic_settings_dots_blue),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    contentDescription = ""
                )
            }
        }
        DropdownMenu(
            expanded = isDropDownExpanded,
            onDismissRequest = onClose
        ) {
            when (itemType) {
                ItemType.EXERCISE -> {
                    ExerciseMenuItem.entries.toList().forEachIndexed { index, item ->
                        DropdownMenuItem(text = {
                            Text(
                                text = stringResource(item.stringResource),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                            onClick = {
                                when (item) {
                                    ExerciseMenuItem.ADD_NOTE -> onFirstOption()
                                    ExerciseMenuItem.REPLACE -> onSecondOption()
                                    ExerciseMenuItem.REMOVE -> onThirdOption()
                                }
                                onClose()
                            })
                    }
                }

                ItemType.SET -> {
                    SetMenuItem.entries.toList().forEachIndexed { index, item ->
                        DropdownMenuItem(text = {
                            Text(
                                text = stringResource(item.stringResource),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                            onClick = {
                                when (item) {
                                    SetMenuItem.WARMUP -> onFirstOption()
                                    SetMenuItem.DROP_SET -> onSecondOption()
                                    SetMenuItem.FAILURE -> onThirdOption()
                                }
                                onClose()
                            })
                    }
                }
            }
        }
    }
}

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