package bg.zahov.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import bg.zahov.app.ui.BottomBar
import bg.zahov.app.ui.theme.FitnessTheme
import bg.zahov.fitness.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(workoutManagerViewModel: WorkoutManagerViewModel) {
    val navController = rememberNavController()
    val state by workoutManagerViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isWorkoutActive) {
        if (state.isWorkoutActive) navController.navigate(Workout)
    }

    FitnessTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier,
                    title = {
                        Text(
                            text = stringResource(R.string.one_rep_max_title_text),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_settings),
                                contentDescription = stringResource(R.string.menu)
                            )
                        }
                    }
                )
            },
            bottomBar = {
                Column {
                    if (state.trailingWorkoutVisibility) {
                        TrailingWorkout(
                            workoutName = state.workoutName,
                            elapsedTime = state.timer,
                            onClick = { workoutManagerViewModel.updateStateToActive() }
                        )
                    }
                    BottomBar(navController = navController)
                }
            }
        ) { padding ->
            MainNavGraph(
                modifier = Modifier.padding(padding),
                navController = navController,
            )
        }
    }
}

@Composable
fun TrailingWorkout(workoutName: String, elapsedTime: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .shadow(elevation = 4.dp)
            .clickable {
                onClick()
            }, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = workoutName, style = MaterialTheme.typography.labelLarge)
        Text(text = elapsedTime, style = MaterialTheme.typography.labelLarge)
    }
}