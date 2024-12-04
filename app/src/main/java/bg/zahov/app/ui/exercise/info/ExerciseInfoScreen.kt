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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bg.zahov.app.data.model.LineChartData
import bg.zahov.app.ui.custom.CommonDivider
import bg.zahov.app.ui.custom.CommonLineChart
import bg.zahov.app.ui.theme.FitnessTheme
import bg.zahov.fitness.app.R

@Preview
@Composable
fun ExerciseInfoScreen() {
    ExerciseInfoContent(
        oneRepMaxEst = ExerciseInfoTestData.testOneRepMaxEst,
        maxVolume = ExerciseInfoTestData.testMaxVolume,
        maxRep = ExerciseInfoTestData.testMaxRep,
        history = ExerciseInfoTestData.testHistory
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .animateContentSize()
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExerciseChartInfo(
                        data = oneRepMaxEst,
                        selected = selectedChart == oneRepMaxEst,
                        onClick = { selectedChart = oneRepMaxEst }
                    )
                    ExerciseChartInfo(
                        data = maxVolume,
                        selected = selectedChart == maxVolume,
                        onClick = { selectedChart = maxVolume }
                    )
                    ExerciseChartInfo(
                        data = maxRep,
                        selected = selectedChart == maxRep,
                        onClick = { selectedChart = maxRep }
                    )
                }
                CommonDivider(modifier = Modifier.padding(0.dp))
                Text(
                    text = stringResource(R.string.history),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.displayLarge
                )
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(history) { data -> ExerciseHistoryCard(data = data) }
                }
            }
            ChartDetails(
                data = selectedChart,
                onConfirmClick = { selectedChart = null }
            )
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ChartDetails(
    data: LineChartData?,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        modifier = modifier.animateContentSize(),
        targetState = data,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = ""
    ) { chart ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            chart?.let {
                val extractedText = it.textId?.let { it1 -> stringResource(it1) } ?: ""
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onConfirmClick() }
                        .background(Color.Black.copy(alpha = 0.5f)),
                )
                Column(
                    modifier = Modifier
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "$extractedText-bound"),
                            animatedVisibilityScope = this@AnimatedContent,
                            clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(8.dp)),
                            resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                        )
                        .background(
                            MaterialTheme.colorScheme.secondary,
                            RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "$extractedText-text"),
                            animatedVisibilityScope = this@AnimatedContent
                        ),
                        text = extractedText, color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.titleLarge
                    )
                    CommonLineChart(data = it)
                    Text(
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .sharedElement(
                                rememberSharedContentState(key = "$extractedText-value"),
                                animatedVisibilityScope = this@AnimatedContent
                            ),
                        text = "${stringResource(R.string.personal_records)}: ${it.maxValue}",
                        color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ExerciseChartInfo(
    data: LineChartData,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = selected.not(),
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
        modifier = modifier.animateContentSize()
    ) {
        val extractedText = data.textId?.let { stringResource(it) } ?: ""
        Column(
            modifier = Modifier
                .sharedBounds(
                    rememberSharedContentState(key = "$extractedText-bound"),
                    animatedVisibilityScope = this@AnimatedVisibility,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                    clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(8.dp)),
                )
                .animateContentSize()
                .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .clickable { onClick() }
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painter = painterResource(R.drawable.ic_trophy), contentDescription = null)
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "$extractedText-text"),
                            animatedVisibilityScope = this@AnimatedVisibility
                        ),
                        text = extractedText, color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            Text(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .sharedElement(
                        rememberSharedContentState(key = "$extractedText-value"),
                        animatedVisibilityScope = this@AnimatedVisibility
                    ),
                text = "${stringResource(R.string.personal_records)}: ${data.maxValue}",
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.labelMedium
            )
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
            .background(
                MaterialTheme.colorScheme.secondary,
                RoundedCornerShape(8.dp)
            )
            .border(1.dp, MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = data.workoutName,
                color = MaterialTheme.colorScheme.onSecondary,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .weight(1f)
            )
            Text(
                text = data.lastPerformed,
                color = MaterialTheme.colorScheme.onSecondary,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .weight(0.5f)
                    .wrapContentWidth(Alignment.End)
            )
        }
        CommonDivider(
            modifier = Modifier
                .padding(0.dp)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSecondary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.sets_performed),
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = data.setsPerformed,
                    color = MaterialTheme.colorScheme.onSecondary,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.End)
            ) {
                Text(
                    text = stringResource(R.string.one_rep_max_text),
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = data.oneRepMaxes,
                    color = MaterialTheme.colorScheme.onSecondary,
                    overflow = TextOverflow.Ellipsis,
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