package bg.zahov.app.ui.workout.finish

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.app.ui.workout.start.Workout
import bg.zahov.fitness.app.R


@Composable
fun WorkoutFinishScreen(
    finishWorkoutViewModel: WorkoutFinishViewModel = viewModel(),
    onClose: () -> Unit,
) {

}

@Preview
@Composable
fun WorkoutFinishContent(onClose: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        IconButton(onClick = onClose) {
            Icon(painter = painterResource(R.drawable.ic_close), contentDescription = "")
        }
        AnimatedVectorDrawable()
        Workout(
            modifier = TODO(),
            workoutName = TODO(),
            workoutDate = TODO(),
            exercises = TODO(),
            onWorkoutClick = TODO(),
            onWorkoutStart = TODO(),
            onEdit = TODO(),
            onDelete = TODO(),
            onDuplicate = TODO()
        ) 
    }
}

@Composable
fun AnimatedVectorDrawable() {
    Icon(painter = painterResource(R.drawable.ic_star), contentDescription = "")
}