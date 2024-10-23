package bg.zahov.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import bg.zahov.fitness.app.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Preview
@Composable
//homeViewModel: HomeViewModel = viewModel(), navController: NavController
fun HomeScreen() {
    HomeScreenContent("", "")
}

@Composable
fun HomeScreenContent(username: String, numberOfWorkouts: String) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 50.dp)
    ) {
        Row(horizontalArrangement = Arrangement.Start) {
            Icon(
                modifier = Modifier.padding(start = 20.dp).width(64.dp).height(64.dp),
                painter = painterResource(R.drawable.ic_profile_circle),
                contentDescription = null,
                tint = Color.White
            )
            Column(Modifier.padding(start = 15.dp)) {
                Text(text = "username", color = colorResource(R.color.text), fontSize = 25.sp)
                Text(text = "numberOfWorkouts", color = colorResource(R.color.text), fontSize = 15.sp)
            }
        }
        Text(
            modifier = Modifier.padding(start = 15.dp, top = 20.dp),
            text = stringResource(R.string.dashboard),
            color = colorResource(R.color.text),
            fontSize = 20.sp
        )
//        BarChartComponent()
    }

}

@Composable
fun BarChartComponent(modifier: Modifier = Modifier, barData: HomeViewModel.State.BarData) {
    AndroidView(modifier = modifier,
        factory = { context ->
            BarChart(context).apply {
                    //visibility =
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
//                    weeklyWorkoutsChart.notifyDataSetChanged()
//                    weeklyWorkoutsChart.invalidate()
            }
        })
}