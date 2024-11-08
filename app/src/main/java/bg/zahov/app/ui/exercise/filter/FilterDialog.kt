package bg.zahov.app.ui.exercise.filter

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.app.data.model.Filter
import bg.zahov.fitness.app.R

@Composable
fun FilterDialog(filterViewModel: FilterViewModel = viewModel(), onDismiss: () -> Unit) {
    val uiState by filterViewModel.filterData.collectAsStateWithLifecycle()
    val interactionSource = remember { MutableInteractionSource() }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = colorResource(R.color.background),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row {
                    Image(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) { onDismiss() },
                        painter = painterResource(R.drawable.ic_back_arrow),
                        contentDescription = "dismiss"
                    )
                    Text(
                        text = stringResource(R.string.select_filter),
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(bottom = 8.dp),
                        color = colorResource(R.color.white)
                    )
                }

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