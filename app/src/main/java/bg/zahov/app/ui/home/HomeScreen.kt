package bg.zahov.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.app.util.setupBarChart
import bg.zahov.fitness.app.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import androidx.compose.runtime.getValue
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Preview
@Composable
fun HomeScreen(homeViewModel: HomeViewModel = viewModel()) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    HomeScreenContent(
        uiState.username,
        uiState.numberOfWorkouts,
        uiState.barData,
        isChartLoading = uiState.isChartLoading
    )
}

@Composable
fun HomeScreenContent(
    username: String,
    numberOfWorkouts: String,
    barData: HomeViewModel.BarData,
    isChartLoading: Boolean = true
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 50.dp)
    ) {
        Row(horizontalArrangement = Arrangement.Start) {
            Icon(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .width(64.dp)
                    .height(64.dp),
                painter = painterResource(R.drawable.ic_profile_circle),
                contentDescription = null,
                tint = Color.White
            )
            Column(Modifier.padding(start = 15.dp)) {
                Text(text = username, color = colorResource(R.color.text), fontSize = 25.sp)
                Text(
                    text = numberOfWorkouts,
                    color = colorResource(R.color.text),
                    fontSize = 15.sp
                )
            }
        }
        Text(
            modifier = Modifier.padding(start = 15.dp, top = 20.dp),
            text = stringResource(R.string.dashboard),
            color = colorResource(R.color.text),
            fontSize = 20.sp
        )


        /**
         * ((200 / 2) - (64 / 2)) = 68 -> to place the loading indicator in the middle of the barChart whenever it is loading
         */
        if (isChartLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(64.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 68.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        } else {
            BarChartComponent(barData = barData)

        }
    }

}

@Composable
fun BarChartComponent(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(200.dp), barData: HomeViewModel.BarData
) {
    AndroidView(modifier = modifier,
        factory = { context ->
            val chart = BarChart(context)
            chart.setupBarChart()
            chart.apply {
                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(barData.weekRanges.toTypedArray())
                    axisMaximum = barData.xMax
                    axisMinimum = barData.xMin
                }
                axisRight.apply {
                    axisMaximum = barData.yMax
                    axisMinimum = barData.yMin
                }
                val dataSet = BarDataSet(barData.chartData, "workouts")
                dataSet.setDrawValues(false)
                val barData = BarData(dataSet)
                barData.barWidth = 0.5f
                data = barData
            }
        })
}