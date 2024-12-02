package bg.zahov.app.ui.exercise.info

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.app.data.model.LineChartData
import bg.zahov.app.data.model.state.ExerciseHistoryInfo
import bg.zahov.app.ui.custom.CommonLineChart
import bg.zahov.app.ui.theme.FitnessTheme
import bg.zahov.fitness.app.R

@Composable
fun ExerciseInfoScreen(viewModel: ExerciseInfoViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ExerciseInfoContent(
        charts = listOf(uiState.maxRep, uiState.maxVolume, uiState.oneRepMaxEst),
        history = uiState.exerciseHistory
    )
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ExerciseInfoContent(
    charts: List<LineChartData>,
    history: List<ExerciseHistoryInfo>
) {
    var selectedChart by remember { mutableStateOf<LineChartData?>(null) }

    FitnessTheme {
        SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(charts) { chart ->
                        AnimatedVisibility(
                            visible = chart != selectedChart,
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut(),
                            modifier = Modifier.animateItem()
                        ) {
                            Box(
                                modifier = Modifier
                                    .sharedBounds(
                                        sharedContentState = rememberSharedContentState(key = "${chart.text}-bounds"),
                                        animatedVisibilityScope = this@AnimatedVisibility,
                                        clipInOverlayDuringTransition = OverlayClip(
                                            RoundedCornerShape(8.dp)
                                        )
                                    )
                            ) {
                                ExerciseChartInfo(
                                    data = chart,
                                    modifier = Modifier.sharedElement(
                                        state = rememberSharedContentState(key = chart.text),
                                        animatedVisibilityScope = this@AnimatedVisibility
                                    ),
                                    onClick = { selectedChart = chart }
                                )
                            }
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.history),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge
                )
                LazyColumn {
                    items(history) { data ->
                        ExerciseHistoryCard(data = data)
                    }
                }
            }
            ChartDetails(
                data = selectedChart,
                onConfirmClick = {
                    selectedChart = null
                }
            )
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ChartDetails(
    data: LineChartData?,
    modifier: Modifier = Modifier,
    onConfirmClick: () -> Unit
) {
    AnimatedContent(
        modifier = modifier,
        targetState = data,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = ""
    ) { chart ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (chart != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onConfirmClick() }
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(key = "${chart.text}-bounds"),
                                animatedVisibilityScope = this@AnimatedContent,
                                clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(8.dp))
                            )
                            .background(
                                MaterialTheme.colorScheme.secondary,
                                RoundedCornerShape(8.dp)
                            )
                            .clip(RoundedCornerShape(8.dp))
                    ) {

                        ExerciseChartInfo(
                            data = chart,
                            modifier = Modifier.sharedElement(
                                state = rememberSharedContentState(key = chart.text),
                                animatedVisibilityScope = this@AnimatedContent,
                            ),
                            onClick = onConfirmClick
                        )
                        CommonLineChart(modifier = Modifier.width(300.dp), data = chart)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ExerciseChartInfo(
    modifier: Modifier = Modifier,
    data: LineChartData = LineChartData(),
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .padding(20.dp)
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            text = data.text, color = MaterialTheme.colorScheme.onSecondary,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            text = "${stringResource(R.string.personal_records)}: ${data.maxValue}",
            color = MaterialTheme.colorScheme.onSecondary,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview
@Composable
fun ExerciseHistoryCard(
    modifier: Modifier = Modifier,
    data: ExerciseHistoryInfo = ExerciseHistoryInfo()
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(16.dp)),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = data.workoutName,
            color = MaterialTheme.colorScheme.onSecondary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = data.lastPerformed,
            color = MaterialTheme.colorScheme.onSecondary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column {
                Text(
                    text = stringResource(R.string.sets_performed),
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = data.setsPerformed,
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Column {
                Text(
                    text = stringResource(R.string.one_rep_max_text),
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = data.oneRepMaxes,
                    color = MaterialTheme.colorScheme.onSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}