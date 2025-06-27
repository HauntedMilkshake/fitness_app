package bg.zahov.app.ui.workout.add

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import bg.zahov.fitness.app.R

@Composable
fun AddTemplateWorkoutTopBar(
    topBarViewModel: AddTemplateWorkoutTopBarViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    AddTemplateWorkoutTopBarContent(
        onBack = onBack,
        onSave = { topBarViewModel.onSave() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTemplateWorkoutTopBarContent(onBack: () -> Unit, onSave: () -> Unit) {
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
            IconButton(
                onClick = onSave,
                modifier = Modifier.semantics { testTag = "TopBarAction" }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = stringResource(R.string.add_template_button_content_description),
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_back_arrow),
                    contentDescription = stringResource(R.string.back_button),
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    )
}