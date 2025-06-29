package bg.zahov.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.app.ui.custom.CommonBarChart
import bg.zahov.fitness.app.R
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = hiltViewModel()) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val chartFormatter = remember(uiState.data) {
        IndexAxisValueFormatter(uiState.data.weekRanges.toTypedArray())
    }
    HomeScreenContent(
        uiState.username,
        uiState.numberOfWorkouts,
        uiState.data,
        isChartLoading = uiState.isChartLoading,
        valueFormatter = chartFormatter,
    )
}

@Composable
fun HomeScreenContent(
    username: String,
    numberOfWorkouts: String,
    chartData: ChartData,
    valueFormatter: ValueFormatter,
    modifier: Modifier = Modifier,
    isChartLoading: Boolean = true,
) {
    val accessibilityText = stringResource(
        R.string.home_accessibility_text,
        username,
        numberOfWorkouts
    )

    Column(
        modifier
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
            Column(
                Modifier
                    .padding(start = 16.dp)
                    .semantics(mergeDescendants = true) {
                        contentDescription = accessibilityText
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
                CommonBarChart(chartData = chartData, chartFormatter = valueFormatter)
            }
        }
    }
}