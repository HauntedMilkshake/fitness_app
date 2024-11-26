package bg.zahov.app.ui.workout.finish

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.app.data.provider.model.HistoryWorkout
import bg.zahov.app.ui.history.Workout
import bg.zahov.fitness.app.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp


@Composable
fun WorkoutFinishScreen(
    finishWorkoutViewModel: WorkoutFinishViewModel = viewModel(),
    onClose: () -> Unit,
) {
    val state by finishWorkoutViewModel.uiState.collectAsStateWithLifecycle()
    WorkoutFinishContent(
        workout = state.workout,
        workoutCount = state.workoutCount,
        onClose = onClose
    )
}

@Composable
fun WorkoutFinishContent(workout: HistoryWorkout, workoutCount: String, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(modifier = Modifier.align(Alignment.Start), onClick = onClose) {
            Icon(
                modifier = Modifier.size(64.dp),
                painter = painterResource(R.drawable.ic_close),
                contentDescription = stringResource(R.string.close_icon_description),
                tint = MaterialTheme.colorScheme.tertiary

            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedIcon()

        Spacer(modifier = Modifier.height(24.dp))

        Workout(item = workout, onItemClick = {})
        Text(
            modifier = Modifier.align(Alignment.Start),
            text = stringResource(R.string.this_is_your_workout_number, workoutCount),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
}

@Composable
fun AnimatedIcon() {
    val scale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.2f,
            animationSpec = tween(durationMillis = 500)
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500)
        )
    }

    Icon(
        modifier = Modifier
            .size(64.dp)
            .scale(scale = scale.value),
        painter = painterResource(R.drawable.ic_star),
        contentDescription = stringResource(R.string.animated_star_icon_description),
        tint = Color.Unspecified
    )
}