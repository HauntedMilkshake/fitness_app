package bg.zahov.app.ui.topbar.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bg.zahov.app.ui.workout.toRestTime
import bg.zahov.app.util.parseTimeStringToLong
import bg.zahov.fitness.app.R
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

@Composable
fun TopBarWorkout(
    viewModel: TopBarWorkoutViewModel = hiltViewModel(),
    onRestClick: () -> Unit,
) {
    val workoutTime: String by viewModel.workoutTimer.map { it.toRestTime() }
        .collectAsStateWithLifecycle("")
    val elapsedRestTime: String by viewModel.restTimer.mapNotNull { it.elapsedTime }
        .collectAsStateWithLifecycle("")
    val restProgress: Float by viewModel.restTimer.mapNotNull {
        viewModel.calculateProgress(
            fullRest = (it.fullRest?.parseTimeStringToLong()?.toFloat() ?: 0f),
            currentRest = (it.elapsedTime?.parseTimeStringToLong()?.toFloat()
                ?: 0f)
        )
    }.collectAsStateWithLifecycle(1f)
    WorkoutTopBarContent(
        elapsedWorkoutTime = workoutTime,
        elapsedRestTime = elapsedRestTime,
        onMinimize = { viewModel.minimize() },
        onRestClick = onRestClick,
        restProgress = restProgress,
        onFinish = { viewModel.finish() }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WorkoutTopBarContent(
    elapsedWorkoutTime: String,
    elapsedRestTime: String,
    restProgress: Float,
    onMinimize: () -> Unit,
    onRestClick: () -> Unit,
    onFinish: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onMinimize, modifier = Modifier.padding(horizontal = 8.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_minimize),
                    contentDescription = stringResource(R.string.minimize_icon_content_description)
                )
            }
            if (elapsedRestTime.isEmpty() || restProgress == 0f) {
                IconButton(onClick = onRestClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_rest_timer),
                        contentDescription = stringResource(R.string.rest_icon_content_description)
                    )
                }
            } else {
                LinearProgressIndicator(
                    progress = { restProgress },
                    modifier = Modifier
                        .clickable(onClick = onRestClick)
                )
            }
        }

        Text(
            text = elapsedWorkoutTime,
            modifier = Modifier.align(alignment = Alignment.CenterVertically),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium
        )

        TextButton(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .testTag("FinishWorkout"),
            onClick = onFinish
        ) {
            Text(
                text = stringResource(R.string.finish_workout),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WorkoutTopBarNoRestView() {
    WorkoutTopBarContent(
        elapsedWorkoutTime = "00:25:43",
        elapsedRestTime = "",
        restProgress = 0.0f, // Empty progress
        onMinimize = { /* No-op */ },
        onRestClick = { /* No-op */ },
        onFinish = { /* No-op */ }
    )
}

@Preview(showBackground = true)
@Composable
fun WorkoutTopBarContentEmptyProgressPreview() {
    WorkoutTopBarContent(
        elapsedWorkoutTime = "00:25:43",
        elapsedRestTime = "00:01:30",
        restProgress = 0.0f, // Empty progress
        onMinimize = { /* No-op */ },
        onRestClick = { /* No-op */ },
        onFinish = { /* No-op */ }
    )
}

@Preview(showBackground = true)
@Composable
fun WorkoutTopBarContentHalfProgressPreview() {
    WorkoutTopBarContent(
        elapsedWorkoutTime = "00:25:43",
        elapsedRestTime = "00:01:30",
        restProgress = 0.5f, // Halfway progress
        onMinimize = { /* No-op */ },
        onRestClick = { /* No-op */ },
        onFinish = { /* No-op */ }
    )
}

@Preview(showBackground = true)
@Composable
fun WorkoutTopBarContentFullProgressPreview() {
    WorkoutTopBarContent(
        elapsedWorkoutTime = "00:25:43",
        elapsedRestTime = "00:01:30",
        restProgress = 1.0f, // Full progress
        onMinimize = { /* No-op */ },
        onRestClick = { /* No-op */ },
        onFinish = { /* No-op */ }
    )
}