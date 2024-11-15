package bg.zahov.app.ui.exercise.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import bg.zahov.app.data.model.FilterItem
import bg.zahov.fitness.app.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterItem(
    list: List<FilterItem>,
    onItemSelected: (FilterItem) -> Unit
) {
    FlowRow(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
    ) {
        list.forEach { filterWrapper ->
            FilterItemBox(
                filterItem = filterWrapper,
                onItemSelected = onItemSelected
            )
        }
    }
}

@Composable
fun FilterItemBox(
    filterItem: FilterItem,
    onItemSelected: (FilterItem) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .clickable { onItemSelected(filterItem) }
            .background(
                color = if (filterItem.selected) colorResource(R.color.selected)
                else colorResource(R.color.unselected_filter),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = filterItem.name, color = Color.White)
    }
}
