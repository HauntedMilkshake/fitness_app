package bg.zahov.app.ui.workout.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import bg.zahov.fitness.app.R

@Preview
@Composable
fun AddTemplateWorkoutTopBar(topBarViewModel: AddTemplateWorkoutTopBarViewModel = hiltViewModel()) {
    AddTemplateWorkoutTopBarContent(
        onSave = { topBarViewModel }
    )
}

@Composable
fun AddTemplateWorkoutTopBarContent(workoutName: String? = null, onSave: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = workoutName ?: stringResource(R.string.new_workout_template),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        Image(
            modifier = Modifier.clickable { onSave() },
            painter = painterResource(R.drawable.ic_plus),
            contentDescription = stringResource(R.string.add_template_button_content_description)
        )

    }
}