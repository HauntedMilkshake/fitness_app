package bg.zahov.app.ui.exercise

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.app.data.model.FilterWrapper
import bg.zahov.app.ui.exercise.filter.FilterDialog
import bg.zahov.fitness.app.R

@Composable
fun ExercisesScreen(viewModel: ExerciseViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.showDialog) {
        FilterDialog(onDismiss = {
            viewModel.updateShowDialog(false)
        })
    }

    ExercisesContent(
        filterItems = uiState.filters,
        exerciseItems = uiState.exercises,
        removeFilter = { viewModel.removeFilter(it) },
        clickExercise = { viewModel.setClickedExercise(it.name) },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExercisesContent(
    filterItems: List<FilterWrapper>,
    exerciseItems: List<ExercisesWrapper>,
    removeFilter: (FilterWrapper) -> Unit,
    clickExercise: (ExercisesWrapper) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        FlowRow(
            modifier = Modifier.padding(16.dp),
        ) {
            filterItems.forEach { item ->
                Card(modifier = Modifier.padding(horizontal = 8.dp),
                    colors = CardColors(
                        contentColor = Color.White,
                        containerColor = colorResource(R.color.selected),
                        disabledContentColor = Color.White,
                        disabledContainerColor = colorResource(R.color.unselected_filter)
                    ),
                    onClick = { removeFilter(item) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = item.name, maxLines = 1)
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
        }
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(exerciseItems) {
                Row(modifier = Modifier
                    .padding(12.dp)
                    .clickable { clickExercise(it) }) {
                    Image(
                        painter = painterResource(it.bodyPart.image),
                        contentDescription = it.bodyPart.body,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(text = it.name)
                        Text(text = it.bodyPart.body)
                        Text(text = it.category.key)
                    }
                }
            }
        }
    }
}