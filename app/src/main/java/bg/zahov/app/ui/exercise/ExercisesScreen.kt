package bg.zahov.app.ui.exercise

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.app.data.model.FilterWrapper
import bg.zahov.app.ui.exercise.filter.FilterDialog
import bg.zahov.fitness.app.R

@Composable
fun ExercisesScreen(viewModel: ExerciseViewModel = viewModel(), navigateInfo: ()->Unit) {
    val uiState by viewModel.exerciseData.collectAsStateWithLifecycle()

    if (uiState.showDialog) {
        FilterDialog(onDismiss = {
            viewModel.updateShowDialog(false)
        })
    }

    ExercisesContent(
        filterItems = uiState.filters,
        exerciseItems = uiState.exercises,
        removeFilter = { viewModel.removeFilter(it) },
        clickExercise = {
            if (uiState.flag == ExerciseFlag.Default) {
                viewModel.onExerciseClicked()
                navigateInfo()
            } else {
                viewModel.setClickedExercise(it)
            }
        })

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExercisesContent(
    filterItems: List<FilterWrapper>,
    exerciseItems: List<ExercisesWrapper>,
    removeFilter: (FilterWrapper) -> Unit,
    clickExercise: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        FlowRow(
            modifier = Modifier.padding(16.dp),
        ) {
            filterItems.forEach { item ->
                FilterCard(item) { removeFilter(it) }
            }
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(exerciseItems) { index, exercise ->
                    ExerciseCards(exercise) { clickExercise(index) }
                }
            }
        }
    }
//    Button(onClick = {}) { Text(text = stringResource(R.string.confirm)) }
}

@Composable
fun ExerciseCards(exercise: ExercisesWrapper, onClick: (ExercisesWrapper) -> Unit) {
    Row(
        modifier = Modifier
            .padding(12.dp)
            .clickable { onClick(exercise) }
            .background(colorResource(if (exercise.selected) R.color.selected else R.color.background)),
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

@Composable
fun FilterCard(filter: FilterWrapper, onClick: (FilterWrapper) -> Unit) {
    Card(modifier = Modifier.padding(horizontal = 8.dp), colors = CardColors(
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