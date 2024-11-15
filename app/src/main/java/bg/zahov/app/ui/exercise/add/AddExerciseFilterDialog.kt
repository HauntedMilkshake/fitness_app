package bg.zahov.app.ui.exercise.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import bg.zahov.app.data.model.FilterItem
import bg.zahov.fitness.app.R

@Composable
fun AddExerciseFilterDialog(
    filters: List<FilterItem>,
    onSelect: (FilterItem) -> Unit,
    onDismiss: () -> Unit
) {
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
                    text = stringResource(R.string.filter),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = colorResource(R.color.white)
                )
                LazyColumn {
                    items(filters) { filter ->
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(filter) }) {
                            RadioButton(
                                selected = filter.selected,
                                onClick = { onSelect(filter) },
                                colors = RadioButtonColors(
                                    selectedColor = colorResource(R.color.white),
                                    unselectedColor = colorResource(R.color.background),
                                    disabledSelectedColor = colorResource(R.color.selected),
                                    disabledUnselectedColor = colorResource(R.color.background)
                                )
                            )
                            Text(
                                text = filter.name,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 8.dp),
                                color = colorResource(R.color.white)
                            )
                        }
                    }
                }
            }
        }
    }
}