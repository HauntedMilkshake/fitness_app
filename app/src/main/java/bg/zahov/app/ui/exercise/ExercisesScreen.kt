package bg.zahov.app.ui.exercise

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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.app.data.model.FilterItem
import bg.zahov.app.data.model.state.ExerciseData
import bg.zahov.app.data.model.state.ExerciseFlag
import bg.zahov.app.ui.exercise.filter.FilterDialog
import bg.zahov.fitness.app.R

@Composable
fun ExercisesScreen(
    viewModel: ExerciseViewModel = viewModel(),
    navigateInfo: () -> Unit,
    navigateBack: () -> Unit
) {
    val uiState by viewModel.exerciseData.collectAsStateWithLifecycle()

    if (uiState.showDialog) {
        FilterDialog(onDismiss = {
            viewModel.updateShowDialog(false)
        })
    }
    if (uiState.navigateBack) {
        LaunchedEffect(Unit) {
            navigateBack()
        }
    }
    if (uiState.navigateInfo) {
        LaunchedEffect(Unit) {
            navigateInfo()
        }
    }

    ExercisesContent(
        filterItems = uiState.filters,
        exerciseItems = uiState.exercises,
        showButton = uiState.flag != ExerciseFlag.Default,
        removeFilter = { viewModel.removeFilter(it) },
        clickExercise = { viewModel.onExerciseClicked(it) },
        onConfirm = {
            viewModel.confirmSelectedExercises()
        })
    if (uiState.loading) {
        CircularProgressIndicator(
            modifier = Modifier,
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExercisesContent(
    filterItems: List<FilterItem>,
    exerciseItems: List<ExerciseData>,
    removeFilter: (FilterItem) -> Unit,
    clickExercise: (Int) -> Unit,
    showButton: Boolean,
    onConfirm: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        FlowRow(
            modifier = Modifier.padding(16.dp),
        ) {
            filterItems.forEach { item ->
                FilterCard(
                    filter = item,
                    modifier = Modifier
                ) { removeFilter(it) }
            }
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(exerciseItems) { index, exercise ->
                    if (exercise.toShow) {
                        ExerciseCards(
                            exercise = exercise,
                            modifier = Modifier
                        ) { clickExercise(index) }
                    }
                }
            }
        }
    }
    if (showButton) {
        Box(
            modifier = Modifier
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
                    containerColor = colorResource(R.color.text),
                    contentColor = colorResource(R.color.white),
                    disabledContentColor = colorResource(R.color.disabled_button),
                    disabledContainerColor = colorResource(R.color.disabled_button),
                )
            ) {
                Image(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = "confirm"
                )
            }
        }
    }
}

@Composable
fun ExerciseCards(
    modifier: Modifier = Modifier,
    exercise: ExerciseData,
    onClick: (ExerciseData) -> Unit
) {
    Card(
        modifier = modifier.padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardColors(
            containerColor = colorResource(if (exercise.selected) R.color.selected else R.color.background),
            contentColor = colorResource(R.color.white),
            disabledContentColor = colorResource(R.color.disabled_button),
            disabledContainerColor = colorResource(R.color.less_vibrant_text)
        ),
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .clickable { onClick(exercise) },
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
                    color = Color.White
                )
                Text(
                    text = exercise.bodyPart.body,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )
                Text(
                    text = exercise.category.key,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun FilterCard(
    modifier: Modifier = Modifier,
    filter: FilterItem,
    onClick: (FilterItem) -> Unit
) {
    Card(modifier = modifier.padding(horizontal = 8.dp), colors = CardColors(
        contentColor = Color.White,
        containerColor = colorResource(R.color.selected),
        disabledContentColor = Color.White,
        disabledContainerColor = colorResource(R.color.unselected_filter)
    ), onClick = { onClick(filter) }) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)
        ) {
            Text(text = filter.name, maxLines = 1)
            Image(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = "Removes the filter",
                modifier = Modifier
                    .size(20.dp)
                    .wrapContentHeight()
            )
        }
    }
}