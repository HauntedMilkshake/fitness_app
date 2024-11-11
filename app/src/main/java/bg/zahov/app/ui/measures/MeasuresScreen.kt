package bg.zahov.app.ui.measures

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.app.ui.custom.CommonDivider

@Composable
fun MeasuresScreen(navigateInfo: (String) -> Unit, viewModel: MeasuresViewModel = viewModel()) {
    MeasuresContent(
        items = viewModel.getAllMeasures(),
        onClick = {
            viewModel.onMeasurementClick(it)
            navigateInfo(it)
        }
    )
}

@Composable
fun MeasuresContent(
    modifier: Modifier = Modifier,
    items: List<String>,
    onClick: (String) -> Unit
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        items(items) { item ->
            Column(Modifier.clickable { onClick(item) }) {
                MeasureItem(text = item)
                CommonDivider()
            }
        }
    }

}

@Composable
fun MeasureItem(modifier: Modifier = Modifier, text: String) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}