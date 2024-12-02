package bg.zahov.app.ui.exercise.info

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import bg.zahov.app.data.model.LineChartData
import bg.zahov.app.ui.custom.CommonLineChart
import bg.zahov.app.ui.theme.FitnessTheme
import bg.zahov.fitness.app.R

@Composable
fun ExerciseInfoScreen() {
    ExerciseInfoContent(
        oneRepMaxEst = TestData.testOneRepMaxEst,
        maxVolume = TestData.testMaxVolume,
        maxRep = TestData.testMaxRep,
        history = TestData.testHistory
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ExerciseInfoContent(
    oneRepMaxEst: LineChartData,
    maxVolume: LineChartData,
    maxRep: LineChartData,
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExerciseChartInfo(
                        data = oneRepMaxEst,
                        selected = selectedChart == oneRepMaxEst,
                        onClick = { selectedChart = oneRepMaxEst },
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                    ExerciseChartInfo(
                        data = maxVolume,
                        selected = selectedChart == maxVolume,
                        onClick = { selectedChart = maxVolume },
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                    ExerciseChartInfo(
                        data = maxRep,
                        selected = selectedChart == maxRep,
                        onClick = { selectedChart = maxRep },
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
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
                sharedTransitionScope = this@SharedTransitionLayout,
                onConfirmClick = {
                    selectedChart = null
                }
            )
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ChartDetails(
    data: LineChartData?,
    sharedTransitionScope: SharedTransitionScope,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    with(sharedTransitionScope) {
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
                chart?.let {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onConfirmClick() }
                            .background(Color.Black.copy(alpha = 0.5f)),
                    )
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(key = "${it.text}-bounds"),
                                animatedVisibilityScope = this@AnimatedContent,
                                clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(8.dp))
                            )
                            .background(
                                MaterialTheme.colorScheme.secondary,
                                RoundedCornerShape(8.dp)
                            )
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .sharedElement(
                                    rememberSharedContentState(key = it.text),
                                    animatedVisibilityScope = this@AnimatedContent
                                ),
                            text = it.text, color = MaterialTheme.colorScheme.onSecondary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        CommonLineChart(modifier = Modifier.width(300.dp), data = it)
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .sharedElement(
                                    rememberSharedContentState(key = it.maxValue),
                                    animatedVisibilityScope = this@AnimatedContent
                                ),
                            text = "${stringResource(R.string.personal_records)}: ${it.maxValue}",
                            color = MaterialTheme.colorScheme.onSecondary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ExerciseChartInfo(
    data: LineChartData,
    selected: Boolean,
    onClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier = Modifier,
) {
    with(sharedTransitionScope) {
        AnimatedVisibility(
            visible = selected.not(),
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.animateContentSize()
        ) {
            Column(
                modifier = modifier
                    .padding(20.dp)
                    .clickable { onClick() }
                    .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp))
                    .sharedBounds(
                        rememberSharedContentState(key = "bounds-${data.text}"),
                        animatedVisibilityScope = this@AnimatedVisibility,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .sharedElement(
                            rememberSharedContentState(key = data.text),
                            animatedVisibilityScope = this@AnimatedVisibility
                        ),
                    text = data.text, color = MaterialTheme.colorScheme.onSecondary,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .sharedElement(
                            rememberSharedContentState(key = data.maxValue),
                            animatedVisibilityScope = this@AnimatedVisibility
                        ),
                    text = "${stringResource(R.string.personal_records)}: ${data.maxValue}",
                    color = MaterialTheme.colorScheme.onSecondary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun ExerciseHistoryCard(
    data: ExerciseHistoryInfo,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                MaterialTheme.colorScheme.secondary,
                RoundedCornerShape(16.dp)
            ),
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

data class ExerciseHistoryInfo(
    val workoutName: String = "",
    val lastPerformed: String = "",
    val setsPerformed: String = "",
    val oneRepMaxes: String = "",
)