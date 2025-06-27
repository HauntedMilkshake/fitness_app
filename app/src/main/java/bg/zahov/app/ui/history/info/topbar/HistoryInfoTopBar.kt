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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.hilt.navigation.compose.hiltViewModel
import bg.zahov.fitness.app.R


@Composable
fun HistoryInfoTopBar(
    viewModel: HistoryInfoTopBarViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    HistoryInfoTopBarContent(
        onBack = onBack,
        onDelete = { viewModel.triggerDelete() },
        onSaveWorkoutTemplate = { viewModel.triggerSave() }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
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
            IconButton(onClick = onBack, modifier = Modifier.semantics {
                testTagsAsResourceId = true
                testTag = "HistoryBack"
            }) {
                Icon(
                    painter = painterResource(R.drawable.ic_back_arrow),
                    contentDescription = stringResource(R.string.minimize_icon_content_description)
                )
            }
        },
        actions = {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings),
                    contentDescription = stringResource(R.string.settings_wheel_content_description)
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