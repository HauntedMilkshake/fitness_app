package bg.zahov.app.ui.exercise.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.ui.custom.CommonTextField
import bg.zahov.fitness.app.R

@Composable
fun AddExerciseScreen(viewModel: AddExerciseViewModel = viewModel()) {
    val uiState by viewModel.newExerciseData.collectAsStateWithLifecycle()

    if (uiState.showDialogCategory) {
        AddExerciseFilterDialog(,selected = uiState.category,onSelect = viewModel.onFilterChange(it))
    }
    if (uiState.showDialogBodyPart) {
        AddExerciseFilterDialog(,selected = uiState.bodyPart,onSelect = viewModel.onFilterChange(it))
    }
    AddExerciseContent(
        name = uiState.name,
        category = uiState.category,
        bodyPart = uiState.bodyPart,
        onNameChange = { viewModel.onNameChange(it) })
}

@Composable
fun AddExerciseContent(
    name: String,
    category: Category?,
    bodyPart: BodyPart?,
    onNameChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.background(color = colorResource(R.color.background)),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CommonTextField(
            text = name,
            label = { Text(stringResource(R.string.add_name_hint)) },
            onTextChange = { onNameChange(it) },
        )

        Text(modifier = Modifier.clickable { }, text = stringResource(R.string.category))
        Text(text = category?.name?:"")
        Text(modifier = Modifier.clickable { }, text = stringResource(R.string.body_part))
        Text(text = bodyPart?.name?:"")

    }
}