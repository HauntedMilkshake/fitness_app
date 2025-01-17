package bg.zahov.app.ui.history.info

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.fitness.app.R
import bg.zahov.app.ui.custom.ExerciseWithSets
import bg.zahov.app.ui.custom.WorkoutStats
import bg.zahov.app.ui.theme.FitnessTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bg.zahov.app.data.model.ToastManager
import bg.zahov.app.data.provider.model.ExerciseDetails

//    private val historyInfoViewModel: HistoryInfoViewModel by viewModels(
//        ownerProducer = { requireParentFragment() }
//    )
@Composable
fun HistoryInfoScreen(
    historyInfoViewModel: HistoryInfoViewModel = viewModel(),
    onDelete: () -> Unit,
) {
    val state by historyInfoViewModel.uiState.collectAsStateWithLifecycle()
    val toast by ToastManager.messages.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(toast) {
        toast?.let { message ->
            Toast.makeText(context, context.getString(message.messageResId), Toast.LENGTH_SHORT)
                .show()
        }
    }

    if (state.isDeleted) {
        LaunchedEffect(Unit) {
            onDelete()
        }
    }

    HistoryInfoContent(
        date = state.workoutDate,
        duration = state.duration,
        volume = state.volume,
        personalRecords = state.prs,
        exercises = state.exercisesInfo,
        onClick = { historyInfoViewModel.performAgain() })
}

@Composable
fun HistoryInfoContent(
    date: String,
    duration: String,
    volume: String,
    personalRecords: String,
    exercises: List<ExerciseDetails>,
    onClick: () -> Unit,
) {
    FitnessTheme {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)) {
            Text(
                text = date,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            WorkoutStats(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                duration = duration,
                volume = volume,
                personalRecords = personalRecords
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(items = exercises) {
                        Exercise(
                            exerciseName = it.exerciseName,
                            sets = it.sets,
                            oneRepMaxes = it.oneRepMaxes
                        )
                    }
                }
                Button(modifier = Modifier
                    .width(240.dp)
                    .padding(top = 8.dp), onClick = onClick) {
                    Text(text = stringResource(R.string.perform_again))
                }
            }
        }
    }
}

@Composable
fun Exercise(
    exerciseName: String,
    sets: List<String>,
    oneRepMaxes: List<String>,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = exerciseName,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = stringResource(R.string.one_rep_max_text),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
    for (i in sets.indices) {
        ExerciseWithSets(sets[i], oneRepMaxes[i])
    }
}