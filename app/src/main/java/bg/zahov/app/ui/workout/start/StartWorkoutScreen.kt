package bg.zahov.app.ui.workout.start

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.res.colorResource
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


@Composable
fun StartWorkoutScreen(
    startWorkoutViewModel: StartWorkoutViewModel = viewModel(),
    onEditWorkout: (String) -> Unit,
    onAddTemplateWorkout: () -> Unit
) {
    //TODO(LaunchedUi effect)
    val uiState by startWorkoutViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        uiState.notifyUser?.let {
            showToast(it, context)
        }
    }
    StartWorkoutContent(workouts = uiState.workouts,
        onAddTemplateWorkout = { onAddTemplateWorkout() } ,
        onStartEmptyWorkout = { startWorkoutViewModel.startEmptyWorkout() },
        onEditWorkout = { onEditWorkout(it) },
        onDeleteWorkout = { startWorkoutViewModel.deleteTemplateWorkout(it) },
        onDuplicateWorkout = { startWorkoutViewModel.addDuplicateTemplateWorkout(it) }
    )

}

@Composable
fun StartWorkoutContent(
    workouts: List<StartWorkout>,
    onAddTemplateWorkout: () -> Unit,
    onStartEmptyWorkout: () -> Unit,
    onEditWorkout: (String) -> Unit,
    onDeleteWorkout: (StartWorkout) -> Unit,
    onDuplicateWorkout: (StartWorkout) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.padding(top = 32.dp, start = 16.dp),
            text = stringResource(R.string.quick_start_text),
            style = MaterialTheme.typography.labelSmall,
            color = colorResource(R.color.text)
        )

        Button(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp), colors = ButtonColors(
            containerColor = Color.Blue,
            contentColor = Color.White,
            colorResource(R.color.disabled_button),
            colorResource(R.color.background)
        ), onClick = { onStartEmptyWorkout() }) {
            Text(
                text = stringResource(R.string.start_empty_workout_text),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                modifier = Modifier.padding(top = 16.dp, start = 16.dp),
                text = stringResource(R.string.my_templates_text),
                style = MaterialTheme.typography.labelSmall,
                color = colorResource(R.color.text)
            )

            IconButton(onClick = { onAddTemplateWorkout() }) {
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    tint = Color.Unspecified,
                    contentDescription = ""
                )
            }
        }

        LazyColumn(Modifier.fillMaxSize()) {
            items(count = workouts.size, key = {}) {
                Workout(workoutName = workouts[it].name,
                    workoutDate = workouts[it].date,
                    exercises = workouts[it].exercises,
                    onEdit = { onEditWorkout(workouts[it].id) },
                    onDelete = { onDeleteWorkout(workouts[it]) },
                    onDuplicate = { onDuplicateWorkout(workouts[it]) })
            }
        }
    }
}

@Composable
fun Workout(
    workoutName: String, workoutDate: String, exercises: List<String> = listOf(),
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit
) {
    var isDropDownExpanded by remember {
        mutableStateOf(false)
    }

    //TODO(Make clickable)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(4.dp))
            .padding(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = workoutName,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
            DropDown(isDropDownExpanded = isDropDownExpanded,
                onExpand = { isDropDownExpanded = true },
                onClose = { isDropDownExpanded = false },
                onEdit = { onEdit() },
                onDuplicate = { onDelete() },
                onDelete = { onDuplicate() })
        }
        Text(
            text = workoutDate,
            color = colorResource(R.color.text),
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        for (i in 0..exercises.size) {
            Text(
                text = exercises[i],
                color = colorResource(R.color.text),
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
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    onExpand()
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings_dots),
                    contentDescription = ""
                )
            }
            DropdownMenu(
                expanded = isDropDownExpanded,
                onDismissRequest = {
                    onClose()
                }) {
                MenuItem.entries.toList().forEachIndexed { index, item ->
                    DropdownMenuItem(text = {
                        Text(text = item.label)
                    },
                        onClick = {
                            onClose()
                            when (item) {
                                MenuItem.EDIT -> onEdit()
                                MenuItem.DELETE -> onDelete()
                                MenuItem.DUPLICATE -> onDuplicate()
                            }
                        })
                }
            }
        }
    }
}

fun showToast(message: String, context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

enum class MenuItem(val label: String) {
    EDIT("Edit"), DELETE("Delete"), DUPLICATE("Duplicate")
}