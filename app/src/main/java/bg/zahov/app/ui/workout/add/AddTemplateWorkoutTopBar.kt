package bg.zahov.app.ui.workout.add

import androidx.compose.foundation.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import bg.zahov.fitness.app.R

@Composable
fun AddTemplateWorkoutTopBar(topBarViewModel: AddTemplateWorkoutTopBarViewModel = hiltViewModel()) {
    AddTemplateWorkoutTopBarContent(
        onSave = { topBarViewModel.onSave() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTemplateWorkoutTopBarContent(onSave: () -> Unit = {}) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.new_workout_template),
                fontWeight = FontWeight.Bold,
                maxLines = 1,

                color = MaterialTheme.colorScheme.secondary
            )
        },
        actions = {
            IconButton(onClick = onSave) {
                Image(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = stringResource(R.string.add_template_button_content_description),
                )
            }
        }
    )
}