package bg.zahov.app.ui.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun <T> RadioSettingsDialog(
    title: String,
    items: List<T>,
    onDismissRequest: () -> Unit,
    selected: String,
    setSelected: (selected: T) -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.TopCenter),
                textAlign = TextAlign.Center,
            )
            items.forEach { item ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (item.toString() == selected),
                            onClick = { setSelected(item) }
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = item.toString() == selected,
                        onClick = { setSelected(item) })
                    Text(text = item.toString(), modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}