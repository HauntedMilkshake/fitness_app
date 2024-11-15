package bg.zahov.app.ui.exercise.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.state.AddExerciseEventState
import bg.zahov.app.ui.custom.CommonTextField
import bg.zahov.fitness.app.R

@Composable
fun AddExerciseScreen(navigate: () -> Unit, viewModel: AddExerciseViewModel = viewModel()) {
    val uiState by viewModel.addExerciseData.collectAsStateWithLifecycle()

    AddExerciseContent(
        name = uiState.name,
        category = viewModel.getSelectedCategory(),
        bodyPart = viewModel.getSelectedBodyPart(),
        onNameChange = { viewModel.onNameChange(it) },
        buttonEnabled = viewModel.checkButtonAvailability(),
        showDialogCategory = { viewModel.changeEvent(AddExerciseEventState.ShowCategoryFilter) },
        showDialogBodyPart = { viewModel.changeEvent(AddExerciseEventState.ShowBodyPartFilter) },
        onConfirm = { viewModel.addExercise() })

    when (uiState.uiAddExerciseEventState) {
        AddExerciseEventState.HideDialog -> {}
        AddExerciseEventState.ShowBodyPartFilter -> {
            AddExerciseFilterDialog(
                filters = uiState.bodyPartFilters,
                onSelect = { viewModel.onBodyPartFilterChange(it) },
                onDismiss = { viewModel.changeEvent(AddExerciseEventState.HideDialog) })
        }

        AddExerciseEventState.ShowCategoryFilter -> {
            AddExerciseFilterDialog(
                filters = uiState.categoryFilters,
                onSelect = { viewModel.onCategoryFilterChange(it) },
                onDismiss = { viewModel.changeEvent(AddExerciseEventState.HideDialog) })
        }

        AddExerciseEventState.NavigateBack -> {
            LaunchedEffect(Unit) {
                navigate()
                viewModel.changeEvent(AddExerciseEventState.HideDialog)
            }
        }
    }
}

@Composable
fun AddExerciseContent(
    modifier: Modifier = Modifier,
    name: String,
    buttonEnabled: Boolean = true,
    category: Category?,
    bodyPart: BodyPart?,
    onNameChange: (String) -> Unit,
    showDialogBodyPart: () -> Unit,
    showDialogCategory: () -> Unit,
    onConfirm: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier.background(color = colorResource(R.color.background)),
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
                .padding(start = 16.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { showDialogCategory() }
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                color = colorResource(R.color.white),
                text = stringResource(R.string.category),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                color = colorResource(R.color.white),
                text = category?.name ?: "",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        Row(
            modifier = Modifier
                .padding(start = 16.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { showDialogBodyPart() }
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                color = colorResource(R.color.white),
                text = stringResource(R.string.body_part),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                color = colorResource(R.color.white),
                text = bodyPart?.name ?: "",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        ConfirmButton(onConfirm = onConfirm, enabled = buttonEnabled)
    }
}

@Composable
fun ConfirmButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onConfirm: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Button(
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.BottomEnd),
            shape = CircleShape,
            onClick = onConfirm,
            enabled = enabled,
            contentPadding = PaddingValues(0.dp),
            colors = ButtonColors(
                containerColor = if (enabled) colorResource(R.color.text) else colorResource(R.color.disabled_button),
                contentColor = colorResource(R.color.white),
                disabledContentColor = colorResource(R.color.disabled_button),
                disabledContainerColor = colorResource(R.color.disabled_button),
            )
        ) {
            Image(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = stringResource(R.string.confirm)
            )
        }
    }
}