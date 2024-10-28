package bg.zahov.app.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.app.data.model.Workout
import bg.zahov.fitness.app.R


@Preview
@Composable
fun HistoryScreen(historyViewModel: HistoryViewModel = viewModel()) {

}

@Composable
fun HistoryContent() {
    LazyColumn(Modifier.fillMaxSize()) {

    }
}

@Preview
@Composable
fun Workout(item: Workout) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
    ) {
        Text(
            modifier = Modifier.padding(top = 20.dp),
            text = item.name,
            color = Color.White,
            fontSize = 18.sp
        )
        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = "",
            color = colorResource(R.color.less_vibrant_text),
            fontSize = 18.sp
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(painter = painterResource(R.drawable.ic_clock), contentDescription = null)
            Text(
                modifier = Modifier.weight(1f),
                text = "01:21:32",
                color = Color.White,
                fontSize = 18.sp
            )

            Icon(painter = painterResource(R.drawable.ic_volume), contentDescription = null)
            Text(
                modifier = Modifier.weight(1f),
                text = "2200.0kg",
                color = Color.White,
                fontSize = 18.sp
            )

            Icon(painter = painterResource(R.drawable.ic_trophy), contentDescription = null)
            Text(
                modifier = Modifier.weight(1f),
                text = "2",
                color = Color.White,
                fontSize = 18.sp
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, end = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stringResource(R.string.exercise), color = Color.White, fontSize = 17.sp)
            Text(text = stringResource(R.string.best_set), color = Color.White, fontSize = 17.sp)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, end = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "bp", color = colorResource(R.color.less_vibrant_text), fontSize = 15.sp)
            Text(
                text = "12 x 69",
                color = colorResource(R.color.less_vibrant_text),
                fontSize = 15.sp
            )
        }
    }
}
