package bg.zahov.app.ui.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.fitness.app.R

@Composable
fun TopBarWorkout(
    viewModel: WorkoutTopBarViewModel = viewModel(),
    onRestClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    WorkoutTopBarContent(
        elapsedWorkoutTime = state.elapsedWorkoutTime,
        elapsedRestTime = state.elapsedRestTime,
        onMinimize = { viewModel.minimize() },
        onRestClick = onRestClick,
        restProgress = state.progress,
        onFinish = { viewModel.finish() }
    )

}

@Composable
fun WorkoutTopBarContent(
    elapsedWorkoutTime: String,
    elapsedRestTime: String = "",
    restProgress: Float,
    onMinimize: () -> Unit,
    onRestClick: () -> Unit,
    onFinish: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                IconButton(onClick = onMinimize, modifier = Modifier.padding(horizontal = 8.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_minimize),
                        contentDescription = null
                    )
                }
                if (elapsedRestTime.isEmpty()) {
                    IconButton(onClick = onRestClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_rest_timer),
                            contentDescription = null
                        )
                    }
                } else {
                    LinearProgressIndicator(
                        progress = { restProgress },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }


            TextButton(
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
        Text(
            text = elapsedWorkoutTime,
            modifier = Modifier.align(alignment = Alignment.Center),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium
        )
    }
}