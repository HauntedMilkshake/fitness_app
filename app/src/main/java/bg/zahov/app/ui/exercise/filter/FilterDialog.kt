package bg.zahov.app.ui.exercise.filter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
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
            color = colorResource(R.color.background),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.select_filter),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = colorResource(R.color.white)
                )

                Text(
                    text = stringResource(R.string.body_part),
                    style = MaterialTheme.typography.headlineMedium,
                    color = colorResource(R.color.white)
                )
                FilterItem(
                    list = uiState.list.filter { it.filter is Filter.BodyPartFilter },
                    onItemSelected = { filterViewModel.onFilterClicked(it) })
                Text(
                    text = stringResource(R.string.category),
                    style = MaterialTheme.typography.headlineMedium,
                    color = colorResource(R.color.white)
                )
                FilterItem(
                    list = uiState.list.filter { it.filter is Filter.CategoryFilter },
                    onItemSelected = { filterViewModel.onFilterClicked(it) })
            }
        }
    }
}