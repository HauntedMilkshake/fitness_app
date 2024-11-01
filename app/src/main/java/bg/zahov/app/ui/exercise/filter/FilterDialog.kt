package bg.zahov.app.ui.exercise.filter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.app.data.model.Filter
import bg.zahov.fitness.app.R

@Composable
fun FilterDialog(filterViewModel: FilterViewModel = viewModel(), onDismiss: () -> Unit) {
    val uiState by filterViewModel.uiState.collectAsStateWithLifecycle()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.select_filter),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = stringResource(R.string.body_part),
                    style = MaterialTheme.typography.headlineMedium
                )
                LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
                    items(uiState.list.filter { it.filter is Filter.BodyPartFilter }) { bodyPartFilter ->
                        FilterItem(
                            filterWrapper = bodyPartFilter,
                            onItemSelected = { filterViewModel.onFilterClicked(it) }
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.category),
                    style = MaterialTheme.typography.headlineMedium
                )
                LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
                    items(uiState.list.filter { it.filter is Filter.CategoryFilter }) { filterWrapper ->
                        FilterItem(
                            filterWrapper = filterWrapper,
                            onItemSelected = { filterViewModel.onFilterClicked(it) }
                        )
                    }
                }
            }
        }
    }
}