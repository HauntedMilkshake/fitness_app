package bg.zahov.app.ui.exercise.topbar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.fitness.app.R

@Composable
fun TopBarExercise(
    onAddClick: () -> Unit,
    viewModel: TopBarExerciseViewModel = viewModel()
) {
    val uiState by viewModel.exerciseData.collectAsStateWithLifecycle()
    TopBarExerciseContent(
        searchQuery = uiState.searchQuery,
        isSearchActive = uiState.isSearchActive,
        onChangeSearch = { viewModel.changeSearch(it) },
        onChangeIsSearchActive = { viewModel.changeIsSearchActive(it) },
        onChangeIsDialogOpen = { viewModel.changeIsDialogOpen() },
        onAddClick = onAddClick,

        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarExerciseContent(
    searchQuery: String,
    isSearchActive: Boolean,
    onChangeSearch: (String) -> Unit,
    onChangeIsSearchActive: (Boolean) -> Unit,
    onChangeIsDialogOpen: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(modifier = modifier,
        title = {
            if (isSearchActive) {
                TextField(
                    value = searchQuery,
                    onValueChange = onChangeSearch,
                    placeholder = {
                        Text(text = stringResource(R.string.search))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.small,
                    trailingIcon = {
                        IconButton(onClick = {
                            onChangeIsSearchActive(false)
                            onChangeSearch("")
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_close),
                                contentDescription = stringResource(R.string.close_search)
                            )
                        }
                    }
                )
            } else {
                Text(modifier = Modifier.testTag("Title"), text = stringResource(R.string.exercise))
            }
        },
        actions = {
            if (isSearchActive.not()) {
                IconButton(onClick = { onChangeIsSearchActive(true) }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = stringResource(R.string.search)
                    )
                }
            }
            IconButton(onClick = onChangeIsDialogOpen,) {
                Icon(
                    painter = painterResource(R.drawable.ic_filters),
                    contentDescription = stringResource(R.string.select_filter)
                )
            }
            IconButton(onClick = onAddClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = stringResource(R.string.add_exercise)
                )
            }
        }
    )
}