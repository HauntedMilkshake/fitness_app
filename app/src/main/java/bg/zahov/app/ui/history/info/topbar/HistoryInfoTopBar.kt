package bg.zahov.app.ui.history.info.topbar

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.fitness.app.R


@Composable
fun HistoryInfoTopBar(
    viewModel: HistoryInfoTopBarViewModel = viewModel(),
    onBack: () -> Unit,
) {
    HistoryInfoTopBarContent(
        onBack = onBack,
        onDelete = { viewModel.triggerDelete() },
        onSaveWorkoutTemplate = { viewModel.triggerSave() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryInfoTopBarContent(
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onSaveWorkoutTemplate: () -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_back_arrow),
                    contentDescription = ""
                )
            }
        },
        actions = {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings),
                    contentDescription = ""
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.delete)) },
                    onClick = onDelete
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.save_as_workout_template)) },
                    onClick = onSaveWorkoutTemplate
                )
            }
        }
    )
}