package bg.zahov.app.ui.history

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.app.data.provider.model.HistoryWorkout
import bg.zahov.app.ui.custom.ExerciseWithSets
import bg.zahov.app.ui.custom.WorkoutStats
import bg.zahov.app.ui.theme.FitnessTheme
import bg.zahov.fitness.app.R


@Composable
fun HistoryScreen(historyViewModel: HistoryViewModel = viewModel(), onItemClick: () -> Unit) {
    val uiState by historyViewModel.uiState.collectAsStateWithLifecycle()
    HistoryContent(uiState.workouts, onItemClick = {
        historyViewModel.setClickedWorkout(it)
        onItemClick()
    })
}

@Composable
fun HistoryContent(
    workouts: List<HistoryWorkout>,
    onItemClick: (String) -> Unit
) {
    val animationDuration = integerResource(R.integer.animation_duration_medium)
    FitnessTheme {
        AnimatedContent(
            workouts.size,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(animationDuration)
                ) togetherWith fadeOut(
                    targetAlpha = animationDuration
                        .toFloat()
                )
            },
            label = ""
        ) {
            when (it) {
                0 -> {
                    CircularProgressIndicator(
                        modifier = Modifier,
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                else -> {
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(items = workouts, key = { it.id }) {
                            Workout(it) {
                                onItemClick(it)
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun Workout(item: HistoryWorkout, onItemClick: (String) -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(4.dp))
            .padding(12.dp)
            .clickable { onItemClick(item.id) }
    ) {
        Text(
            text = item.name,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = item.date,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge
        )

        WorkoutStats(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            duration = item.duration,
            volume = item.volume,
            personalRecords = item.personalRecords
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.exercise),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(R.string.best_set),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        for (i in item.exercises.indices) {
            ExerciseWithSets(exerciseName = item.exercises[i], bestSet = item.bestSets[i])
        }
    }
}

@Composable
fun ExerciseWithSets(exerciseName: String, bestSet: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = exerciseName,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
            softWrap = true,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )

        Text(
            text = bestSet,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}

@Composable
fun TextWithLeadingIcon(
    text: String,
    icon: Painter,
    textModifier: Modifier = Modifier,
    textColor: Color = Color.White,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    textSoftWrap: Boolean = false,
    textOverflow: TextOverflow = TextOverflow.Ellipsis,
    iconModifier: Modifier = Modifier,
    iconColor: Color? = null,
    contentDescription: String? = null
) {
    Icon(
        modifier = iconModifier,
        contentDescription = contentDescription,
        painter = icon,
        tint = iconColor ?: LocalContentColor.current
    )
    Text(
        text = text,
        modifier = textModifier,
        color = textColor,
        style = textStyle,
        softWrap = textSoftWrap,
        overflow = textOverflow
    )
}