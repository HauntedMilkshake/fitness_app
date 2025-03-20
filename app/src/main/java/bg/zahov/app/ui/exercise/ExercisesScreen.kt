package bg.zahov.app.ui.exercise

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bg.zahov.app.data.model.FilterItem
import bg.zahov.app.data.model.state.ExerciseData
import bg.zahov.app.data.model.state.ExerciseFlag
import bg.zahov.app.ui.exercise.filter.FilterDialog
import bg.zahov.app.ui.theme.FitnessTheme
import bg.zahov.fitness.app.R

@Composable
fun ExercisesScreen(
    viewModel: ExerciseViewModel = hiltViewModel(),
    navigateInfo: () -> Unit,
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.exerciseData.collectAsStateWithLifecycle()

    ExercisesContent(
        showLoading = uiState.loading,
        filterItems = uiState.filters,
        exerciseItems = uiState.exercisesToShow,
        showButton = uiState.flag != ExerciseFlag.Default,
        removeFilter = { viewModel.removeFilter(it) },
        clickExercise = { viewModel.onExerciseClicked(it) },
        onConfirm = {
            viewModel.confirmSelectedExercises()
        })

    Log.d("test2", uiState.navigateBack.toString())
    when {
        uiState.navigateInfo ->
            LaunchedEffect(Unit) {
                navigateInfo()
                viewModel.resetNavigationState()
            }

        uiState.navigateBack ->
            LaunchedEffect(Unit) {
                navigateBack()
            }

        uiState.showDialog ->
            FilterDialog(onDismiss = {
                viewModel.updateShowDialog(false)
            })
    }
}


@OptIn(ExperimentalLayoutApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ExercisesContent(
    showLoading: Boolean,
    filterItems: List<FilterItem>,
    exerciseItems: List<IndexedValue<ExerciseData>>,
    removeFilter: (FilterItem) -> Unit,
    clickExercise: (Int) -> Unit,
    showButton: Boolean,
    onConfirm: () -> Unit,
) {
    FitnessTheme {
        if (showLoading) {
            CircularProgressIndicator(
                modifier = Modifier,
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            FlowRow(modifier = Modifier.padding(16.dp)) {
                filterItems.forEach { item ->
                    FilterCard(
                        filter = item,
                        modifier = Modifier
                    ) { removeFilter(it) }
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        testTagsAsResourceId = true
                        testTag = "Exercises"
                    }) {
                items(exerciseItems) { exercise ->
                    ExerciseCards(exercise = exercise.value) { clickExercise(exercise.index) }
                }
            }

        }
        if (showButton) {
            ConfirmButton(onConfirm = onConfirm)
        }
    }
}

@Composable
fun ConfirmButton(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Button(
            onClick = onConfirm,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .wrapContentSize(),
            colors = ButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.tertiaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.tertiary,
            )
        ) {
            Image(
                modifier = Modifier.size(40.dp),
                painter = painterResource(R.drawable.ic_check),
                contentDescription = stringResource(R.string.confirm)
            )
        }
    }
}

@Composable
fun ExerciseCards(
    modifier: Modifier = Modifier,
    exercise: ExerciseData,
    onClick: (ExerciseData) -> Unit,
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .clickable { onClick(exercise) },
        shape = RoundedCornerShape(16.dp),
        colors = CardColors(
            containerColor = if (exercise.selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.tertiaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.tertiary
        ),
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier.size(40.dp),
                painter = painterResource(exercise.bodyPart.image),
                contentDescription = exercise.bodyPart.body,
                contentScale = ContentScale.Fit,
            )
            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.headlineLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Text(
                    text = exercise.bodyPart.body,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Text(
                    text = exercise.category.key,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

@Composable
fun FilterCard(
    modifier: Modifier = Modifier,
    filter: FilterItem,
    onClick: (FilterItem) -> Unit,
) {
    Card(modifier = modifier.padding(horizontal = 8.dp), colors = CardColors(
        contentColor = MaterialTheme.colorScheme.primary,
        containerColor = MaterialTheme.colorScheme.onPrimary,
        disabledContentColor = MaterialTheme.colorScheme.secondary,
        disabledContainerColor = MaterialTheme.colorScheme.onSecondary
    ), onClick = { onClick(filter) }) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)
        ) {
            Text(text = filter.name, maxLines = 1, color = MaterialTheme.colorScheme.onSecondary)
            Image(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = stringResource(R.string.remove_filter),
                modifier = Modifier
                    .size(20.dp)
                    .wrapContentHeight()
            )
        }
    }
}