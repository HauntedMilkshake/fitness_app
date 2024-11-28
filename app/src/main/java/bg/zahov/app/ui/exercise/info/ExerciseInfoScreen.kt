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
import androidx.compose.material3.Card
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
import bg.zahov.app.data.model.LineChartData
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.ui.custom.CommonLineChart
import bg.zahov.app.ui.theme.FitnessTheme
import bg.zahov.fitness.app.R
import com.github.mikephil.charting.data.Entry


@Composable
fun ExerciseInfoScreen() {
    val testHistory = listOf(
        ExerciseHistoryInfo(
            workoutName = "Bench Press",
            lastPerformed = "Yesterday",
            setsPerformed = "5 sets",
            oneRepMaxes = "100 kg"
        ),
        ExerciseHistoryInfo(
            workoutName = "Deadlift",
            lastPerformed = "2 days ago",
            setsPerformed = "4 sets",
            oneRepMaxes = "120 kg"
        )
    )
    val testLineChartDataList = listOf(
        LineChartData(
            text = "Weight Progress",
            maxValue = 100f,
            minValue = 50f,
            suffix = MeasurementType.Weight,
            list = listOf(
                Entry(1f, 60f),
                Entry(2f, 65f),
                Entry(3f, 70f),
                Entry(4f, 75f),
                Entry(5f, 80f)
            )
        ),
        LineChartData(
            text = "Running Distance",
            maxValue = 20f,
            minValue = 5f,
            suffix = MeasurementType.Weight,
            list = listOf(
                Entry(1f, 6f),
                Entry(2f, 8f),
                Entry(3f, 12f),
                Entry(4f, 15f),
                Entry(5f, 18f)
            )
        ),
        LineChartData(
            text = "Calories Burned",
            maxValue = 500f,
            minValue = 100f,
            suffix = MeasurementType.Weight,
            list = listOf(
                Entry(1f, 150f),
                Entry(2f, 200f),
                Entry(3f, 300f),
                Entry(4f, 400f),
                Entry(5f, 450f)
            )
        )
    )
    ExerciseInfoContent(charts = testLineChartDataList, history = testHistory)
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
                                        clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(8.dp))
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
        if (chart != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
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
                            sharedContentState = rememberSharedContentState(key = "${chart.text}-bounds"),
                            animatedVisibilityScope = this@AnimatedContent,
                            clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(8.dp))
                        )
                        .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp))
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

data class ExerciseHistoryInfo(
    val workoutName: String = "",
    val lastPerformed: String = "",
    val setsPerformed: String = "",
    val oneRepMaxes: String = "",
)