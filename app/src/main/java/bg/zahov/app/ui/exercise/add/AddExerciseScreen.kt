package bg.zahov.app.ui.exercise.add

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
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
fun AddExerciseScreen(navigate: () -> Unit, viewModel: AddExerciseViewModel = viewModel()) {
    val uiState by viewModel.newExerciseData.collectAsStateWithLifecycle()
    LaunchedEffect(uiState) {
        Log.d("AddExerciseScreen", "uiState updated: $uiState")
    }
    AddExerciseContent(
        name = uiState.name,
        category = viewModel.getSelectedCategory(),
        bodyPart = viewModel.getSelectedBodyPart(),
        onNameChange = { viewModel.onNameChange(it) },
        showDialogCategory = { viewModel.changeEvent(AddExerciseViewModel.EventState.ShowCategoryFilter) },
        showDialogBodyPart = { viewModel.changeEvent(AddExerciseViewModel.EventState.ShowBodyPartFilter) })

    when (uiState.uiEventState) {
        AddExerciseViewModel.EventState.HideDialog -> {}
        AddExerciseViewModel.EventState.ShowBodyPartFilter -> {
            AddExerciseFilterDialog(
                filters = uiState.bodyPartFilters,
                onSelect = { viewModel.onBodyPartFilterChange(it) },
                onDismiss = { viewModel.changeEvent(AddExerciseViewModel.EventState.HideDialog) })
        }

        AddExerciseViewModel.EventState.ShowCategoryFilter -> {
            AddExerciseFilterDialog(
                filters = uiState.categoryFilters,
                onSelect = { viewModel.onCategoryFilterChange(it) },
                onDismiss = { viewModel.changeEvent(AddExerciseViewModel.EventState.HideDialog) })
        }

        AddExerciseViewModel.EventState.NavigateBack -> LaunchedEffect(Unit) { navigate() }
    }
}

@Composable
fun AddExerciseContent(
    name: String,
    category: Category?,
    bodyPart: BodyPart?,
    onNameChange: (String) -> Unit,
    showDialogBodyPart: () -> Unit,
    showDialogCategory: () -> Unit,
) {
    Column(
        modifier = Modifier.background(color = colorResource(R.color.background)),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CommonTextField(
            text = name,
            label = { Text(stringResource(R.string.add_name_hint)) },
            onTextChange = { onNameChange(it) },
        )
        Row(
            modifier = Modifier
                .clickable { showDialogCategory() }
                .fillMaxWidth(),
        ) {
            Text(
                color = colorResource(R.color.white),
                text = stringResource(R.string.category)
            )
            Text(text = category?.name ?: "")
        }
        Row(
            modifier = Modifier
                .clickable { showDialogBodyPart() }
                .fillMaxWidth(),

            ) {
            Text(
                color = colorResource(R.color.white),
                text = stringResource(R.string.body_part)
            )
            Text(text = bodyPart?.name ?: "")
        }
    }
}