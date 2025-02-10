package bg.zahov.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import bg.zahov.app.ui.BottomBar
import bg.zahov.app.ui.theme.FitnessTheme
import bg.zahov.app.ui.topbar.TopBar
import kotlinx.coroutines.launch

@Composable
fun App(workoutManagerViewModel: WorkoutManagerViewModel) {
    val navController = rememberNavController()
    val state by workoutManagerViewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    val snackScope = rememberCoroutineScope()

    LaunchedEffect(state.isWorkoutActive) {
        if (state.isWorkoutActive) navController.navigate(Workout)
    }
    FitnessTheme {
        Scaffold(
            topBar = {
                TopBar(navController = navController)
            },
            floatingActionButtonPosition = FabPosition.Center,
            floatingActionButton = {
                if (state.trailingWorkoutVisibility) {
                    TrailingWorkout(
                        workoutName = state.workoutName,
                        elapsedTime = state.timer,
                        onClick = { workoutManagerViewModel.updateStateToActive() }
                    )
                }
            },
            bottomBar = {
                Column {
                    BottomBar(navController = navController)
                }
            },
            snackbarHost = { SnackbarHost(snackBarHostState) }
        ) { padding ->
            MainNavGraph(
                modifier = Modifier.padding(padding),
                onShowSnackBar = { message, action ->
                    snackScope.launch {
                        snackBarHostState.showSnackbar(
                            message = message,
                            actionLabel = action,
                            duration = SnackbarDuration.Short
                            )
                    }
                },
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
            .clip(RoundedCornerShape(3.dp))
            .shadow(elevation = 4.dp)
            .clickable {
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(4.dp),
            text = workoutName,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            modifier = Modifier.padding(4.dp),
            text = elapsedTime,
            style = MaterialTheme.typography.titleMedium
        )
    }
}