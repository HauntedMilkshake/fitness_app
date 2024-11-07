package bg.zahov.app.ui.workout.start

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.fitness.app.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bg.zahov.app.data.model.ToastManager
import bg.zahov.app.ui.theme.FitnessTheme


@Composable
fun StartWorkoutScreen(
    startWorkoutViewModel: StartWorkoutViewModel = viewModel(),
    onWorkoutClick: (String) -> Unit,
    onEditWorkout: (String) -> Unit,
    onAddTemplateWorkout: () -> Unit
) {
    val uiState by startWorkoutViewModel.uiState.collectAsStateWithLifecycle()
    val toast by ToastManager.messages.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(toast) {
        toast?.let { message ->
            Toast.makeText(context, context.getString(message.messageResId), Toast.LENGTH_SHORT)
                .show()
        }
    }

    StartWorkoutContent(workouts = uiState.workouts,
        onWorkoutClick = { onWorkoutClick(it) },
        onWorkoutStart = { startWorkoutViewModel.startWorkout(it) },
        onAddTemplateWorkout = onAddTemplateWorkout,
        onStartEmptyWorkout = { startWorkoutViewModel.startWorkout() },
        onEditWorkout = { onEditWorkout(it) },
        onDeleteWorkout = { startWorkoutViewModel.deleteTemplateWorkout(it) },
        onDuplicateWorkout = { startWorkoutViewModel.addDuplicateTemplateWorkout(it) }
    )

}

@Composable
fun StartWorkoutContent(
    workouts: List<StartWorkout>,
    onWorkoutClick: (String) -> Unit,
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
                color = MaterialTheme.colorScheme.secondary
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp), colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    disabledContainerColor = MaterialTheme.colorScheme.onErrorContainer,
                    disabledContentColor = MaterialTheme.colorScheme.background
                ), onClick = onStartEmptyWorkout
            ) {
                Text(
                    text = stringResource(R.string.start_empty_workout_text),
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSecondary,
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
                    color = MaterialTheme.colorScheme.secondary
                )

                IconButton(onClick = onAddTemplateWorkout) {
                    Icon(
                        painter = painterResource(R.drawable.ic_plus),
                        tint = Color.Unspecified,
                        contentDescription = ""
                    )
                }
            }

            LazyColumn(Modifier.fillMaxSize()) {
                items(items = workouts, key = { it.id }) {
                    Workout(
                        modifier = Modifier.animateItem(),
                        workoutName = it.name,
                        workoutDate = it.date,
                        exercises = it.exercises,
                        onWorkoutClick = { onWorkoutClick(it.id) },
                        onWorkoutStart = { onWorkoutStart(it) },
                        onEdit = { onEditWorkout(it.id) },
                        onDelete = { onDeleteWorkout(it) },
                        onDuplicate = { onDuplicateWorkout(it) })
                }
            }
        }
    }
}

@Composable
fun Workout(
    modifier: Modifier,
    workoutName: String, workoutDate: String, exercises: List<String> = listOf(),
    onWorkoutClick: () -> Unit,
    onWorkoutStart: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit
) {
    var isDropDownExpanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { onWorkoutClick() }
            .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(4.dp))
            .padding(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = workoutName,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSecondary,
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
            color = MaterialTheme.colorScheme.onSecondary,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        for (i in 0 until exercises.size) {
            Text(
                text = exercises[i],
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.primary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.primary)
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
            modifier = Modifier.background(color = MaterialTheme.colorScheme.primary),
            expanded = isDropDownExpanded,
            onDismissRequest = onClose
        ) {
            MenuItem.entries.toList().forEachIndexed { index, item ->
                DropdownMenuItem(text = {
                    Text(
                        text = item.label,
                        color = MaterialTheme.colorScheme.onSecondary,
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

enum class MenuItem(val label: String) {
    EDIT("Edit"), DELETE("Delete"), DUPLICATE("Duplicate"), START("Start")
}