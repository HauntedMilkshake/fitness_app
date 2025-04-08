package bg.zahov.app.ui.exercise.add

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.FilterItem
import bg.zahov.app.ui.custom.CommonTextField
import bg.zahov.app.ui.theme.FitnessTheme
import bg.zahov.fitness.app.R

@Composable
fun AddExerciseScreen(navigate: () -> Unit, viewModel: AddExerciseViewModel = hiltViewModel()) {
    val uiState by viewModel.addExerciseData.collectAsStateWithLifecycle()
    val uiDialogState by viewModel.addDialogExerciseData.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.navigateBack) {
        if (uiState.navigateBack)
            navigate()
    }

    AddExerciseContent(
        name = uiState.name,
        category = uiState.selectedCategory,
        bodyPart = uiState.selectedBodyPart,
        dialogFilters = uiDialogState.toShow,
        onNameChange = { viewModel.onNameChange(it) },
        buttonEnabled = uiState.isButtonAvailable,
        showDialogCategory = { viewModel.showCategoryDialog() },
        showDialogBodyPart = { viewModel.showBodyPartDialog() },
        onConfirm = { viewModel.addExercise() },
        showDialog = uiDialogState.showDialog,
        onFilterChange = { viewModel.onFilterChange(it) },
        onDismiss = { viewModel.hideDialog() },
    )
}

@Composable
fun AddExerciseContent(
    modifier: Modifier = Modifier,
    name: String,
    buttonEnabled: Boolean = true,
    category: Category?,
    bodyPart: BodyPart?,
    dialogFilters: List<FilterItem>,
    showDialog: Boolean = false,
    onNameChange: (String) -> Unit,
    showDialogBodyPart: () -> Unit,
    showDialogCategory: () -> Unit,
    onConfirm: () -> Unit,
    onFilterChange: (FilterItem) -> Unit,
    onDismiss: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    FitnessTheme {
        if (showDialog)
            AddExerciseFilterDialog(
                filters = dialogFilters,
                onSelect = { onFilterChange(it) },
                onDismiss = onDismiss
            )

        Column(
            modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CommonTextField(
                text = name,
                label = {
                    Text(
                        stringResource(R.string.add_name_hint),
                        color = MaterialTheme.colorScheme.secondary
                    )
                },
                onTextChange = { onNameChange(it) },
                testTagString = "Add Name"
            )
            Row(
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { showDialogCategory() }
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.category),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = category?.name ?: "",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            Row(
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { showDialogBodyPart() }
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.body_part),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = bodyPart?.name ?: "",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            ConfirmButton(onConfirm = onConfirm, enabled = buttonEnabled)
        }
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
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.background
            )
        ) {
            Image(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = stringResource(R.string.confirm)
            )

        }
    }
}