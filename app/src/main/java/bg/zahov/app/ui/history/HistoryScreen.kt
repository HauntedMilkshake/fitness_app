package bg.zahov.app.ui.history

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.fitness.app.R


@Composable
fun HistoryScreen(historyViewModel: HistoryViewModel = viewModel(), onItemClick: (String) -> Unit) {
    val uiState by historyViewModel.uiState.collectAsStateWithLifecycle()
    HistoryContent(uiState.workouts, uiState.isDataLoading, onItemClick = { onItemClick(it) })
}

@Composable
fun HistoryContent(
    workouts: List<HistoryWorkout>,
    isDataLoading: Boolean,
    onItemClick: (String) -> Unit
) {
    if (isDataLoading) {
        CircularProgressIndicator(
            modifier = Modifier
                .width(64.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    } else {
        LazyColumn(Modifier.fillMaxSize()) {
            items(workouts) {
                Workout(it) {
                    onItemClick(it)
                }
            }
        }
    }
}

@Composable
fun Workout(item: HistoryWorkout, onItemClick: (String) -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .border(1.dp, colorResource(R.color.less_vibrant_text), RoundedCornerShape(4.dp))
            .clickable { onItemClick(item.id) }
    ) {
        Column(
            Modifier
                .padding(12.dp)
        ) {
            Text(
                text = item.name,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = item.date,
                color = colorResource(R.color.less_vibrant_text),
                style = MaterialTheme.typography.bodyLarge
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_clock),
                    contentDescription = null,
                    tint = colorResource(R.color.less_vibrant_text)
                )
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .padding(4.dp),
                    text = item.duration,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )

                Icon(
                    painter = painterResource(R.drawable.ic_volume),
                    contentDescription = null,
                    tint = colorResource(R.color.less_vibrant_text)
                )
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    text = item.volume,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )

                Icon(
                    painter = painterResource(R.drawable.ic_trophy),
                    contentDescription = null,
                    tint = colorResource(R.color.less_vibrant_text)
                )
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    text = item.personalRecords,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.exercise),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(R.string.best_set),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.exercises,
                    color = colorResource(R.color.less_vibrant_text),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = item.bestSets,
                    color = colorResource(R.color.less_vibrant_text),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
