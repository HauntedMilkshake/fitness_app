package bg.zahov.app.ui.home

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.fitness.app.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarDataSet
import androidx.compose.runtime.getValue
import com.github.mikephil.charting.components.XAxis
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.core.content.ContextCompat.getString
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

@Preview
@Composable
fun HomeScreen(homeViewModel: HomeViewModel = viewModel()) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val chartFormatter = remember(uiState.barData) {
        IndexAxisValueFormatter(uiState.barData.weekRanges.toTypedArray())
    }

    HomeScreenContent(
        uiState.username,
        uiState.numberOfWorkouts,
        uiState.barData,
        isChartLoading = uiState.isChartLoading,
        valueFormatter = chartFormatter
    )
}

@Composable
fun HomeScreenContent(
    username: String,
    numberOfWorkouts: String,
    barData: BarData,
    isChartLoading: Boolean = true,
    valueFormatter: ValueFormatter
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 52.dp)
    ) {
        Row(horizontalArrangement = Arrangement.Start) {
            Icon(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .size(64.dp),
                painter = painterResource(R.drawable.ic_profile_circle),
                contentDescription = null,
                tint = Color.White
            )
            Column(Modifier.padding(start = 16.dp).semantics(mergeDescendants = true){
                contentDescription = "Username: $username, number of workouts: $numberOfWorkouts"
            }) {
                Text(
                    text = username,
                    style = MaterialTheme.typography.titleLarge,
                    color = colorResource(R.color.text)
                )
                Text(
                    text = numberOfWorkouts,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorResource(R.color.text)
                )
            }
        }
        Text(
            modifier = Modifier.padding(start = 15.dp, top = 20.dp),
            text = stringResource(R.string.dashboard),
            style = MaterialTheme.typography.bodyLarge,
            color = colorResource(R.color.text)
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (isChartLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            } else {
                BarChartComponent(barData = barData, chartFormatter = valueFormatter)
            }
        }
    }
}

@Composable
fun BarChartComponent(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(200.dp),
    barData: BarData,
    chartFormatter: ValueFormatter
) {
    AndroidView(modifier = modifier,
        factory = { context ->
            val chart = BarChart(context)
            chart.setupBarChart(context)
            chart.apply {
                chart.xAxis.valueFormatter = chartFormatter
                chart.xAxis.axisMaximum = barData.xMax
                chart.xAxis.axisMinimum = barData.xMin
                chart.axisRight.axisMaximum = barData.yMax
                chart.axisRight.axisMinimum = barData.yMin
                val dataSet = BarDataSet(barData.chartData, "")
                dataSet.setDrawValues(false)
                val barData = com.github.mikephil.charting.data.BarData(dataSet)
                barData.barWidth = 0.5f
                data = barData
            }
        })
}

fun BarChart.setupBarChart(context: Context) {
    setFitBars(true)
    legend.isEnabled = false
    isDoubleTapToZoomEnabled = false
    axisLeft.isEnabled = false
    isDragEnabled = false
    isHighlightFullBarEnabled = false

    description.setPosition(250f, 60f)
    description.text = getString(context, R.string.weekly_workouts)
    description.textColor = android.graphics.Color.WHITE

    xAxis.position = XAxis.XAxisPosition.BOTTOM
    xAxis.granularity = 1f
    xAxis.axisMinimum = 0f
    xAxis.textColor = android.graphics.Color.WHITE
    xAxis.setCenterAxisLabels(true)
    xAxis.isGranularityEnabled = true

    axisRight.textColor = android.graphics.Color.WHITE
    axisRight.granularity = 1f
    axisRight.setDrawGridLines(false)
}