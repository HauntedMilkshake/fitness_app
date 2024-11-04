package bg.zahov.app.ui.workout.start

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.fitness.app.R


@Composable
fun StartWorkoutScreen(startWorkoutViewModel: StartWorkoutViewModel = viewModel()) {
}

@Preview
@Composable
fun StartWorkoutContent() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.padding(top = 32.dp, start = 16.dp),
            text = stringResource(R.string.quick_start_text),
            style = MaterialTheme.typography.labelSmall,
            color = colorResource(R.color.text)
        )

        Button(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp), colors = ButtonColors(
            containerColor = Color.Blue,
            contentColor = Color.White,
            colorResource(R.color.disabled_button),
            colorResource(R.color.background)
        ), onClick = {}) {
            Text(
                text = stringResource(R.string.start_empty_workout_text),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                modifier = Modifier.padding(top = 16.dp, start = 16.dp),
                text = stringResource(R.string.my_templates_text),
                style = MaterialTheme.typography.labelSmall,
                color = colorResource(R.color.text)
            )

            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    tint = Color.Unspecified,
                    contentDescription = ""
                )
            }
        }

        LazyColumn(Modifier.fillMaxSize()) {

        }
    }
}

@Preview
@Composable
fun Workout(exercises: List<String> = listOf()) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "WORKOUT LABEL",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
            //TODO(Dropdown menu)
        }
        Text(
            text = "SOME DATE",
            color = colorResource(R.color.text),
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        for (i in 0..exercises.size) {
            Text(
                text = "SOME DATE",
                color = colorResource(R.color.text),
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

        }
    }
}